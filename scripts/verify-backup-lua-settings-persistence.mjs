import fs from 'node:fs';

const filePath = process.argv[2];
if (!filePath) {
  console.error('Usage: node scripts/verify-backup-lua-settings-persistence.mjs <lua-file>');
  process.exit(2);
}

const source = fs.readFileSync(filePath, 'utf8');
const required = [
  ['settings loader definition', '_G.LoadModSettings = function()'],
  ['authorized settings load guard', 'if not _G.ModSettingsLoaded then'],
  ['settings load invocation', 'if _G.LoadModSettings then _G.LoadModSettings() end'],
  ['settings load completion flag', '_G.ModSettingsLoaded = true'],
  ['menu initialization after settings load', 'if _G.ModMenuInitialized then return end'],
  ['config serializer', '_G.SaveModSettings = function()'],
  ['primary NEXA settings filename', 'local ConfigFileName = "NEXA_settings.txt"'],
  ['JSON migration filename', 'local LegacyConfigFileName = "nexaconfig.json"'],
  ['prior absolute config migration source', 'local PreviousConfigFileName = "/storage/emulated/0/Android/data/com.pubg.imobile/files/nexaconfig.json"'],
  ['shared menu persistence trigger', 'if changed ~= false and _G.SaveModSettings then _G.SaveModSettings() end'],
  ['cross-match write tracking', 'local wroteAny = false'],
  ['cross-match config path synchronization', 'wroteAny = true'],
  ['save cache gated by a successful write', 'if wroteAny then _G.LastConfigSaveStr = data end'],
  ['non-destructive validation transition', 'Do not overwrite saved toggle or slider values while a new match is'],
  ['non-destructive unauthorized loop', 'Keep persisted preferences intact during lobby/match revalidation.'],
];

let failed = false;
for (const [label, marker] of required) {
  const index = source.indexOf(marker);
  if (index < 0) {
    console.error(`FAIL: missing ${label}: ${marker}`);
    failed = true;
  }
}

const initIndex = source.indexOf('function _G.InitModMenuTab()');
const loadGuardIndex = source.indexOf('if not _G.ModSettingsLoaded then', initIndex);
const initializedGuardIndex = source.indexOf('if _G.ModMenuInitialized then return end', initIndex);
if (initIndex < 0 || loadGuardIndex < 0 || initializedGuardIndex < 0 || loadGuardIndex > initializedGuardIndex) {
  console.error('FAIL: settings must load before the menu initialized guard');
  failed = true;
}

const saveStart = source.indexOf('_G.SaveModSettings = function()');
const cacheAfterWrite = source.indexOf('_G.LastConfigSaveStr = data', saveStart);
const writeIndex = source.indexOf('file:write(data)', saveStart);
if (saveStart < 0 || writeIndex < 0 || cacheAfterWrite < writeIndex) {
  console.error('FAIL: settings cache must update only after a successful file write');
  failed = true;
}

const wrapperStart = source.indexOf('local function GuardMenuStack(stack)');
const changedIndex = source.indexOf('local changed = originalSet(...)', wrapperStart);
const persistIndex = source.indexOf('if changed ~= false and _G.SaveModSettings then _G.SaveModSettings() end', wrapperStart);
if (wrapperStart < 0 || changedIndex < 0 || persistIndex < changedIndex) {
  console.error('FAIL: guarded standard menu setters must persist successful changes');
  failed = true;
}

const writeLoopIndex = source.indexOf('local wroteAny = false', saveStart);
const noEarlyBreakIndex = source.indexOf('break', writeLoopIndex);
const cacheAfterAllWrites = source.indexOf('if wroteAny then _G.LastConfigSaveStr = data end', writeLoopIndex);
if (writeLoopIndex < 0 || cacheAfterAllWrites < writeLoopIndex || (noEarlyBreakIndex >= writeLoopIndex && noEarlyBreakIndex < cacheAfterAllWrites)) {
  console.error('FAIL: settings must synchronize every writable config path before caching the payload');
  failed = true;
}

if (source.includes('local function DisableAllFeatureFlags()') || source.includes('if type(value) == "boolean" then _G.LexusConfig[key] = false end')) {
  console.error('FAIL: authorization transitions must not overwrite persisted boolean settings');
  failed = true;
}

if (failed) process.exit(1);
console.log('PASS: backup Lua preserves settings through revalidation and persists successful toggle/slider changes across writable config paths');
