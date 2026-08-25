import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";

const [luaPath, outputPath] = process.argv.slice(2);
if (!luaPath || !outputPath) {
  throw new Error(
    "Usage: node scripts/generate-latest-lua-runtime-inventory.mjs <lua-file> <output-markdown>",
  );
}

const source = readFileSync(luaPath, "utf8");
const lines = source.split(/\r?\n/);
const declarations = [];
const configFlags = new Set();
const menuStacks = [];

for (const [index, line] of lines.entries()) {
  const lineNumber = index + 1;
  const declaration = line.match(
    /^\s*(?:local\s+)?function\s+([A-Za-z0-9_.:]+)\s*\(|^\s*([A-Za-z0-9_.]+)\s*=\s*function\s*\(/,
  );
  if (declaration) {
    declarations.push({ lineNumber, name: declaration[1] ?? declaration[2] });
  }

  for (const match of line.matchAll(/_G\.LexusConfig\.([A-Za-z0-9_]+)/g)) {
    configFlags.add(match[1]);
  }

  const stack = line.match(/^\s*local\s+(Stack[A-Za-z0-9_]+)\s*=\s*\{/);
  if (stack) menuStacks.push({ lineNumber, name: stack[1] });
}

const globals = declarations.filter(({ name }) => name.startsWith("_G."));
const locals = declarations.filter(({ name }) => !name.startsWith("_G."));
const code = (value) => "`" + value + "`";
const menuStackText = menuStacks.map(({ name, lineNumber }) => "- " + code(name) + " — line " + lineNumber).join("\n");
const sourceOccurrences = (name) => source.split(name).length - 1;
const compatibilityOnly = new Set([
  "LoginWithKey",
  "GetConfigPaths",
  "_G.ReadLiveConfig",
]);
const classifyDeclaration = ({ name, lineNumber }) => {
  const occurrences = sourceOccurrences(name);
  const surroundingSource = lines.slice(Math.max(0, lineNumber - 3), Math.min(lines.length, lineNumber + 12)).join("\n");

  if (compatibilityOnly.has(name)) {
    return {
      category: "compatibility-only",
      evidence: "explicit compatibility helper; not an authorization or visible menu feature",
      occurrences,
    };
  }
  if (/\[BYPASSED\]|removed for stability/i.test(surroundingSource)) {
    return {
      category: "legacy/unreachable",
      evidence: "declaration is adjacent to a bypassed or removed runtime path",
      occurrences,
    };
  }
  if (name.startsWith("_G.") || name.includes(":")) {
    return {
      category: "external-callback",
      evidence: "global export or object method; may be invoked by the game/UI framework",
      occurrences,
    };
  }
  if (occurrences > 1) {
    return {
      category: "active-internal",
      evidence: "has one or more lexical references beyond its declaration",
      occurrences,
    };
  }
  return {
    category: "external-callback",
    evidence: "declaration-only candidate; treated as a possible engine callback rather than a current user feature",
    occurrences,
  };
};
const classifiedDeclarations = declarations.map((entry) => ({ ...entry, ...classifyDeclaration(entry) }));
const classificationCounts = Object.fromEntries(
  ["active-internal", "external-callback", "compatibility-only", "legacy/unreachable"].map((category) => [
    category,
    classifiedDeclarations.filter((entry) => entry.category === category).length,
  ]),
);
const formatDeclaration = ({ name, lineNumber }) =>
  "- " + code(name) + " — line " + lineNumber + "; source occurrences: " + sourceOccurrences(name);
const globalText = globals.map(formatDeclaration).join("\n");
const localText = locals.map(formatDeclaration).join("\n");
const declarationOnly = declarations.filter(({ name }) => sourceOccurrences(name) === 1);
const declarationOnlyText = declarationOnly.map(formatDeclaration).join("\n");
const classificationTable = classifiedDeclarations
  .map(
    ({ name, lineNumber, category, evidence, occurrences }) =>
      `| ${lineNumber} | ${code(name)} | ${occurrences} | ${category} | ${evidence} |`,
  )
  .join("\n");
const configText = [...configFlags]
  .sort((a, b) => a.localeCompare(b))
  .map((flag) => code(flag))
  .join(", ");

const output = `# Generated Latest Lua Runtime Inventory

**Generated from:** ${code(resolve(luaPath))}  
**Scope:** Complete lexical scan of the delivered Lua source. This appendix enumerates runtime declarations, menu stacks, and ${code("LexusConfig")} flags. It is an evidence index; a declaration alone does not prove that an external game engine invokes it.

| Metric | Count |
|---|---:|
| Source lines scanned | ${lines.length} |
| Function declarations found | ${declarations.length} |
| Exported ${code("_G")} helper declarations | ${globals.length} |
| Local/runtime helper declarations | ${locals.length} |
| Registered menu stacks | ${menuStacks.length} |
| Referenced ${code("LexusConfig")} flags | ${configFlags.size} |
| Declaration-only lexical candidates | ${declarationOnly.length} |
| Active internal declarations | ${classificationCounts["active-internal"]} |
| External-callback declarations | ${classificationCounts["external-callback"]} |
| Compatibility-only declarations | ${classificationCounts["compatibility-only"]} |
| Legacy/unreachable declarations | ${classificationCounts["legacy/unreachable"]} |

## Registered menu stacks

${menuStackText || "_None found._"}

## Exported global helpers

${globalText || "_None found._"}

## Local and namespaced function declarations

${localText || "_None found._"}

## Declaration-only lexical candidates

These entries appear once in the Lua text at their declaration. They are candidates for legacy-only, engine-callback, or externally invoked behavior. They are **not automatically treated as active user features** without an audited startup, menu, main-loop, or game-engine entry path.

${declarationOnlyText || "_None found._"}

## Declaration-by-declaration classification

The table below classifies **every function declaration found in the complete source scan**. The category is static/lexical evidence: it distinguishes direct internal references from game/UI callback candidates and identifies known compatibility or bypass-adjacent paths. It does not claim Android-engine invocation without a device runtime trace.

| Line | Declaration | Source occurrences | Static classification | Evidence |
|---:|---|---:|---|---|
${classificationTable || "| — | _None found._ | — | — | — |"}

## Referenced ${code("LexusConfig")} flags

${configText || "_None found._"}
`;

mkdirSync(dirname(outputPath), { recursive: true });
writeFileSync(outputPath, output, "utf8");
console.log(`RUNTIME_INVENTORY_GENERATED=${resolve(outputPath)}`);
console.log(`SOURCE_LINES=${lines.length}`);
console.log(`DECLARATIONS=${declarations.length}`);
console.log(`GLOBAL_HELPERS=${globals.length}`);
console.log(`MENU_STACKS=${menuStacks.length}`);
console.log(`CONFIG_FLAGS=${configFlags.size}`);
console.log(`ACTIVE_INTERNAL=${classificationCounts["active-internal"]}`);
console.log(`EXTERNAL_CALLBACK=${classificationCounts["external-callback"]}`);
console.log(`COMPATIBILITY_ONLY=${classificationCounts["compatibility-only"]}`);
console.log(`LEGACY_UNREACHABLE=${classificationCounts["legacy/unreachable"]}`);
