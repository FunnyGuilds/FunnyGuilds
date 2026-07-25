// REFERENCE COPY — no longer imported anywhere. Kept for history/comparison only.
//
// This was the original hand-rolled 2D SVG isometric renderer: painter's-algorithm depth
// sorting plus manually-derived, per-feature visibility rules (which wall face is visible
// for a raised tile vs. a sunken one, which neighbor needs an occlusion patch repainted
// over a leaking edge, angle-adaptive corner-bracket insets...). Every one of those rules
// was a real, separately-discovered bug during development, because SVG has no depth
// buffer — every visibility question has to be answered by hand instead of by hardware.
// Superseded by a real 3D renderer (src/lib/isomap/) where a depth buffer and back-face
// culling answer all of those questions for free. Left in place
// as a reference for the map's original visual target, and in case any of the fixes here
// (e.g. the Mersenne Twister port, the value-noise terrain) are useful again.
//
// Isometric territory map for the FunnyGuilds landing page.
//
// This is a from-scratch, deterministic procedural generator: a winding river with sand
// banks, noise-based terrain elevation, scattered trees/cacti, four fortified guild
// territories (stone walls + moat), a guild "heart" per territory, and brand-colored
// corner markers. Everything is seeded, so the same output regenerates every build.
//
// Ported from an interactively-designed Python/SVG prototype. A few non-obvious fixes
// are load-bearing — see the comments at each site before "simplifying" them:
//   - WATER_HU (water sink depth) must stay smaller than `b` (one grid row-step), or an
//     isolated water tile visually overshoots into the next row and reads as a floating,
//     disconnected tile instead of a sunken one.
//   - the "near-side grass patch" pass repaints flat grass tiles that sit on the near
//     side of a water tile; grass is otherwise only a static background <pattern>,
//     painted once before everything else, so nothing naturally occludes the sunken
//     water tile's edge bleeding into it.
//   - bracket_at() insets each corner along its own angle bisector by a distance scaled
//     to that corner's half-angle, so the perpendicular clearance from the stone edge is
//     constant everywhere. This rhombus is wider than it is tall (2:1 tile ratio), so its
//     corners are NOT symmetric — top/bottom are wide/obtuse (~127deg), left/right are
///     sharp/acute (~53deg). A fixed (non angle-adaptive) inset distance overshoots at
//     top/bottom and undershoots at left/right.

const A = 24; // tile half-width
const B = 12; // tile half-height (2:1 iso ratio)
const HU = 15; // elevation height unit, for RAISED terrain/walls only
const WATER_HU = 8; // dedicated water depth unit — must stay < B
const HU_DOWN = 8; // dedicated depth unit for sunken (negative-height) terrain — must stay < B

// A raised tile shifts screen-up (away from the viewer, toward already-painted, farther
// tiles), so an HU larger than one row-step (B) just makes it look plausibly taller — the
// overshoot silently gets painted over by whatever draws later. A SUNKEN tile shifts
// screen-down, i.e. toward the viewer / toward tiles that haven't been painted yet, so
// the same size overshoot intrudes into not-yet-drawn territory and reads as visually
// wrong ("above, not below" — the exact same failure mode the water depth had, fixed the
// same way: a dedicated depth unit smaller than B). Always convert a height through this
// helper rather than a bare `h * HU`, so every site (top face, wall ground-reference,
// tree placement) picks the right unit automatically.
function elevOf(h: number): number {
  return h >= 0 ? h * HU : h * HU_DOWN;
}

const VB_W = 2100;
const VB_H = 1380;
const MARGIN = 150;

const TS = 5; // territory size, in tiles
const WALL_H = 2; // wall height, in HU units
const WATER_DEPTH = 1;

const RIVER_C0 = 0;
const RIVER_AMP = 6;
const RIVER_FREQ = 0.1;

const NOISE_CELL = 9.0;

const GRASS = '#63a350';
const GRASS_LINE = '#3f6b30';
const WATER = '#4a93c4';
const WATER_DEEP = '#417fa8';
const WATER_LINE = '#2c5c78';
const WATER_SIDE_A = '#356e91';
const WATER_SIDE_B = '#2a5875';
const SAND = '#dfc384';
const SAND_DARK = '#d2b273';
const SAND_LINE = '#a88950';
const STONE_TOP: [string, string] = ['#8f8a83', '#837e77'];
const STONE_L = '#6d6862';
const STONE_R = '#57534e';

const BRAND_BLUE = '#2f74f7';
const BRAND_GOLD = '#f5a623';
const BRAND_CORAL = '#ff5a5f';
const BRAND_GREEN = '#57b894';

const LEVEL_TOP: Record<number, [string, string]> = {
  [-1]: ['#4f7a48', '#46703f'],
  1: ['#6fae57', '#63a04c'],
  2: ['#8f7a52', '#816e48'],
};
const LEVEL_SIDE_L: Record<number, string> = { [-1]: '#3a5c36', 1: '#6b5232', 2: '#5c4a30' };
const LEVEL_SIDE_R: Record<number, string> = { [-1]: '#2e4a2b', 1: '#54401f', 2: '#463822' };

interface Territory {
  name: string;
  c0: number;
  r0: number;
  rank: string;
}

const TERRITORIES: Territory[] = [
  { name: 'Nordheim', c0: -19, r0: -2, rank: '#1' },
  { name: 'Ashfall', c0: 14, r0: 8, rank: '#2' },
  { name: 'Wren', c0: -15, r0: 26, rank: '#4' },
  { name: 'Ember', c0: 17, r0: 30, rank: '#3' },
];

type Pt = [number, number];

function corner(c: number, r: number): Pt {
  return [(c - r) * A, (c + r) * B];
}

function riverCol(r: number): number {
  return Math.round(RIVER_C0 + RIVER_AMP * Math.sin(r * RIVER_FREQ));
}

// A faithful port of CPython's Mersenne Twister (random.Random), NOT a different PRNG
// that merely "looks similarly random". The original map was designed interactively
// against the Python prototype's exact output; an earlier version of this file used a
// different, simpler generator (mulberry32) seeded the same way, which is deterministic
// and non-periodic but produces completely different numbers per seed than Python does —
// same structure (river path, territory positions, walls — none of which are randomized)
// but every randomized detail (hills, tree/cactus scatter, shading variance) came out
// different, which is why the shipped map didn't match the approved design. Verified
// bit-for-bit identical to `random.Random(seed).random()` / `.uniform(a, b)` for the
// seed formula used here (always a non-negative uint32, so CPython's `init_by_array`
// always runs with a single-word key — the general bignum-splitting path is not needed).
const MT_N = 624;
const MT_M = 397;
const MT_MATRIX_A = 0x9908b0df;
const MT_UPPER_MASK = 0x80000000;
const MT_LOWER_MASK = 0x7fffffff;

class MersenneTwister {
  private mt = new Uint32Array(MT_N);
  private mti = MT_N + 1;

  constructor(seed: number) {
    this.initByArray([seed >>> 0]);
  }

  private initGenrand(s: number) {
    this.mt[0] = s >>> 0;
    for (let i = 1; i < MT_N; i++) {
      const prev = this.mt[i - 1] ^ (this.mt[i - 1] >>> 30);
      this.mt[i] = (Math.imul(1812433253, prev) + i) >>> 0;
    }
    this.mti = MT_N;
  }

  private initByArray(key: number[]) {
    this.initGenrand(19650218);
    let i = 1;
    let j = 0;
    for (let k = Math.max(MT_N, key.length); k; k--) {
      const prev = this.mt[i - 1] ^ (this.mt[i - 1] >>> 30);
      this.mt[i] = ((this.mt[i] ^ Math.imul(prev, 1664525)) + key[j] + j) >>> 0;
      i++;
      j++;
      if (i >= MT_N) {
        this.mt[0] = this.mt[MT_N - 1];
        i = 1;
      }
      if (j >= key.length) j = 0;
    }
    for (let k = MT_N - 1; k; k--) {
      const prev = this.mt[i - 1] ^ (this.mt[i - 1] >>> 30);
      this.mt[i] = ((this.mt[i] ^ Math.imul(prev, 1566083941)) - i) >>> 0;
      i++;
      if (i >= MT_N) {
        this.mt[0] = this.mt[MT_N - 1];
        i = 1;
      }
    }
    this.mt[0] = 0x80000000;
  }

  private genrandUint32(): number {
    const mag01 = [0, MT_MATRIX_A];
    if (this.mti >= MT_N) {
      let kk = 0;
      for (; kk < MT_N - MT_M; kk++) {
        const y = (this.mt[kk] & MT_UPPER_MASK) | (this.mt[kk + 1] & MT_LOWER_MASK);
        this.mt[kk] = this.mt[kk + MT_M] ^ (y >>> 1) ^ mag01[y & 1];
      }
      for (; kk < MT_N - 1; kk++) {
        const y = (this.mt[kk] & MT_UPPER_MASK) | (this.mt[kk + 1] & MT_LOWER_MASK);
        this.mt[kk] = this.mt[kk + (MT_M - MT_N)] ^ (y >>> 1) ^ mag01[y & 1];
      }
      const y = (this.mt[MT_N - 1] & MT_UPPER_MASK) | (this.mt[0] & MT_LOWER_MASK);
      this.mt[MT_N - 1] = this.mt[MT_M - 1] ^ (y >>> 1) ^ mag01[y & 1];
      this.mti = 0;
    }
    let y = this.mt[this.mti++];
    y ^= y >>> 11;
    y ^= (y << 7) & 0x9d2c5680;
    y ^= (y << 15) & 0xefc60000;
    y ^= y >>> 18;
    return y >>> 0;
  }

  random(): number {
    const a = this.genrandUint32() >>> 5;
    const b = this.genrandUint32() >>> 6;
    return (a * 67108864.0 + b) * (1.0 / 9007199254740992.0);
  }
}

// Deterministic per-cell PRNG, seeded the same way regardless of iteration order, so
// terrain/decoration never shifts between builds and never forms a visible periodic
// lattice (the failure mode of a plain modulo-based "randomness").
function hashRand(c: number, r: number, salt = 0): () => number {
  const seed = (c * 2654435761 + r * 40503 + salt * 97) >>> 0;
  const mt = new MersenneTwister(seed);
  return () => mt.random();
}

function fmt(n: number): string {
  return n.toFixed(0);
}

function pts(...ps: Pt[]): string {
  return ps.map(([x, y]) => `${fmt(x)},${fmt(y)}`).join(' ');
}

export function renderIsomap(): { svg: string; stats: Record<string, number> } {
  const out: string[] = [];

  out.push('<defs>');
  out.push(
    `<pattern id="gridPattern" x="${-A}" y="0" width="${2 * A}" height="${2 * B}" patternUnits="userSpaceOnUse">`,
  );
  out.push(`<rect width="${2 * A}" height="${2 * B}" fill="${GRASS}"/>`);
  out.push(
    `<polygon points="${A},0 ${2 * A},${B} ${A},${2 * B} 0,${B}" fill="none" stroke="${GRASS_LINE}" stroke-width="1.2" stroke-opacity="0.26"/>`,
  );
  out.push('</pattern>');
  out.push('</defs>');

  // --- territory bounding boxes + viewBox, centered on the territories themselves (not
  // the river's full sampled path, which is much larger/off-center) ---
  const centers = new Map<
    string,
    { c0: number; r0: number; c1: number; r1: number; rank: string; cx: number; cy: number }
  >();
  const cornersAll: Pt[] = [];
  for (const t of TERRITORIES) {
    const c1 = t.c0 + TS;
    const r1 = t.r0 + TS;
    const cmid = t.c0 + Math.floor(TS / 2);
    const rmid = t.r0 + Math.floor(TS / 2);
    const [cx, cy] = corner(cmid + 0.5, rmid + 0.5);
    centers.set(t.name, { c0: t.c0, r0: t.r0, c1, r1, rank: t.rank, cx, cy });
    for (const [cc, rr] of [
      [t.c0, t.r0],
      [c1, t.r0],
      [t.c0, r1],
      [c1, r1],
    ] as [number, number][]) {
      cornersAll.push(corner(cc, rr));
    }
  }

  const xs = cornersAll.map((p) => p[0]);
  const ys = cornersAll.map((p) => p[1]);
  const bboxCx = (Math.min(...xs) + Math.max(...xs)) / 2;
  const bboxCy = (Math.min(...ys) + Math.max(...ys)) / 2;
  const vbX0 = bboxCx - VB_W / 2;
  const vbY0 = bboxCy - VB_H / 2;
  const fits =
    Math.max(...xs) - Math.min(...xs) <= VB_W && Math.max(...ys) - Math.min(...ys) <= VB_H;
  if (!fits) {
    // Non-fatal: surfaced in stats so a build-time check can catch a future territory
    // layout change that no longer fits the viewBox, instead of silently clipping.
    console.warn('[isomap] territories no longer fit the viewBox — check TERRITORIES layout');
  }

  function toCR(x: number, y: number): [number, number] {
    return [(x / A + y / B) / 2, (y / B - x / A) / 2];
  }
  const crCorners = [
    [vbX0, vbY0],
    [vbX0 + VB_W, vbY0],
    [vbX0, vbY0 + VB_H],
    [vbX0 + VB_W, vbY0 + VB_H],
  ].map(([x, y]) => toCR(x, y));
  // Python used int(x) here (truncate-toward-zero) on both the min and the max, NOT
  // floor(min)/ceil(max) — those disagree by one whenever a bound is negative (e.g.
  // int(-25.7) == -25, but Math.floor(-25.7) == -26), which silently shifts the whole
  // generation grid by a row/column and changes every tile count from here on.
  const cLo = Math.trunc(Math.min(...crCorners.map((p) => p[0]))) - 3;
  const cHi = Math.trunc(Math.max(...crCorners.map((p) => p[0]))) + 3;
  const rMin = Math.trunc(Math.min(...crCorners.map((p) => p[1]))) - 3;
  const rMax = Math.trunc(Math.max(...crCorners.map((p) => p[1]))) + 3;

  out.push(
    `<rect x="${fmt(vbX0 - MARGIN)}" y="${fmt(vbY0 - MARGIN)}" width="${VB_W + 2 * MARGIN}" height="${VB_H + 2 * MARGIN}" fill="url(#gridPattern)"/>`,
  );

  function inAnyTerritoryCore(c: number, r: number): boolean {
    for (const t of centers.values()) {
      if (c >= t.c0 - 2 && c < t.c1 + 2 && r >= t.r0 - 2 && r < t.r1 + 2) return true;
    }
    return false;
  }
  function inAnyWallRing(c: number, r: number): boolean {
    for (const t of centers.values()) {
      const inOuter = c >= t.c0 - 1 && c <= t.c1 && r >= t.r0 - 1 && r <= t.r1;
      const inInner = c >= t.c0 && c < t.c1 && r >= t.r0 && r < t.r1;
      if (inOuter && !inInner) return true;
    }
    return false;
  }
  function inAnyMoatRing(c: number, r: number): boolean {
    for (const t of centers.values()) {
      const outer = c >= t.c0 - 2 && c <= t.c1 + 1 && r >= t.r0 - 2 && r <= t.r1 + 1;
      const wall = c >= t.c0 - 1 && c <= t.c1 && r >= t.r0 - 1 && r <= t.r1;
      if (outer && !wall) return true;
    }
    return false;
  }

  function riverTileType(c: number, r: number): 'water' | 'sand' | null {
    const rc = riverCol(r);
    if (c === rc || c === rc + 1) return 'water';
    if (c === rc - 1 || c === rc + 2) return 'sand';
    return null;
  }
  function riverDist(c: number, r: number): number {
    const rc = riverCol(r);
    return Math.min(Math.abs(c - rc), Math.abs(c - rc - 1));
  }
  function isWater(c: number, r: number): boolean {
    if (riverTileType(c, r) === 'water') return true;
    if (inAnyMoatRing(c, r) && riverDist(c, r) >= 3) return true;
    return false;
  }
  function inAnyTerritory(c: number, r: number): boolean {
    return inAnyTerritoryCore(c, r) || inAnyMoatRing(c, r);
  }

  // --- value noise (smooth, structurally-continuous terrain height — not a rigid zone
  // grid of flat plateaus, which reads as obviously mechanical) ---
  const noiseCache = new Map<string, number>();
  function noiseGrid(gx: number, gy: number): number {
    const key = `${gx},${gy}`;
    let v = noiseCache.get(key);
    if (v === undefined) {
      v = hashRand(gx, gy, 201)() * 2 - 1;
      noiseCache.set(key, v);
    }
    return v;
  }
  function smoothstep(t: number): number {
    return t * t * (3 - 2 * t);
  }
  function valueNoise(x: number, y: number): number {
    const gx0 = Math.floor(x / NOISE_CELL);
    const gy0 = Math.floor(y / NOISE_CELL);
    const tx = x / NOISE_CELL - gx0;
    const ty = y / NOISE_CELL - gy0;
    const sx = smoothstep(tx);
    const sy = smoothstep(ty);
    const v00 = noiseGrid(gx0, gy0);
    const v10 = noiseGrid(gx0 + 1, gy0);
    const v01 = noiseGrid(gx0, gy0 + 1);
    const v11 = noiseGrid(gx0 + 1, gy0 + 1);
    const vx0 = v00 + (v10 - v00) * sx;
    const vx1 = v01 + (v11 - v01) * sx;
    return vx0 + (vx1 - vx0) * sy;
  }
  const heightCache = new Map<string, number>();
  function heightLevel(c: number, r: number): number {
    const key = `${c},${r}`;
    let h = heightCache.get(key);
    if (h !== undefined) return h;
    if (inAnyTerritory(c, r) || riverDist(c, r) < 5) {
      h = 0;
    } else {
      const v = valueNoise(c, r) * 0.9;
      h = Math.max(-1, Math.min(2, Math.round(v)));
    }
    heightCache.set(key, h);
    return h;
  }

  // --- river + moat, sunk with the dedicated (sub-row-step) water depth unit ---
  //
  // The whole terrain block below (water, sand, elevation) is deliberately split into 3
  // GLOBAL phases — every top face, THEN one unified occlusion patch, THEN every wall —
  // rather than each feature drawing itself fully (top + walls together) in one pass.
  // The single-pass version had a real bug: a flat grass tile can simultaneously be (a)
  // one sunken tile's NEAR neighbor, which needs a patch repaint to cover that tile's
  // near-edge spill (grass is only ever the static background <pattern>, painted once, so
  // nothing else occludes the spill unless we explicitly repaint it), AND (b) a DIFFERENT
  // sunken tile's FAR neighbor, which already has a legitimate far-wall drawn onto that
  // same shared edge (this genuinely happens — moat corners are exactly this, where the
  // ring turns and a single grass tile sits at the joint). With walls and patches
  // interleaved per-feature, whichever ran later would silently erase the other. Doing
  // ALL top faces, then ONE combined patch, then ALL walls last, guarantees walls are
  // always painted after anything that could cover them.
  const waterElev = -WATER_DEPTH * WATER_HU;
  const waterSet = new Set<string>();
  let waterCount = 0;
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      if (isWater(c, r)) waterSet.add(`${c},${r}`);
    }
  }
  const hasWater = (c: number, r: number) => waterSet.has(`${c},${r}`);

  // Phase 1a: water top faces only.
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      if (!hasWater(c, r)) continue;
      waterCount++;
      const rnd = hashRand(c, r, 5);
      const fill = rnd() < 0.18 ? WATER_DEEP : WATER;
      const cTop = corner(c, r);
      const cRight = corner(c + 1, r);
      const cBottom = corner(c + 1, r + 1);
      const cLeft = corner(c, r + 1);
      const top: Pt = [cTop[0], cTop[1] - waterElev];
      const right: Pt = [cRight[0], cRight[1] - waterElev];
      const bottom: Pt = [cBottom[0], cBottom[1] - waterElev];
      const left: Pt = [cLeft[0], cLeft[1] - waterElev];
      out.push(`<polygon points="${pts(top, right, bottom, left)}" fill="${fill}" stroke="${WATER_LINE}" stroke-width="1" stroke-opacity="0.2"/>`);
    }
  }

  const sandSet = new Set<string>();
  const cactiCandidates: [number, number][] = [];
  let sandCount = 0;
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      if (riverTileType(c, r) !== 'sand') continue;
      const rnd = hashRand(c, r);
      const fill = rnd() < 0.18 ? SAND_DARK : SAND;
      const top = corner(c, r);
      const right = corner(c + 1, r);
      const bottom = corner(c + 1, r + 1);
      const left = corner(c, r + 1);
      out.push(`<polygon points="${pts(top, right, bottom, left)}" fill="${fill}" stroke="${SAND_LINE}" stroke-width="1.2" stroke-opacity="0.22"/>`);
      cactiCandidates.push([c, r]);
      sandSet.add(`${c},${r}`);
      sandCount++;
    }
  }

  // --- elevation (hills/depressions) ---
  const tilesToDraw: [number, number, number][] = [];
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      const h = heightLevel(c, r);
      if (h !== 0) tilesToDraw.push([c, r, h]);
    }
  }
  tilesToDraw.sort((a, b) => a[0] + a[1] - (b[0] + b[1]) || a[2] - b[2]);

  // Phase 1b: elevation top faces only.
  for (const [c, r, h] of tilesToDraw) {
    const elev = elevOf(h);
    const cTop = corner(c, r);
    const cRight = corner(c + 1, r);
    const cBottom = corner(c + 1, r + 1);
    const cLeft = corner(c, r + 1);
    const top: Pt = [cTop[0], cTop[1] - elev];
    const right: Pt = [cRight[0], cRight[1] - elev];
    const bottom: Pt = [cBottom[0], cBottom[1] - elev];
    const left: Pt = [cLeft[0], cLeft[1] - elev];
    const tp = LEVEL_TOP[h][(c + r) % 2 !== 0 ? 1 : 0];
    out.push(`<polygon points="${pts(top, right, bottom, left)}" fill="${tp}" stroke="${GRASS_LINE}" stroke-width="1" stroke-opacity="0.15"/>`);
  }

  // Phase 2: ONE unified near-side grass-occlusion patch, covering both water's and
  // depressions' near-edge spill (toward r+1/c+1). Raised (h>0) neighbors don't need this
  // — their own near-side wall (phase 3) already draws over that shared edge.
  const grassPatch = new Set<string>();
  for (const key of waterSet) {
    const [c, r] = key.split(',').map(Number);
    for (const [nc, nr] of [
      [c + 1, r],
      [c, r + 1],
    ] as [number, number][]) {
      const nk = `${nc},${nr}`;
      if (waterSet.has(nk) || sandSet.has(nk)) continue;
      if (heightLevel(nc, nr) !== 0) continue;
      grassPatch.add(nk);
    }
  }
  for (const [c, r, h] of tilesToDraw) {
    if (h >= 0) continue;
    for (const [nc, nr] of [
      [c + 1, r],
      [c, r + 1],
    ] as [number, number][]) {
      if (heightLevel(nc, nr) !== 0) continue;
      const nk = `${nc},${nr}`;
      if (waterSet.has(nk) || sandSet.has(nk)) continue;
      grassPatch.add(nk);
    }
  }
  for (const key of grassPatch) {
    const [c, r] = key.split(',').map(Number);
    const top = corner(c, r);
    const right = corner(c + 1, r);
    const bottom = corner(c + 1, r + 1);
    const left = corner(c, r + 1);
    out.push(`<polygon points="${pts(top, right, bottom, left)}" fill="${GRASS}" stroke="${GRASS_LINE}" stroke-width="1.2" stroke-opacity="0.22"/>`);
  }

  // Phase 3a: water far walls — toward r-1/c-1, the pit's genuinely visible interior
  // walls. Drawn last (after the phase-2 patch) so the patch can never erase one.
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      if (!hasWater(c, r)) continue;
      const cTop = corner(c, r);
      const cRight = corner(c + 1, r);
      const cLeft = corner(c, r + 1);
      const top: Pt = [cTop[0], cTop[1] - waterElev];
      const right: Pt = [cRight[0], cRight[1] - waterElev];
      const left: Pt = [cLeft[0], cLeft[1] - waterElev];
      if (!hasWater(c, r - 1)) {
        out.push(`<polygon points="${pts(top, right, cRight, cTop)}" fill="${WATER_SIDE_A}"/>`);
      }
      if (!hasWater(c - 1, r)) {
        out.push(`<polygon points="${pts(left, top, cTop, cLeft)}" fill="${WATER_SIDE_B}"/>`);
      }
    }
  }

  // Phase 3b: elevation walls — RAISED (h>0) tiles get near-side walls (toward r+1/c+1,
  // the tile's own front-facing cliff); SUNKEN (h<0) tiles get far-side walls (toward
  // r-1/c-1), the same "far walls only" rule the water pit uses and for the same reason:
  // you're looking down and across into a depression, so its far interior wall is what's
  // visible, while its near wall is hidden behind the nearer rim. (An earlier attempt drew
  // the depression's wall on the near side, matching the raised-tile convention — a
  // geometrically valid quad, but the visible-wall convention for a hill and a pit are
  // opposite sides, so it read as "looks raised, not sunken" even with the top face
  // correctly positioned below ground.) Drawn last so phase 2's patch can never erase one.
  for (const [c, r, h] of tilesToDraw) {
    const elev = elevOf(h);
    const hRight = heightLevel(c + 1, r);
    const hLeftNb = heightLevel(c, r + 1);
    const cTop = corner(c, r);
    const cRight = corner(c + 1, r);
    const cBottom = corner(c + 1, r + 1);
    const cLeft = corner(c, r + 1);
    const top: Pt = [cTop[0], cTop[1] - elev];
    const right: Pt = [cRight[0], cRight[1] - elev];
    const bottom: Pt = [cBottom[0], cBottom[1] - elev];
    const left: Pt = [cLeft[0], cLeft[1] - elev];
    if (h > hLeftNb) {
      const elevNb = elevOf(hLeftNb);
      const leftG: Pt = [cLeft[0], cLeft[1] - elevNb];
      const bottomG: Pt = [cBottom[0], cBottom[1] - elevNb];
      out.push(`<polygon points="${pts(left, bottom, bottomG, leftG)}" fill="${LEVEL_SIDE_L[h]}"/>`);
    }
    if (h > hRight) {
      const elevNb = elevOf(hRight);
      const rightG: Pt = [cRight[0], cRight[1] - elevNb];
      const bottomG2: Pt = [cBottom[0], cBottom[1] - elevNb];
      out.push(`<polygon points="${pts(bottom, right, rightG, bottomG2)}" fill="${LEVEL_SIDE_R[h]}"/>`);
    }
    const hFarTop = heightLevel(c, r - 1);
    const hFarLeft = heightLevel(c - 1, r);
    if (h < hFarTop) {
      const elevNb = elevOf(hFarTop);
      const topG: Pt = [cTop[0], cTop[1] - elevNb];
      const rightG: Pt = [cRight[0], cRight[1] - elevNb];
      out.push(`<polygon points="${pts(top, right, rightG, topG)}" fill="${LEVEL_SIDE_R[h]}"/>`);
    }
    if (h < hFarLeft) {
      const elevNb = elevOf(hFarLeft);
      const leftG: Pt = [cLeft[0], cLeft[1] - elevNb];
      const topG2: Pt = [cTop[0], cTop[1] - elevNb];
      out.push(`<polygon points="${pts(left, top, topG2, leftG)}" fill="${LEVEL_SIDE_L[h]}"/>`);
    }
  }

  // --- trees ---
  function tree(cx: number, cy: number): string {
    const tHa = 2.6;
    const tH = 9;
    const cHa = 12;
    const cHb = 6;
    const cH = 15;
    const trunk = `<rect x="${fmt(cx - tHa)}" y="${fmt(cy - tH)}" width="${fmt(2 * tHa)}" height="${fmt(tH)}" fill="#6b4a2b"/>`;
    const ccy = cy - tH;
    const top: Pt = [cx, ccy - cH - cHb];
    const right: Pt = [cx + cHa, ccy - cH];
    const bot: Pt = [cx, ccy - cH + cHb];
    const left: Pt = [cx - cHa, ccy - cH];
    const gb: Pt = [cx, ccy + cHb];
    const gl: Pt = [cx - cHa, ccy];
    const gr: Pt = [cx + cHa, ccy];
    const canopy =
      `<polygon points="${pts(left, bot, gb, gl)}" fill="#3f7a30"/>` +
      `<polygon points="${pts(bot, right, gr, gb)}" fill="#2f6323"/>` +
      `<polygon points="${pts(top, right, bot, left)}" fill="#4f9a3d"/>`;
    return trunk + canopy;
  }
  let treeCount = 0;
  for (let r = rMin; r < rMax; r++) {
    for (let c = cLo; c < cHi; c++) {
      if (inAnyTerritory(c, r) || riverDist(c, r) < 4) continue;
      const rnd = hashRand(c, r, 9);
      if (rnd() < 0.06) {
        const [x, y0] = corner(c + 0.5, r + 0.5);
        const y = y0 - elevOf(heightLevel(c, r));
        out.push(tree(x, y));
        treeCount++;
      }
    }
  }

  // --- cacti (sand only, sparse) ---
  function cactus(baseX: number, baseY: number): string {
    const ha = 3.5;
    const h = 16;
    return (
      `<rect x="${fmt(baseX - ha)}" y="${fmt(baseY - h)}" width="${fmt(2 * ha)}" height="${fmt(h)}" fill="#4a8a3f"/>` +
      `<rect x="${fmt(baseX - ha - 4)}" y="${fmt(baseY - h * 0.55)}" width="3.5" height="${fmt(h * 0.32)}" fill="#3f7735"/>` +
      `<rect x="${fmt(baseX + ha + 0.5)}" y="${fmt(baseY - h * 0.7)}" width="3.5" height="${fmt(h * 0.32)}" fill="#5a9a4d"/>`
    );
  }
  let cactiPlaced = 0;
  for (const [c, r] of cactiCandidates) {
    if (inAnyTerritory(c, r)) continue;
    const rnd = hashRand(c * 13 + 1, r * 7 + 1);
    if (rnd() < 0.22) {
      const [baseX, baseY] = corner(c + 0.5, r + 0.5);
      const jx = (rnd() * 2 - 1) * A * 0.16;
      const jy = (rnd() * 2 - 1) * B * 0.16;
      out.push(cactus(baseX + jx, baseY + jy));
      cactiPlaced++;
    }
  }

  // --- territory stone walls ---
  const wallTiles: [number, number][] = [];
  for (const t of centers.values()) {
    for (let r = t.r0 - 1; r < t.r1 + 1; r++) {
      for (let c = t.c0 - 1; c < t.c1 + 1; c++) {
        if (c >= t.c0 && c < t.c1 && r >= t.r0 && r < t.r1) continue;
        wallTiles.push([c, r]);
      }
    }
  }
  wallTiles.sort((a, b) => a[0] + a[1] - (b[0] + b[1]));

  for (const [c, r] of wallTiles) {
    const elev = WALL_H * HU;
    const cTop = corner(c, r);
    const cRight = corner(c + 1, r);
    const cBottom = corner(c + 1, r + 1);
    const cLeft = corner(c, r + 1);
    const top: Pt = [cTop[0], cTop[1] - elev];
    const right: Pt = [cRight[0], cRight[1] - elev];
    const bottom: Pt = [cBottom[0], cBottom[1] - elev];
    const left: Pt = [cLeft[0], cLeft[1] - elev];
    const tp = STONE_TOP[(c + r) % 2 !== 0 ? 1 : 0];
    if (!inAnyWallRing(c, r + 1)) {
      out.push(`<polygon points="${pts(left, bottom, cBottom, cLeft)}" fill="${STONE_L}"/>`);
    }
    if (!inAnyWallRing(c + 1, r)) {
      out.push(`<polygon points="${pts(bottom, right, cRight, cBottom)}" fill="${STONE_R}"/>`);
    }
    out.push(`<polygon points="${pts(top, right, bottom, left)}" fill="${tp}" stroke="#4a4640" stroke-width="1" stroke-opacity="0.3"/>`);
  }

  // --- corner brackets: two INDEPENDENT segments, each lying exactly on its own true
  // edge direction (not a shared pivot inset along the angle bisector). A shared bisector
  // inset keeps a constant *perpendicular* clearance from both edges, which sounds right
  // but isn't: at a wide/obtuse corner the bisector is nearly perpendicular to the edges,
  // so almost the entire inset is a sideways drift off the edge line, not movement along
  // it — the arm ends up parallel to the true edge but visibly offset from it, worse the
  // longer the arm runs. Two segments computed straight from the vertex along each edge's
  // own unit vector can't drift at all; a small gap-from-vertex (comfortably more than
  // half the stroke width) keeps the round line caps from overhanging past the true
  // corner, without needing any angle-dependent math.
  function unit(from: Pt, to: Pt): Pt {
    const dx = to[0] - from[0];
    const dy = to[1] - from[1];
    const l = Math.hypot(dx, dy) || 1;
    return [dx / l, dy / l];
  }
  function bracketAt(vertex: Pt, neighborA: Pt, neighborB: Pt, length: number, col: string): string {
    const gap = 3;
    const [ux1, uy1] = unit(vertex, neighborA);
    const [ux2, uy2] = unit(vertex, neighborB);
    const a1: Pt = [vertex[0] + ux1 * gap, vertex[1] + uy1 * gap];
    const b1: Pt = [vertex[0] + ux1 * (gap + length), vertex[1] + uy1 * (gap + length)];
    const a2: Pt = [vertex[0] + ux2 * gap, vertex[1] + uy2 * gap];
    const b2: Pt = [vertex[0] + ux2 * (gap + length), vertex[1] + uy2 * (gap + length)];
    return `<path d="M${fmt(a1[0])},${fmt(a1[1])} L${fmt(b1[0])},${fmt(b1[1])} M${fmt(a2[0])},${fmt(a2[1])} L${fmt(b2[0])},${fmt(b2[1])}" fill="none" stroke="${col}" stroke-width="5" stroke-linecap="round"/>`;
  }

  function regionOutline(c0: number, r0: number, c1: number, r1: number, elev: number): [Pt, Pt, Pt, Pt] {
    const raw: Pt[] = [corner(c0, r0), corner(c1, r0), corner(c1, r1), corner(c0, r1)];
    return raw.map(([x, y]) => [x, y - elev] as Pt) as [Pt, Pt, Pt, Pt];
  }

  const labelAnchor = new Map<string, { fy: number }>();
  for (const [name, t] of centers) {
    const [pTop, pRight, pBottom, pLeft] = regionOutline(t.c0 - 1, t.r0 - 1, t.c1 + 1, t.r1 + 1, WALL_H * HU);
    const L = 16;
    // pBottom is the corner nearest the viewer (largest c+r -> painted last/frontmost),
    // pTop is farthest. Blue goes on pBottom so it reads as "in front" of the viewer
    // instead of tucked away on the far side, where it read as a glitch against the
    // blue-toned water/moat far-wall nearby. This is a 180-degree rotation of the
    // original top/right/bottom/left assignment (not an arbitrary reassignment), so the
    // diagonal pairing from the real logo (blue opposite coral, gold opposite green) is
    // preserved.
    out.push(bracketAt(pTop, pRight, pLeft, L, BRAND_CORAL));
    out.push(bracketAt(pRight, pTop, pBottom, L, BRAND_GREEN));
    out.push(bracketAt(pBottom, pRight, pLeft, L, BRAND_BLUE));
    out.push(bracketAt(pLeft, pTop, pBottom, L, BRAND_GOLD));
    labelAnchor.set(name, { fy: t.cy - 34 });
  }

  // --- guild hearts (pixel-art, upright "billboard" that spins on its Y axis via CSS) ---
  const HEART_ROWS = ['0110110', '1111111', '1111111', '0111110', '0011100', '0001000'];
  const PS = 4.6;
  const rows = HEART_ROWS.length;
  const cols = HEART_ROWS[0].length;
  const heartPx: Pt[] = [];
  HEART_ROWS.forEach((row, ri) => {
    [...row].forEach((ch, ci) => {
      if (ch === '1') {
        const lx = (ci - (cols - 1) / 2) * PS;
        const ly = (ri - (rows - 1) / 2) * PS;
        heartPx.push([lx, ly]);
      }
    });
  });
  const heartTopLocal = (-(rows - 1) / 2) * PS;

  for (const [, t] of centers) {
    const fy = t.cy - 34;
    const g = [`<g transform="translate(${t.cx.toFixed(1)},${fy.toFixed(1)})"><g class="heart-spin">`];
    for (const [lx, ly] of heartPx) {
      g.push(
        `<rect x="${lx.toFixed(1)}" y="${ly.toFixed(1)}" width="${PS.toFixed(1)}" height="${PS.toFixed(1)}" fill="#e8464d" stroke="#9c2a30" stroke-width="0.4"/>`,
      );
    }
    g.push('</g></g>');
    out.push(g.join(''));
  }

  // --- guild name labels, anchored directly above each heart ---
  for (const [name, t] of centers) {
    const fy = t.cy - 34;
    const titleY = fy + heartTopLocal - 12;
    out.push(
      `<text x="${fmt(t.cx)}" y="${fmt(titleY)}" text-anchor="middle" font-family="Plex Sans, sans-serif" font-weight="700" font-size="22" fill="#14171a" stroke="#fff" stroke-width="5" paint-order="stroke">${name}</text>`,
    );
  }

  const stats = {
    viewBoxX: Math.round(vbX0),
    viewBoxY: Math.round(vbY0),
    fits: fits ? 1 : 0,
    waterTiles: waterCount,
    sandTiles: sandCount,
    grassPatch: grassPatch.size,
    elevatedTiles: tilesToDraw.length,
    wallTiles: wallTiles.length,
    trees: treeCount,
    cacti: cactiPlaced,
  };

  return { svg: out.join('\n'), stats };
}

export const ISOMAP_VIEWBOX = { w: VB_W, h: VB_H };
