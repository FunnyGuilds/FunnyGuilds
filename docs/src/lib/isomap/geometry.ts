import * as THREE from 'three';
import { CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js';
import { TILE, WALL_HEIGHT, type Territory } from './terrain';
import { BRAND, CANOPY, HEART_RED, WATER_SIDE, type FaceColors } from './colors';

// Shared column bottom: a height difference between neighbors exposes the right wall face for free.
export const BEDROCK = -6;

export function columnFromTop(topFace: number) {
  return { height: topFace - BEDROCK, centerY: (topFace + BEDROCK) / 2 };
}

export function makeGridTexture(): THREE.CanvasTexture {
  const size = 64;
  const canvas = document.createElement('canvas');
  canvas.width = canvas.height = size;
  const ctx = canvas.getContext('2d')!;
  ctx.fillStyle = '#ffffff';
  ctx.fillRect(0, 0, size, size);
  ctx.strokeStyle = 'rgba(74,70,64,0.3)';
  ctx.lineWidth = 1.5;
  ctx.strokeRect(0.75, 0.75, size - 1.5, size - 1.5);
  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
  texture.anisotropy = 4;
  return texture;
}

// Box material slots are +x,-x,+y,-y,+z,-z; camera only sees +x/+z, so -x/-y/-z reuse them.
function boxMaterials(faces: FaceColors, map?: THREE.Texture) {
  const material = (color: number) => new THREE.MeshBasicMaterial({ color, ...(map ? { map } : {}) });
  const right = material(faces.right);
  const top = material(faces.top);
  const left = material(faces.left);
  return [right, right, top, top, left, left];
}

export function addInstancedBoxes(
  scene: THREE.Scene,
  tiles: { x: number; z: number }[],
  width: number,
  height: number,
  depth: number,
  centerY: number,
  faces: FaceColors,
  map?: THREE.Texture,
) {
  if (tiles.length === 0) return;
  const mesh = new THREE.InstancedMesh(new THREE.BoxGeometry(width, height, depth), boxMaterials(faces, map), tiles.length);
  const matrix = new THREE.Matrix4();
  tiles.forEach((tile, i) => {
    matrix.makeTranslation(tile.x, centerY, tile.z);
    mesh.setMatrixAt(i, matrix);
  });
  mesh.instanceMatrix.needsUpdate = true;
  scene.add(mesh);
}

// Thin quad at the water/land boundary — without it the dry neighbor's face wins the shared edge and no blue shows.
export function addWaterWalls(scene: THREE.Scene, walls: { x: number; z: number; axis: 'x' | 'z' }[], topY: number, bottomY: number) {
  const height = topY - bottomY;
  const y = (topY + bottomY) / 2;
  const scale = new THREE.Vector3(1, 1, 1);

  function build(color: number, rotationY: number, tiles: { x: number; z: number }[]) {
    if (tiles.length === 0) return;
    // polygonOffset (not nudged vertices) avoids z-fighting — vertex nudging broke corners where two axes must meet exactly.
    const material = new THREE.MeshBasicMaterial({ color, polygonOffset: true, polygonOffsetFactor: -4, polygonOffsetUnits: -4 });
    const mesh = new THREE.InstancedMesh(new THREE.PlaneGeometry(TILE, height), material, tiles.length);
    const rotation = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0, 1, 0), rotationY);
    const matrix = new THREE.Matrix4();
    tiles.forEach((tile, i) => {
      // Rotation baked per-instance, not on the mesh — a mesh-level rotation would spin every instance's position around the origin too.
      matrix.compose(new THREE.Vector3(tile.x, y, tile.z), rotation, scale);
      mesh.setMatrixAt(i, matrix);
    });
    mesh.instanceMatrix.needsUpdate = true;
    scene.add(mesh);
  }

  build(WATER_SIDE.right, Math.PI / 2, walls.filter((w) => w.axis === 'x'));
  build(WATER_SIDE.left, 0, walls.filter((w) => w.axis === 'z'));
}

export function addTrees(scene: THREE.Scene, trees: { x: number; z: number; topY: number }[]) {
  if (trees.length === 0) return;
  const trunkHeight = 0.9;
  const canopySize = 1.4;
  const trunk = new THREE.InstancedMesh(new THREE.BoxGeometry(0.35, trunkHeight, 0.35), new THREE.MeshBasicMaterial({ color: 0x6b4a2b }), trees.length);
  const canopy = new THREE.InstancedMesh(new THREE.BoxGeometry(canopySize, canopySize, canopySize), boxMaterials(CANOPY), trees.length);
  const matrix = new THREE.Matrix4();
  trees.forEach((tree, i) => {
    matrix.makeTranslation(tree.x, tree.topY + trunkHeight / 2, tree.z);
    trunk.setMatrixAt(i, matrix);
    matrix.makeTranslation(tree.x, tree.topY + trunkHeight + canopySize / 2 - 0.2, tree.z);
    canopy.setMatrixAt(i, matrix);
  });
  trunk.instanceMatrix.needsUpdate = true;
  canopy.instanceMatrix.needsUpdate = true;
  scene.add(trunk, canopy);
}

export function addCacti(scene: THREE.Scene, cacti: { x: number; z: number; topY: number }[]) {
  if (cacti.length === 0) return;
  const height = 1.3;
  const mesh = new THREE.InstancedMesh(new THREE.BoxGeometry(0.3, height, 0.3), new THREE.MeshBasicMaterial({ color: 0x4a8a3f }), cacti.length);
  const matrix = new THREE.Matrix4();
  cacti.forEach((cactus, i) => {
    matrix.makeTranslation(cactus.x, cactus.topY + height / 2, cactus.z);
    mesh.setMatrixAt(i, matrix);
  });
  mesh.instanceMatrix.needsUpdate = true;
  scene.add(mesh);
}

const HEART_ROWS = ['0110110', '1111111', '1111111', '0111110', '0011100', '0001000'];

export function addHeart(scene: THREE.Scene, territory: Territory): THREE.Group {
  const group = new THREE.Group();
  const rows = HEART_ROWS.length;
  const cols = HEART_ROWS[0].length;
  const pixel = 0.24;
  const cells: [number, number][] = [];
  HEART_ROWS.forEach((row, ri) => {
    [...row].forEach((cell, ci) => {
      if (cell === '1') cells.push([ci - (cols - 1) / 2, ri - (rows - 1) / 2]);
    });
  });

  const mesh = new THREE.InstancedMesh(new THREE.BoxGeometry(pixel, pixel, pixel), new THREE.MeshBasicMaterial({ color: HEART_RED }), cells.length);
  const matrix = new THREE.Matrix4();
  cells.forEach(([cx, cy], i) => {
    matrix.makeTranslation(cx * pixel, 1.6 - cy * pixel, 0);
    mesh.setMatrixAt(i, matrix);
  });
  mesh.instanceMatrix.needsUpdate = true;

  group.add(mesh);
  group.rotation.y = Math.PI / 4;
  group.position.set(territory.cx, 0, territory.cz);
  scene.add(group);
  return group;
}

export function addBrackets(scene: THREE.Scene, territory: Territory) {
  const x0 = (territory.c0 - 1) * TILE;
  const x1 = (territory.c0 + territory.size + 1) * TILE;
  const z0 = (territory.r0 - 1) * TILE;
  const z1 = (territory.r0 + territory.size + 1) * TILE;
  const y = WALL_HEIGHT + 0.05;
  const armLength = 2.2;
  const armWidth = 0.4;
  const armThickness = 0.1;

  const corners: [number, number, number, number][] = [
    [x0, z0, 1, 1],
    [x1, z0, -1, 1],
    [x1, z1, -1, -1],
    [x0, z1, 1, -1],
  ];
  const colors = [BRAND.coral, BRAND.green, BRAND.blue, BRAND.gold];

  corners.forEach(([cx, cz, sx, sz], i) => {
    const material = new THREE.MeshBasicMaterial({ color: colors[i] });
    const armX = new THREE.Mesh(new THREE.BoxGeometry(armLength, armThickness, armWidth), material);
    armX.position.set(cx + (sx * armLength) / 2, y, cz);
    scene.add(armX);
    const armZ = new THREE.Mesh(new THREE.BoxGeometry(armWidth, armThickness, armLength), material);
    armZ.position.set(cx, y, cz + (sz * armLength) / 2);
    scene.add(armZ);
  });
}

export function addLabel(scene: THREE.Scene, territory: Territory) {
  const el = document.createElement('div');
  el.textContent = territory.name;
  Object.assign(el.style, {
    fontFamily: "'IBM Plex Sans', sans-serif",
    fontWeight: '700',
    fontSize: '16px',
    letterSpacing: '-0.01em',
    color: '#14171a',
    whiteSpace: 'nowrap',
  });
  // white outline behind the text so it reads over both light and dark terrain
  el.style.setProperty('-webkit-text-stroke', '3px #fff');
  el.style.setProperty('paint-order', 'stroke');
  const label = new CSS2DObject(el);
  label.position.set(territory.cx, WALL_HEIGHT + 2.4, territory.cz);
  scene.add(label);
}
