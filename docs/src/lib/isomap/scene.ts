// Terrain is real-height boxes; depth buffer + back-face culling handle visibility, no lights (unlit materials = flat-shaded SVG look).
import * as THREE from 'three';
import { CSS2DRenderer } from 'three/examples/jsm/renderers/CSS2DRenderer.js';
import { generateTerrain, GRASS_TOP, HEIGHT_UNIT, LANDING_TERRAIN_CONFIG, TILE, WALL_HEIGHT, WATER_DEPTH, type TerrainConfig, type TerrainData } from './terrain';
import { GRASS, GRASS_DARK, HILL_SIDE, HILL_TOP, SAND, STONE_SIDE, STONE_TOP, WATER_DEEP, WATER_SIDE, WATER_TOP, type FaceColors } from './colors';
import { addBrackets, addCacti, addHeart, addInstancedBoxes, addLabel, addTrees, addWaterWalls, columnFromTop, makeGridTexture } from './geometry';

export interface MountOptions {
  /** Half-height of the orthographic view, in world units. Default 46 is tuned for Landing's large layout; smaller configs should pass less. */
  frustumHalfHeight?: number;
}

export function mountIsomapScene(container: HTMLElement, config: TerrainConfig = LANDING_TERRAIN_CONFIG, options: MountOptions = {}): () => void {
  return mountIsomapSceneFromData(container, generateTerrain(config), options);
}

export function mountIsomapSceneFromData(container: HTMLElement, data: TerrainData, options: MountOptions = {}): () => void {
  const scene = new THREE.Scene();
  scene.background = new THREE.Color(GRASS_DARK);

  const frustumHalfHeight = options.frustumHalfHeight ?? 46;
  const camera = new THREE.OrthographicCamera(-1, 1, 1, 1, 0.1, 500);
  const center = new THREE.Vector3((data.ground.minX + data.ground.maxX) / 2, 0, (data.ground.minZ + data.ground.maxZ) / 2);
  // ~15% shallower than a 45° top-down view, closer to the original SVG's flatter angle.
  const cameraDirection = new THREE.Vector3(1, 0.85, 1).normalize();
  camera.position.copy(center).addScaledVector(cameraDirection, 140);
  camera.lookAt(center);

  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  container.appendChild(renderer.domElement);

  const labelRenderer = new CSS2DRenderer();
  labelRenderer.domElement.style.position = 'absolute';
  labelRenderer.domElement.style.inset = '0';
  labelRenderer.domElement.style.pointerEvents = 'none';
  container.appendChild(labelRenderer.domElement);

  const gridTexture = buildTerrain(scene, data);
  const hearts = data.territories.map((t) => {
    const heart = addHeart(scene, t);
    addBrackets(scene, t);
    addLabel(scene, t);
    return heart;
  });

  function resize() {
    const width = container.clientWidth || 1;
    const height = container.clientHeight || 1;
    const aspect = width / height;
    camera.left = -frustumHalfHeight * aspect;
    camera.right = frustumHalfHeight * aspect;
    camera.top = frustumHalfHeight;
    camera.bottom = -frustumHalfHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height);
    labelRenderer.setSize(width, height);
  }
  resize();
  const resizeObserver = new ResizeObserver(resize);
  resizeObserver.observe(container);

  const stopAnimating = animateHearts(hearts, renderer, labelRenderer, scene, camera);

  return () => {
    stopAnimating();
    resizeObserver.disconnect();
    disposeMeshes(scene);
    gridTexture.dispose();
    renderer.dispose();
    container.removeChild(renderer.domElement);
    container.removeChild(labelRenderer.domElement);
  };
}

// renderer.dispose() doesn't free scene geometries/materials/textures — must be done explicitly or a mount/unmount cycle leaks GPU memory.
function disposeMeshes(scene: THREE.Scene) {
  scene.traverse((object) => {
    if (!(object instanceof THREE.Mesh)) return;
    object.geometry.dispose();
    for (const material of Array.isArray(object.material) ? object.material : [object.material]) {
      material.dispose();
    }
  });
}

function buildTerrain(scene: THREE.Scene, data: TerrainData): THREE.Texture {
  const gridTexture = makeGridTexture();
  const flatColumn = columnFromTop(GRASS_TOP);
  addInstancedBoxes(scene, data.flatTiles, TILE, flatColumn.height, TILE, flatColumn.centerY, GRASS, gridTexture);
  addInstancedBoxes(scene, data.sandTiles, TILE, flatColumn.height, TILE, flatColumn.centerY, SAND, gridTexture);

  const waterTop = GRASS_TOP - WATER_DEPTH;
  const waterColumn = columnFromTop(waterTop);
  const deepWater = data.waterTiles.filter((w) => w.deep);
  const shallowWater = data.waterTiles.filter((w) => !w.deep);
  addInstancedBoxes(scene, shallowWater, TILE, waterColumn.height, TILE, waterColumn.centerY, { top: WATER_TOP, ...WATER_SIDE }, gridTexture);
  addInstancedBoxes(scene, deepWater, TILE, waterColumn.height, TILE, waterColumn.centerY, { top: WATER_DEEP, ...WATER_SIDE }, gridTexture);
  addWaterWalls(scene, data.waterWalls, GRASS_TOP, waterTop);

  // Grouped by height + checkerboard parity so each combination is one instanced mesh, not one box per tile.
  const heightGroups = groupBy(data.heightTiles, (t) => `${t.height}:${t.parity}`);
  for (const tiles of heightGroups.values()) {
    const { height, parity } = tiles[0];
    const column = columnFromTop(GRASS_TOP + height * HEIGHT_UNIT);
    const faces: FaceColors = { top: HILL_TOP[height][parity], ...HILL_SIDE[height] };
    addInstancedBoxes(scene, tiles, TILE, column.height, TILE, column.centerY, faces, gridTexture);
  }

  const wallColumn = columnFromTop(WALL_HEIGHT);
  const wallsByParity = groupBy(data.wallTiles, (t) => t.parity);
  for (const [parity, tiles] of wallsByParity) {
    const faces: FaceColors = { top: STONE_TOP[parity], ...STONE_SIDE };
    addInstancedBoxes(scene, tiles, TILE, wallColumn.height, TILE, wallColumn.centerY, faces, gridTexture);
  }

  addTrees(scene, data.trees);
  addCacti(scene, data.cacti);
  return gridTexture;
}

function groupBy<T, K>(items: T[], key: (item: T) => K): Map<K, T[]> {
  const groups = new Map<K, T[]>();
  for (const item of items) {
    const k = key(item);
    if (!groups.has(k)) groups.set(k, []);
    groups.get(k)!.push(item);
  }
  return groups;
}

function animateHearts(
  hearts: THREE.Group[],
  renderer: THREE.WebGLRenderer,
  labelRenderer: CSS2DRenderer,
  scene: THREE.Scene,
  camera: THREE.Camera,
): () => void {
  // Capped well below display refresh rate — only the heart spin animates, nothing to gain from matching it.
  const fps = 16;
  const frameInterval = 1000 / fps;
  const spinRadiansPerSecond = 1.188; // matches the original spin speed at 60fps
  const spinPerFrame = spinRadiansPerSecond / fps;

  let lastFrameTime = 0;
  function tick(now: number) {
    if (now - lastFrameTime < frameInterval) return;
    lastFrameTime = now;
    for (const heart of hearts) heart.rotation.y += spinPerFrame;
    renderer.render(scene, camera);
    labelRenderer.render(scene, camera);
  }
  renderer.setAnimationLoop(tick);

  return () => renderer.setAnimationLoop(null);
}
