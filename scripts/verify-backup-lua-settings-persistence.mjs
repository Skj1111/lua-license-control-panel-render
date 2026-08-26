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

if (failed) process.exit(1);
console.log('PASS: backup Lua has authorized one-time settings load before menu initialization');
