// Tiny GitHub-flavored-markdown-ish renderer for release changelog bodies. Not a general
// markdown engine - just the subset these changelogs actually use (headers, nested bullet
// lists, blockquotes, bold/italic/code, links), so we don't need a markdown dependency for
// static content that never needs to handle arbitrary user input.

function renderInline(text: string): string {
  return text
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
}

function listIndent(line: string): number {
  const match = line.match(/^(\s*)/);
  return Math.floor((match ? match[1].length : 0) / 2);
}

export function renderChangelog(markdown: string): string {
  const lines = markdown.replace(/\r\n/g, '\n').split('\n');
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

  for (const raw of lines) {
    const line = raw.trimEnd();
    const heading = line.match(/^(#{1,6})\s+(.*)$/);
    const listItem = line.match(/^(\s*)[-*]\s+(.*)$/);
    const quote = line.match(/^>\s?(.*)$/);

    if (heading) {
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
