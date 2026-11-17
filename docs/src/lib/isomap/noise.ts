import { MathUtils } from 'three';
import { seededRandom } from './mersenne-twister';

// Cached since neighboring tiles share the same noise-grid corners.
const cache = new Map<string, number>();

function noiseAt(gx: number, gy: number, salt: number): number {
  const key = `${salt}:${gx},${gy}`;
  let value = cache.get(key);
  if (value === undefined) {
    value = seededRandom(gx, gy, salt)() * 2 - 1;
    cache.set(key, value);
  }
  return value;
}

export function valueNoise(x: number, y: number, cellSize: number, salt: number): number {
  const gx = Math.floor(x / cellSize);
  const gy = Math.floor(y / cellSize);
  const tx = MathUtils.smoothstep(x / cellSize - gx, 0, 1);
  const ty = MathUtils.smoothstep(y / cellSize - gy, 0, 1);

  const topLeft = noiseAt(gx, gy, salt);
  const topRight = noiseAt(gx + 1, gy, salt);
  const bottomLeft = noiseAt(gx, gy + 1, salt);
  const bottomRight = noiseAt(gx + 1, gy + 1, salt);

  const top = topLeft + (topRight - topLeft) * tx;
  const bottom = bottomLeft + (bottomRight - bottomLeft) * tx;
  return top + (bottom - top) * ty;
}
