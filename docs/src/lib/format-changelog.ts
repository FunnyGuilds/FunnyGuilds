// Tiny GitHub-flavored-markdown-ish renderer for release changelog bodies. Not a general
// markdown engine - just the subset these changelogs actually use (headers, nested bullet
// lists, blockquotes, bold/italic/code, links), so we don't need a markdown dependency for
// static content that never needs to handle arbitrary user input.

function renderInline(text: string): string {
  const html = text
    // Issue/PR references (GH-1234) - link before anything else so the generated <a> tags
    // below can't accidentally swallow or re-wrap them.
    .replace(/\bGH-(\d+)\b/g, '<a href="https://github.com/FunnyGuilds/FunnyGuilds/issues/$1" target="_blank" rel="noopener">GH-$1</a>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
  // Old releases sometimes embed raw <b>/<i> HTML with a typo'd unclosed or stray tag - balance
  // per line/list-item so a typo can only ever affect the one line it's on, not bleed into
  // every list item after it (or, unbalanced enough times over, off the end of the page).
  return balanceInlineTags(html);
}

// Every changelog carries its own "Pobierz"/"Download" section (jar mirrors, Maven
// coordinates, CI links) and usually a "Discord:" section (an invite link) - both redundant on
// this page, since the site already has its own download UI and its own Discord widget, so
// both are dropped rather than rendered. Modern releases write these as a bold pseudo-heading
// paragraph (**Pobierz:**/**Discord:**) followed by a list; old download links (pre-2017) are
// just a single inline "<b>Download</b>: <a>...</a>" line with no section structure at all.
function stripRedundantSections(markdown: string): string {
  const lines = markdown.split('\n');
  const out: string[] = [];
  let skipping = false;

  for (const line of lines) {
    const trimmed = line.trim();
    const pseudoHeading = trimmed.match(/^\*\*([^*]+?)\*\*:?\s*$/);
    const realHeading = trimmed.match(/^#{1,6}\s+(.+?):?\s*$/);
    const headingText = pseudoHeading?.[1] ?? realHeading?.[1];

    if (headingText !== undefined) {
      // The colon usually sits inside the bold markers (**Pobierz:**), not after them.
      skipping = /^(pobierz|download|discord):?$/i.test(headingText.trim());
      if (skipping) continue;
    }
    if (skipping) continue;
    if (/<b>[^<]*\b(download|pobierz)\b[^<]*<\/b>/i.test(trimmed)) continue;
    if (/^(download|pobierz)\b/i.test(trimmed)) continue;

    out.push(line);
  }

  return out.join('\n');
}

function listIndent(line: string): number {
  const match = line.match(/^(\s*)/);
  return Math.floor((match ? match[1].length : 0) / 2);
}

function escapeHtml(text: string): string {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// Old releases (pre-2017) sometimes embed raw <b>/<i> HTML directly in their changelog text,
// and a few of those have a typo'd unclosed or stray tag (e.g. 1.4.8's "<i>UUID gildii" never
// gets a closing tag; 3.9 has a stray "</i>" with no opener at all). Since we don't escape that
// raw HTML (it's trusted content from our own GitHub releases, not arbitrary input), an unclosed
// tag would otherwise stay "open" for the rest of the document as far as the browser's parser is
// concerned - italicizing everything after it on the page, including sections that have nothing
// to do with this changelog. This closes anything still open (and drops orphan closing tags) so
// no single entry's typo can ever escape its own place on the page.
function balanceInlineTags(html: string): string {
  const stack: string[] = [];
  const voidTags = new Set(['br', 'hr', 'img']);

  return (
    html.replace(/<(\/?)(\w+)[^>]*>/g, (fullMatch, closing: string, name: string) => {
      const tag = name.toLowerCase();
      if (voidTags.has(tag)) return fullMatch;
      if (closing) {
        const idx = stack.lastIndexOf(tag);
        if (idx === -1) return ''; // orphan closing tag - drop it rather than emit stray markup
        stack.splice(idx, 1);
        return fullMatch;
      }
      stack.push(tag);
      return fullMatch;
    }) + stack.reverse().map((tag) => `</${tag}>`).join('')
  );
}

export function renderChangelog(markdown: string): string {
  const lines = stripRedundantSections(markdown.replace(/\r\n/g, '\n')).split('\n');
  const out: string[] = [];
  let listDepth = -1;
  let inQuote = false;
  let paragraph: string[] = [];

  const closeLists = () => {
    while (listDepth >= 0) {
      out.push('</ul>');
      listDepth--;
    }
  };
  const closeQuote = () => {
    if (inQuote) {
      out.push('</blockquote>');
      inQuote = false;
    }
  };
  const flushParagraph = () => {
    if (paragraph.length > 0) {
      out.push(`<p>${renderInline(paragraph.join(' '))}</p>`);
      paragraph = [];
    }
  };

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trimEnd();
    const fence = line.match(/^```/);
    const heading = line.match(/^(#{1,6})\s+(.*)$/);
    const listItem = line.match(/^(\s*)[-*]\s+(.*)$/);
    const quote = line.match(/^>\s?(.*)$/);

    if (fence) {
      flushParagraph();
      closeLists();
      closeQuote();
      const codeLines: string[] = [];
      i++;
      while (i < lines.length && !lines[i].trimEnd().match(/^```/)) {
        codeLines.push(lines[i]);
        i++;
      }
      out.push(`<pre><code>${escapeHtml(codeLines.join('\n'))}</code></pre>`);
    } else if (heading) {
      flushParagraph();
      closeLists();
      closeQuote();
      const level = Math.min(heading[1].length + 2, 6); // demote so it nests under the page's own h2
      out.push(`<h${level}>${renderInline(heading[2])}</h${level}>`);
    } else if (listItem) {
      flushParagraph();
      closeQuote();
      const depth = listIndent(listItem[1]);
      while (listDepth < depth) {
        out.push('<ul>');
        listDepth++;
      }
      while (listDepth > depth) {
        out.push('</ul>');
        listDepth--;
      }
      out.push(`<li>${renderInline(listItem[2])}</li>`);
    } else if (quote) {
      flushParagraph();
      closeLists();
      if (!inQuote) {
        out.push('<blockquote>');
        inQuote = true;
      }
      out.push(`<p>${renderInline(quote[1])}</p>`);
    } else if (line.trim() === '') {
      flushParagraph();
      closeLists();
      closeQuote();
    } else {
      paragraph.push(line.trim());
    }
  }

  flushParagraph();
  closeLists();
  closeQuote();

  return out.join('\n');
}
