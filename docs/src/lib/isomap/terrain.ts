import { MathUtils } from 'three';
import { seededRandom } from './mersenne-twister';
import { valueNoise } from './noise';

export const TILE = 2;
export const HEIGHT_UNIT = 1;
export const WATER_DEPTH = 1.15;
export const WALL_HEIGHT = 2.2;
export const DEFAULT_TERRITORY_SIZE = 5;
export const GRASS_TOP = 0.03;

export interface Territory {
  name: string;
  c0: number;
  r0: number;
  size: number;
  cx: number;
  cz: number;
}

export interface TerritoryInput {
  name: string;
  c0: number;
  r0: number;
  size?: number;
}

export interface TerrainConfig {
  territories: TerritoryInput[];
  /** Draws the river through the generated area. Default true. */
  river?: boolean;
  /** Tiles of wilderness generated beyond the outermost territory. Default 40. */
  margin?: number;
  /**
   * When false, territories still reserve their footprint (so bounds and camera framing stay
   * identical) but get no wall ring, moat or flattened interior — the area generates as plain
   * wilderness, as if nothing had been founded there yet. Default true.
   */
  claimed?: boolean;
}

// Labels are guild tags for FunnyGuilds' top contributors, matching the HUD tags in Landing.astro.
export const LANDING_TERRAIN_CONFIG: TerrainConfig = {
  territories: [
    { name: 'DZK', c0: -15, r0: 26 },
    { name: 'INS', c0: 17, r0: 30 },
    { name: 'P3R', c0: -19, r0: -2 },
    { name: 'KIM', c0: 14, r0: 8 },
  ],
};

export const DEMO_TERRAIN_CONFIG: TerrainConfig = {
  territories: [
    { name: 'Northgate', c0: -6, r0: -6 },
    { name: 'Southgate', c0: -6, r0: 5 },
  ],
  margin: 14,
};

export interface HeightTile {
  x: number;
  z: number;
  height: number;
  parity: 0 | 1;
}
export interface FlatTile {
  x: number;
  z: number;
}
export interface Prop {
  x: number;
  z: number;
  topY: number;
}
export interface WallTile {
  x: number;
  z: number;
  parity: 0 | 1;
}
export interface WaterTile {
  x: number;
  z: number;
  deep: boolean;
}
export interface WaterWall {
  x: number;
  z: number;
  axis: 'x' | 'z';
}

export interface TerrainData {
  /** Mirrors TerrainConfig.claimed — gates the heart/brackets/label in scene.ts. */
  claimed: boolean;
  territories: Territory[];
  flatTiles: FlatTile[];
  waterTiles: WaterTile[];
  waterWalls: WaterWall[];
  sandTiles: FlatTile[];
  heightTiles: HeightTile[];
  wallTiles: WallTile[];
  trees: Prop[];
  cacti: Prop[];
  ground: { minX: number; maxX: number; minZ: number; maxZ: number };
}

const RIVER_COL = 0;
const RIVER_AMPLITUDE = 6;
const RIVER_FREQUENCY = 0.1;
const NOISE_CELL = 9;
const FOREST_CELL = 14;
const FOREST_SALT = 305;
const EDGE_BAND = 8;
const TREE_RATE_MAIN = 0.0384;
const TREE_RATE_EDGE = 0.22;

function toWorld(c: number, r: number): [number, number] {
  return [c * TILE, r * TILE];
}

function territorySize(t: TerritoryInput): number {
  return t.size ?? DEFAULT_TERRITORY_SIZE;
}

export function generateTerrain(config: TerrainConfig): TerrainData {
  const territoryInputs = config.territories;
  const riverEnabled = config.river ?? true;
  const margin = config.margin ?? 40;
  const claimed = config.claimed ?? true;

  function riverColumn(r: number): number {
    return Math.round(RIVER_COL + RIVER_AMPLITUDE * Math.sin(r * RIVER_FREQUENCY));
  }
  function distanceToRiver(c: number, r: number): number {
    if (!riverEnabled) return Infinity;
    const col = riverColumn(r);
    return Math.min(Math.abs(c - col), Math.abs(c - col - 1));
  }
  function riverTileType(c: number, r: number): 'water' | 'sand' | null {
    if (!riverEnabled) return null;
    const col = riverColumn(r);
    if (c === col || c === col + 1) return 'water';
    if (c === col - 1 || c === col + 2) return 'sand';
    return null;
  }

  // Chebyshev distance to a territory's footprint: 0 inside, 1 = wall ring, 2 = moat ring.
  function ringDistance(c: number, r: number, t: TerritoryInput): number {
    const size = territorySize(t);
    const dc = c - MathUtils.clamp(c, t.c0, t.c0 + size - 1);
    const dr = r - MathUtils.clamp(r, t.r0, t.r0 + size - 1);
    return Math.max(Math.abs(dc), Math.abs(dr));
  }
  // Per-territory, not a min-distance across all of them — those disagree when rings overlap.
  function isTerritory(c: number, r: number): boolean {
    return claimed && territoryInputs.some((t) => ringDistance(c, r, t) <= 2);
  }
  function isWallRing(c: number, r: number): boolean {
    return claimed && territoryInputs.some((t) => ringDistance(c, r, t) === 1);
  }
  function isMoatRing(c: number, r: number): boolean {
    return claimed && territoryInputs.some((t) => ringDistance(c, r, t) === 2);
  }
  // Moat only becomes water a few tiles clear of the river, so the two features don't collide.
  function isWater(c: number, r: number): boolean {
    if (riverTileType(c, r) === 'water') return true;
    return isMoatRing(c, r) && distanceToRiver(c, r) >= 3;
  }

  function distanceToNearestTerritory(c: number, r: number): number {
    return Math.min(...territoryInputs.map((t) => ringDistance(c, r, t)));
  }

  // Per-call, not module-level: heights depend on this config's own territory layout.
  const heightCache = new Map<string, number>();
  function heightAt(c: number, r: number): number {
    const key = `${c},${r}`;
    let height = heightCache.get(key);
    if (height !== undefined) return height;
    if (isTerritory(c, r) || distanceToRiver(c, r) < 5) {
      height = 0;
    } else {
      const ramp = MathUtils.clamp(distanceToNearestTerritory(c, r) / 20, 0, 1);
      const scale = 0.9 + 0.4 * ramp;
      const noise = valueNoise(c, r, NOISE_CELL, 201) * scale;
      height = MathUtils.clamp(Math.round(noise), -1, 2);
    }
    heightCache.set(key, height);
    return height;
  }

  function isInForestPatch(c: number, r: number): boolean {
    return valueNoise(c, r, FOREST_CELL, FOREST_SALT) > -0.15;
  }

  const territories: Territory[] = territoryInputs.map((t) => {
    const size = territorySize(t);
    const [cx, cz] = toWorld(t.c0 + Math.floor(size / 2) + 0.5, t.r0 + Math.floor(size / 2) + 0.5);
    return { name: t.name, c0: t.c0, r0: t.r0, size, cx, cz };
  });

  const columns = territories.flatMap((t) => [t.c0 - 2, t.c0 + t.size + 2]);
  const rows = territories.flatMap((t) => [t.r0 - 2, t.r0 + t.size + 2]);
  const colMin = Math.min(...columns) - margin;
  const colMax = Math.max(...columns) + margin;
  const rowMin = Math.min(...rows) - margin;
  const rowMax = Math.max(...rows) + margin;

  const flatTiles: FlatTile[] = [];
  const waterTiles: WaterTile[] = [];
  const waterWalls: WaterWall[] = [];
  const sandTiles: FlatTile[] = [];
  const heightTiles: HeightTile[] = [];
  const wallTiles: WallTile[] = [];
  const trees: Prop[] = [];
  const cacti: Prop[] = [];

  for (let r = rowMin; r < rowMax; r++) {
    for (let c = colMin; c < colMax; c++) {
      const [x, z] = toWorld(c + 0.5, r + 0.5);
      const parity: 0 | 1 = (c + r) % 2 !== 0 ? 1 : 0;

      if (isWater(c, r)) {
        const deep = seededRandom(c, r, 5)() < 0.18;
        waterTiles.push({ x, z, deep });
        // Only -x/-z needs a wall patch; the +x/+z neighbour is closer to the camera and occludes its own side.
        if (!isWater(c - 1, r)) waterWalls.push({ x: c * TILE, z, axis: 'x' });
        if (!isWater(c, r - 1)) waterWalls.push({ x, z: r * TILE, axis: 'z' });
        continue;
      }

      if (riverTileType(c, r) === 'sand') {
        sandTiles.push({ x, z });
        if (!isTerritory(c, r)) {
          const random = seededRandom(c * 13 + 1, r * 7 + 1);
          if (random() < 0.22) {
            const jx = (random() * 2 - 1) * TILE * 0.16;
            const jz = (random() * 2 - 1) * TILE * 0.16;
            cacti.push({ x: x + jx, z: z + jz, topY: GRASS_TOP });
          }
        }
        continue;
      }

      if (isWallRing(c, r)) {
        wallTiles.push({ x, z, parity });
        continue;
      }

      if (isTerritory(c, r)) {
        flatTiles.push({ x, z });
        continue;
      }

      const height = heightAt(c, r);
      if (height !== 0) {
        heightTiles.push({ x, z, height, parity });
      } else {
        flatTiles.push({ x, z });
      }

      if (distanceToRiver(c, r) >= 4) {
        const distanceToEdge = Math.min(c - colMin, colMax - 1 - c, r - rowMin, rowMax - 1 - r);
        const baseRate = distanceToEdge < EDGE_BAND ? TREE_RATE_EDGE : TREE_RATE_MAIN;
        const treeRate = isInForestPatch(c, r) ? baseRate : baseRate * 0.45;
        if (seededRandom(c, r, 9)() < treeRate) trees.push({ x, z, topY: GRASS_TOP + height * HEIGHT_UNIT });
      }
    }
  }

  const groundMargin = TILE * 6;
  return {
    claimed,
    territories,
    flatTiles,
    waterTiles,
    waterWalls,
    sandTiles,
    heightTiles,
    wallTiles,
    trees,
    cacti,
    ground: {
      minX: colMin * TILE - groundMargin,
      maxX: colMax * TILE + groundMargin,
      minZ: rowMin * TILE - groundMargin,
      maxZ: rowMax * TILE + groundMargin,
    },
  };
}
