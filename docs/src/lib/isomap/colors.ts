export interface FaceColors {
  top: number;
  right: number;
  left: number;
}

export const GRASS_DARK = 0x4f8a41;

export const GRASS: FaceColors = { top: 0x63a350, right: 0x3f6b30, left: 0x3f6b30 };
export const SAND: FaceColors = { top: 0xdfc384, right: 0xa88950, left: 0xa88950 };

export const WATER_TOP = 0x4a93c4;
export const WATER_DEEP = 0x417fa8;
export const WATER_SIDE = { right: 0x356e91, left: 0x2a5875 };

export const STONE_TOP: [number, number] = [0x8f8a83, 0x837e77];
export const STONE_SIDE = { right: 0x57534e, left: 0x6d6862 };

export const HILL_TOP: Record<number, [number, number]> = {
  [-1]: [0x4f7a48, 0x46703f],
  1: [0x6fae57, 0x63a04c],
  2: [0x8f7a52, 0x816e48],
};
export const HILL_SIDE: Record<number, { right: number; left: number }> = {
  [-1]: { right: 0x2e4a2b, left: 0x3a5c36 },
  1: { right: 0x54401f, left: 0x6b5232 },
  2: { right: 0x463822, left: 0x5c4a30 },
};

export const CANOPY: FaceColors = { top: 0x4f9a3d, right: 0x2f6323, left: 0x2f6323 };
export const HEART_RED = 0xe8464d;

export const BRAND = {
  blue: 0x2f74f7,
  gold: 0xf5a623,
  coral: 0xff5a5f,
  green: 0x57b894,
};
