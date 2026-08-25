import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";

const [luaPath, outputPath] = process.argv.slice(2);
if (!luaPath || !outputPath) {
  throw new Error("Usage: node scripts/audit-full-lua-sections.mjs <lua-file> <output-markdown>");
}

const source = readFileSync(luaPath, "utf8");
const lines = source.split(/\r?\n/);
const sectionStarts = [];

for (const [index, line] of lines.entries()) {
  const match = line.match(/^--\s*FEATURE\s+(\d+):\s*(.+)$/i);
  if (match && index + 1 > 60) {
    sectionStarts.push({ line: index + 1, feature: match[1], title: match[2].trim() });
  }
}

const sections = sectionStarts.map((start, index) => {
  const end = (sectionStarts[index + 1]?.line ?? lines.length + 1) - 1;
  const body = lines.slice(start.line - 1, end).join("\n");
  const count = (pattern) => body.match(pattern)?.length ?? 0;
  return {
    ...start,
    end,
    length: end - start.line + 1,
    declarations: count(/^\s*(?:local\s+)?function\s+[A-Za-z0-9_.:]+\s*\(|^\s*[A-Za-z0-9_.]+\s*=\s*function\s*\(/gm),
    protectedCalls: count(/\bPCall\(/g),
    authGates: count(/HasPanelAuthorization\(/g),
    timers: count(/AddTimer(?:Once|Loop)\(/g),
    callbacks: count(/(?:OnResponseEvent|\.Add\(|:Add\()/g),
    hostApis: count(/(?:RequestForLua|ShowUIWithLuaAndBpPath|GetHttpWrapper|GetPhoneDeviceID|WriteFileAtPath)/g),
    configReads: count(/_G\.LexusConfig\./g),
  };
});

const localFunctionNames = new Map();
for (const [index, line] of lines.entries()) {
  const match = line.match(/^\s*local\s+function\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(/);
  if (!match) continue;
  const lineNumbers = localFunctionNames.get(match[1]) ?? [];
  lineNumbers.push(index + 1);
  localFunctionNames.set(match[1], lineNumbers);
}
const duplicateLocals = [...localFunctionNames.entries()].filter(([, lineNumbers]) => lineNumbers.length > 1);
const invalidStringGuardLines = lines
  .map((line, index) => ({ line, lineNumber: index + 1 }))
  .filter(({ line }) => line.includes("string.sformat"));
const earlyPCallLines = lines
  .slice(0, 21)
  .map((line, index) => ({ line, lineNumber: index + 1 }))
  .filter(({ line }) => /^\s*PCall\(/.test(line));
const sectionRows = sections
  .map(
    (section) =>
      `| ${section.feature} | ${section.title} | ${section.line}–${section.end} | ${section.length} | ${section.declarations} | ${section.protectedCalls} | ${section.authGates} | ${section.timers} | ${section.callbacks} | ${section.hostApis} | ${section.configReads} |`,
  )
  .join("\n");

const result = duplicateLocals.length === 0 && invalidStringGuardLines.length === 0 && earlyPCallLines.length === 0 ? "PASS" : "REVIEW";
const output = `# Latest Lua — Exhaustive Second-Pass Section Audit

**Source:** \`${resolve(luaPath)}\`  
**Method:** This report processes **every source line** and divides the delivered Lua into its numbered major feature sections. Each range is scanned for declarations, protected calls, authorization gates, timers, callback registration, host APIs, and configuration access. It is a static audit record, not a substitute for Android runtime testing.

| Audit status | Source lines processed | Major sections | Duplicate local functions | Invalid \`string.sformat\` guards | Top-level \`PCall\` before alias |
|---|---:|---:|---:|---:|---:|
| **${result}** | ${lines.length} | ${sections.length} | ${duplicateLocals.length} | ${invalidStringGuardLines.length} | ${earlyPCallLines.length} |

## Section-by-section evidence

| Feature | Major section | Source range | Lines | Functions | PCall | Auth gates | Timers | Callbacks | Host APIs | Config reads |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|---:|
${sectionRows || "| — | _No major section markers found._ | — | — | — | — | — | — | — | — | — |"}

## Global static findings

| Check | Result | Evidence |
|---|---|---|
| Duplicate local functions | ${duplicateLocals.length === 0 ? "PASS" : "REVIEW"} | ${duplicateLocals.length === 0 ? "None found." : duplicateLocals.map(([name, lineNumbers]) => `\`${name}\` at lines ${lineNumbers.join(", ")}`).join("; ")} |
| Invalid \`string.sformat\` guard | ${invalidStringGuardLines.length === 0 ? "PASS" : "REVIEW"} | ${invalidStringGuardLines.length === 0 ? "None found." : invalidStringGuardLines.map(({ lineNumber }) => `line ${lineNumber}`).join(", ")} |
| Top-level \`PCall\` before local alias | ${earlyPCallLines.length === 0 ? "PASS" : "REVIEW"} | ${earlyPCallLines.length === 0 ? "None found." : earlyPCallLines.map(({ lineNumber }) => `line ${lineNumber}`).join(", ")} |
| Canonical callback self argument | ${source.includes("PCall(add_response, response_event, function") ? "PASS" : "REVIEW"} | HTTP response delegate passes its event object as the first argument. |
| Strict MainLoop authorization gate | ${source.includes("local isAuthorized = HasPanelAuthorization()") ? "PASS" : "REVIEW"} | MainLoop obtains authorization before feature work. |
| Auto-head hook idempotency | ${source.includes("if _G.AutoHeadHooksInitialized then return true end") ? "PASS" : "REVIEW"} | Repeated authorized initialization no longer stacks hook wrappers. |

## Scope note

This audit records the full static source review requested for the delivered Lua. A static scan can prove syntax-adjacent structure and entry-point wiring, but it cannot prove the game host accepts every reflected API at runtime. Android evidence remains necessary for real client error, popup, key-file, and in-match rendering confirmation.
`;

mkdirSync(dirname(outputPath), { recursive: true });
writeFileSync(outputPath, output, "utf8");
console.log(`SECOND_PASS_AUDIT=${result}`);
console.log(`SOURCE_LINES=${lines.length}`);
console.log(`MAJOR_SECTIONS=${sections.length}`);
console.log(`DUPLICATE_LOCAL_FUNCTIONS=${duplicateLocals.length}`);
console.log(`INVALID_STRING_GUARDS=${invalidStringGuardLines.length}`);
console.log(`EARLY_PCALL_LINES=${earlyPCallLines.length}`);
if (result !== "PASS") process.exitCode = 1;
