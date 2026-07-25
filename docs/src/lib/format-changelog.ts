// Tiny GitHub-flavored-markdown-ish renderer for release changelog bodies. Not a general
// markdown engine - just the subset these changelogs actually use (headers, nested bullet
// lists, blockquotes, bold/italic/code, links), so we don't need a markdown dependency for
// static content that never needs to handle arbitrary user input.

function renderInline(text: string): string {
  return text
    // Issue/PR references (GH-1234) - link before anything else so the generated <a> tags
    // below can't accidentally swallow or re-wrap them.
    .replace(/\bGH-(\d+)\b/g, '<a href="https://github.com/FunnyGuilds/FunnyGuilds/issues/$1" target="_blank" rel="noopener">GH-$1</a>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
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
