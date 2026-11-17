// Tiny markdown renderer for release changelog bodies — just the subset these changelogs use
// (headings, nested lists, blockquotes, code, bold/italic/links), so rendering static, trusted
// content doesn't need a markdown dependency.
const REDUNDANT_HEADING = /^(pobierz|download|discord):?$/i;
const VOID_TAGS = new Set(['br', 'hr', 'img']);

function renderInline(text: string): string {
  const html = text
    // Linked first so the generic [text](url) rule below can't re-wrap the <a> tags it emits.
    .replace(/\bGH-(\d+)\b/g, '<a href="https://github.com/FunnyGuilds/FunnyGuilds/issues/$1" target="_blank" rel="noopener">GH-$1</a>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
  return balanceInlineTags(html);
}

// Every changelog carries its own download section (jar mirrors, Maven coordinates, CI links)
// and usually a Discord invite — both redundant here, since the page has its own download UI
// and Discord widget. Modern releases write them as a bold pseudo-heading followed by a list;
// pre-2017 ones as a bare inline "<b>Download</b>: ..." line with no section structure at all.
function stripRedundantSections(markdown: string): string {
  const out: string[] = [];
  let skipping = false;

  for (const line of markdown.split('\n')) {
    const trimmed = line.trim();
    // The colon usually sits inside the bold markers (**Pobierz:**), not after them.
    const heading = trimmed.match(/^\*\*([^*]+?)\*\*:?\s*$/)?.[1] ?? trimmed.match(/^#{1,6}\s+(.+?):?\s*$/)?.[1];
    if (heading !== undefined) skipping = REDUNDANT_HEADING.test(heading.trim());

    if (skipping) continue;
    if (/<b>[^<]*\b(download|pobierz)\b[^<]*<\/b>/i.test(trimmed)) continue;
    if (/^(download|pobierz)\b/i.test(trimmed)) continue;
    out.push(line);
  }

  return out.join('\n');
}

function escapeHtml(text: string): string {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// Pre-2017 changelogs embed raw <b>/<i> HTML, and a few have a typo'd unclosed or stray tag
// (1.4.8 never closes an <i>; 3.9 has an orphan </i>). That HTML is trusted so it isn't escaped
// — which means an unclosed tag would stay open for the rest of the page as far as the browser
// is concerned. Closing what's still open keeps a typo contained to its own entry.
function balanceInlineTags(html: string): string {
  const open: string[] = [];

  const balanced = html.replace(/<(\/?)(\w+)[^>]*>/g, (tag, closing: string, name: string) => {
    const lower = name.toLowerCase();
    if (VOID_TAGS.has(lower)) return tag;
    if (!closing) {
      open.push(lower);
      return tag;
    }
    const index = open.lastIndexOf(lower);
    if (index === -1) return ''; // orphan closing tag — drop rather than emit stray markup
    open.splice(index, 1);
    return tag;
  });

  return balanced + open.reverse().map((tag) => `</${tag}>`).join('');
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
  const closeAll = () => {
    flushParagraph();
    closeLists();
    closeQuote();
  };

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trimEnd();
    const heading = line.match(/^(#{1,6})\s+(.*)$/);
    const listItem = line.match(/^(\s*)[-*]\s+(.*)$/);
    const quote = line.match(/^>\s?(.*)$/);

    if (line.startsWith('```')) {
      closeAll();
      const code: string[] = [];
      while (++i < lines.length && !lines[i].trimEnd().startsWith('```')) code.push(lines[i]);
      out.push(`<pre><code>${escapeHtml(code.join('\n'))}</code></pre>`);
    } else if (heading) {
      closeAll();
      const level = Math.min(heading[1].length + 2, 6); // demote so it nests under the page's own h2
      out.push(`<h${level}>${renderInline(heading[2])}</h${level}>`);
    } else if (listItem) {
      flushParagraph();
      closeQuote();
      const depth = Math.floor(listItem[1].length / 2);
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
      closeAll();
    } else {
      paragraph.push(line.trim());
    }
  }

  closeAll();
  return out.join('\n');
}
