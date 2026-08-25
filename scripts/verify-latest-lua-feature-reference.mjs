import { readFileSync } from "node:fs";

const [luaPath, referencePath, inventoryPath] = process.argv.slice(2);
if (!luaPath || !referencePath || !inventoryPath) {
  throw new Error(
    "Usage: node scripts/verify-latest-lua-feature-reference.mjs <lua-file> <reference-markdown> <inventory-markdown>",
  );
}

const lua = readFileSync(luaPath, "utf8");
const reference = readFileSync(referencePath, "utf8");
const inventory = readFileSync(inventoryPath, "utf8");

function requireSource(label, marker) {
  if (!lua.includes(marker)) {
    throw new Error(`Feature-reference source coverage failed: ${label}`);
  }
}

function requireReference(label, marker) {
  if (!reference.includes(marker)) {
    throw new Error(`Feature-reference documentation coverage failed: ${label}`);
  }
}

function requireInventory(label, marker) {
  if (!inventory.includes(marker)) {
    throw new Error(`Feature-reference inventory coverage failed: ${label}`);
  }
}

const lineCount = lua.split(/\r?\n/).length;
const declarationMatches = [...lua.matchAll(/^\s*(?:local\s+)?function\s+([A-Za-z0-9_.:]+)\s*\(|^\s*([A-Za-z0-9_.]+)\s*=\s*function\s*\(/gm)];
const declarationCount = declarationMatches.length;
const globalHelperCount = declarationMatches.filter((match) => (match[1] ?? match[2]).startsWith("_G.")).length;
const menuStackCount = lua.match(/^\s*local\s+Stack[A-Za-z0-9_]+\s*=\s*\{/gm)?.length ?? 0;
const configFlagCount = new Set([...lua.matchAll(/_G\.LexusConfig\.([A-Za-z0-9_]+)/g)].map((match) => match[1])).size;
const classifiedDeclarationRows = inventory.match(/^\| \d+ \| `[^`]+` \| \d+ \| (?:active-internal|external-callback|compatibility-only|legacy\/unreachable) \|/gm)?.length ?? 0;

[
  ["canonical license validation", "function NexaLicenseClient.verify"],
  ["key-entry retry", "function _G.ScheduleNexaKeyEntryRetry"],
  ["configuration autosave", "local function AutoSaveLoop"],
  ["configuration load", "_G.LoadModSettings = function"],
  ["authorized main loop", "function MainLoop()"],
  ["authorized startup bootstrap", "local function InitAllModSystems()"],
  ["legacy helper exclusion evidence", "_G.ForceEnableKillMessage = function"],
  ["key-entry popup", "KEY_ENTRY_BLUEPRINT"],
].forEach(([label, marker]) => requireSource(label, marker));

[
  ["login and saved-key section", "## 1. Login, key, aur authorization system"],
  ["AD ESP section", "## 3. AD ESP — Advanced Stable Tags & Tracers"],
  ["visible ESP section", "## 4. ESP tab — all controls"],
  ["visible aimbot section", "## 5. Aimbot tab — all controls"],
  ["visible graphics section", "## 7. Support & Graphics tab — all controls"],
  ["non-menu runtime section", "## 8. Non-menu runtime systems — active behavior"],
  ["legacy exclusion section", "## 9. Legacy / compatibility / excluded paths"],
  ["audit-coverage section", "## 10. Full-source audit proof"],
  ["static classification section", "### Static classification ka matlab"],
  ["Hinglish explanation", "Kaise kaam karta hai"],
  ["inventory appendix reference", "LATEST_LUA_RUNTIME_INVENTORY.md"],
].forEach(([label, marker]) => requireReference(label, marker));

[
  ["line count", `| Source lines scanned | ${lineCount} |`],
  ["function declaration count", `| Function declarations found | ${declarationCount} |`],
  ["global-helper count", `| Exported \`_G\` helper declarations | ${globalHelperCount} |`],
  ["menu-stack count", `| Registered menu stacks | ${menuStackCount} |`],
  ["config-flag count", `| Referenced \`LexusConfig\` flags | ${configFlagCount} |`],
  ["declaration-only candidate section", "## Declaration-only lexical candidates"],
  ["full declaration classification table", "## Declaration-by-declaration classification"],
].forEach(([label, marker]) => requireInventory(label, marker));

[
  ["active-internal classification count", "| **Active-internal declarations** | `166` |"],
  ["external-callback classification count", "| **External-callback declarations** | `176` |"],
  ["compatibility-only classification count", "| **Compatibility-only declarations** | `3` |"],
  ["legacy classification count", "| **Legacy/unreachable declarations** | `3` |"],
].forEach(([label, marker]) => requireReference(label, marker));

if (classifiedDeclarationRows !== declarationCount) {
  throw new Error(
    `Feature-reference inventory coverage failed: classified ${classifiedDeclarationRows} of ${declarationCount} declarations`,
  );
}

console.log("LATEST_LUA_FEATURE_REFERENCE_CHECK=PASS");
console.log(`SOURCE_LINES=${lineCount}`);
console.log(`FUNCTION_DECLARATIONS=${declarationCount}`);
console.log(`GLOBAL_HELPERS=${globalHelperCount}`);
console.log(`VISIBLE_MENU_STACKS=${menuStackCount}`);
console.log(`CONFIG_FLAGS=${configFlagCount}`);
console.log(`CLASSIFIED_DECLARATIONS=${classifiedDeclarationRows}`);
console.log("NON_MENU_RUNTIME_AUDIT=PASS");
