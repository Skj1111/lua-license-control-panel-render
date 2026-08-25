import { readFileSync } from "node:fs";

const [luaPath, auditPath] = process.argv.slice(2);
if (!luaPath || !auditPath) {
  throw new Error("Usage: node scripts/verify-full-second-pass-audit.mjs <lua-file> <audit-markdown>");
}

const lua = readFileSync(luaPath, "utf8");
const audit = readFileSync(auditPath, "utf8");
const lines = lua.split(/\r?\n/);
const sectionStarts = [];

for (const [index, line] of lines.entries()) {
  const match = line.match(/^--\s*FEATURE\s+(\d+):\s*(.+)$/i);
  if (match && index + 1 > 60) sectionStarts.push(index + 1);
}

const expectedRanges = sectionStarts.map((start, index) => {
  const end = (sectionStarts[index + 1] ?? lines.length + 1) - 1;
  return `${start}–${end}`;
});

for (const range of expectedRanges) {
  if (!audit.includes(`| ${range} |`)) {
    throw new Error(`Second-pass audit is missing reviewed range ${range}`);
  }
}

if (!audit.includes(`| **PASS** | ${lines.length} | ${expectedRanges.length} | 0 | 0 | 0 |`)) {
  throw new Error("Second-pass audit does not report clean full-source structural findings");
}

console.log("FULL_SECOND_PASS_AUDIT_CHECK=PASS");
console.log(`SOURCE_LINES=${lines.length}`);
console.log(`REVIEWED_MAJOR_SECTIONS=${expectedRanges.length}`);
console.log(`REVIEWED_SOURCE_RANGE=${expectedRanges[0]}..${expectedRanges.at(-1)}`);
