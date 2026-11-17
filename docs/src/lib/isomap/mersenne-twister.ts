import MersenneTwister from 'mersenne-twister';

// random_long() matches CPython's random.Random().random() bit-for-bit; random() doesn't (lower precision).
export function seededRandom(c: number, r: number, salt = 0): () => number {
  const seed = (c * 2654435761 + r * 40503 + salt * 97) >>> 0;
  const mt = new MersenneTwister([seed]);
  return () => mt.random_long();
}
