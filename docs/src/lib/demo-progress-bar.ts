// Drives the segmented progress bar shared by the auto-cycling isomap demos (see the
// .demo-bar/.demo-segment/.demo-fill rules in src/styles/custom.css). `segments` are the
// `.demo-segment` elements in order; `index` is the one currently filling.
export function setActiveSegment(segments: HTMLElement[], index: number, durationMs: number) {
  segments.forEach((segment, i) => {
    const fill = segment.querySelector<HTMLElement>('.demo-fill');
    if (!fill) return;
    fill.style.animationDuration = `${durationMs}ms`;
    fill.classList.remove('demo-fill--active', 'demo-fill--done');
    if (i < index) {
      fill.classList.add('demo-fill--done');
    } else if (i === index) {
      // Force a reflow so re-adding the class restarts the CSS animation every cycle.
      void fill.offsetWidth;
      fill.classList.add('demo-fill--active');
    }
  });
}
