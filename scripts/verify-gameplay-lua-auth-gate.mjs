import fs from "node:fs";
import path from "node:path";

const target = process.argv[2];
if (!target) {
  console.error("Usage: node scripts/verify-gameplay-lua-auth-gate.mjs /absolute/path/to/BRPlayerCharacterBase.lua");
  process.exit(2);
}

const source = fs.readFileSync(path.resolve(target), "utf8");
const failures = [];

function requireMatch(label, pattern) {
  if (!pattern.test(source)) failures.push(label);
}

function blockFrom(anchor, lineCount) {
  const start = source.indexOf(anchor);
  if (start < 0) {
    failures.push(`missing anchor: ${anchor}`);
    return "";
  }
  return source.slice(start, start + lineCount);
}

requireMatch("canonical authorization helper", /local function HasPanelAuthorization\(\)[\s\S]{0,500}?panel\.IsAuthorized\(\) == true/);

const menuInit = blockFrom("function _G.InitModMenuTab()", 450);
if (!/if not HasPanelAuthorization\(\) then[\s\S]{0,180}?return false/.test(menuInit)) {
  failures.push("InitModMenuTab must refuse unauthorised initialization");
}

const menuStack = blockFrom("local function GuardMenuStack(stack)", 2400);
if ((menuStack.match(/GuardMenuStack\(Stack/g) ?? []).length !== 5) {
  failures.push("all five menu stacks must be guarded");
}
if (!/if not HasPanelAuthorization\(\) then return false end/.test(menuStack)) {
  failures.push("menu GetFunc/SetFunc authorization guard");
}

const showUi = blockFrom("UIManager.ShowUI = function(config, ...)", 2200);
if (!/table\.remove\(catalog, modMenuIdx\)/.test(showUi)) {
  failures.push("unauthorised settings catalog must strip ModMenu");
}

const mainLoop = blockFrom("function MainLoop()", 3200);
if (!/local isAuthorized = HasPanelAuthorization\(\)[\s\S]{0,900}?if not isAuthorized then[\s\S]{0,900}?return/.test(mainLoop)) {
  failures.push("MainLoop must early-return before feature work when unauthorised");
}

for (const [entry, anchor] of [
  ["TPP view", "function ApplyTPPView()"],
  ["AD ESP initialization", "function _G.InitializeADESPSystem()"],
  ["AD ESP update timer", "local function UpdateAllESP()"],
  ["system initialization", "local function InitAllModSystems()"],
]) {
  const block = blockFrom(anchor, 800);
  if (!/if not HasPanelAuthorization\(\) then[\s\S]{0,250}?return/.test(block)) {
    failures.push(`${entry} must early-return before login`);
  }
}

for (const [entry, anchor] of [
  ["saved setting writes", "_G.SaveModSettings = function()"],
  ["saved setting reads", "_G.LoadModSettings = function()"],
  ["live setting writes", "_G.ReadLiveConfig = function()"],
]) {
  const block = blockFrom(anchor, 650);
  if (!/if not HasPanelAuthorization\(\) then return false end/.test(block)) {
    failures.push(`${entry} must be blocked before login`);
  }
}

const startupConfig = blockFrom("-- KHỞI CHẠY LẦN ĐẦU TIÊN", 750);
if (/\b_G\.LoadModSettings\(\)/.test(startupConfig) || !/Saved toggles are deliberately not loaded here/.test(startupConfig)) {
  failures.push("startup must defer persisted toggle loading until authorization");
}

const globalFeatureEntrypoints = [
  ["auto-head hook installer", "function _G.InitializeAutoHeadHooks()"],
  ["kill counter hook installer", "_G.ForceEnableKillCounterUI = function()"],
  ["kill message hook installer", "_G.ForceEnableKillMessage = function()"],
  ["enemy target helper", "_G.GetEnemyTargetsFromActors = function(radius)"],
  ["aim helper", "_G.AimTouch = function()"],
  ["vehicle and item ESP helper", "_G.RunOptimizedItemAndVehicleESP = function(pc)"],
  ["AD ESP initializer", "function _G.InitializeADESPSystem()"],
  ["mortar aim helper", "function _G.MortarAimTick()"],
];

for (const [entry, anchor] of globalFeatureEntrypoints) {
  const block = blockFrom(anchor, 950);
  if (!/if not HasPanelAuthorization\(\) then[\s\S]{0,250}?return/.test(block)) {
    failures.push(`${entry} must early-return before login`);
  }
}

const globalDefinitions = [...source.matchAll(/^(?:function _G\.([A-Za-z0-9_]+)|_G\.([A-Za-z0-9_]+)\s*=\s*function)/gm)]
  .map((match) => match[1] ?? match[2]);
const expectedGlobals = new Set([
  "NexaLoginPanel",
  "require",
  "InitializeFeature03PresentationHooks",
  "InitializeAutoHeadHooks",
  "ForceEnableKillCounterUI",
  "ForceEnableKillMessage",
  "SaveModSettings",
  "LoadModSettings",
  "ReadLiveConfig",
  "InitModMenuTab",
  "ScheduleNexaKeyEntryRetry",
  "GetEnemyTargetsFromActors",
  "AimTouch",
  "RunOptimizedItemAndVehicleESP",
  "InitializeADESPSystem",
  "MortarAimTick",
]);
for (const name of globalDefinitions) {
  if (!expectedGlobals.has(name)) failures.push(`unreviewed standalone global helper: ${name}`);
}

const callbackInventory = [
  ["autosave callback", "local function AutoSaveLoop()", /_G\.SaveModSettings\(\)/],
  ["AD ESP timer callback", "local function UpdateAllESP()", /if not HasPanelAuthorization\(\) then[\s\S]{0,250}?return/],
  ["fast scheduler callback", "local function FastTick()", /PCall\(MainLoop\)/],
  ["main feature scheduler", "function MainLoop()", /if not isAuthorized then[\s\S]{0,900}?return/],
  ["authorized system initializer", "local function InitAllModSystems()", /if not HasPanelAuthorization\(\) then[\s\S]{0,250}?return/],
  ["mortar timer target", "function _G.MortarAimTick()", /if not HasPanelAuthorization\(\) then[\s\S]{0,250}?return/],
  ["graphics initializer invocation", "function MainLoop()", /InitializeGraphicsUnlock\(\)/],
  ["native ESP initializer invocation", "function MainLoop()", /InitializeNativeESP\(\)/],
];
for (const [entry, anchor, expectation] of callbackInventory) {
  const block = blockFrom(anchor, 24000);
  if (!expectation.test(block)) failures.push(`${entry} inventory expectation`);
}

if (failures.length > 0) {
  console.error("AUTH_GATE_CHECK=FAIL");
  for (const failure of failures) console.error(`- ${failure}`);
  process.exit(1);
}

console.log("AUTH_GATE_CHECK=PASS");
console.log("GUARDED_MENU_STACKS=5");
console.log("GUARDED_INDEPENDENT_FEATURE_PATHS=12");
console.log("GUARDED_PERSISTED_CONFIG_PATHS=3");
console.log(`REVIEWED_GLOBAL_HELPERS=${globalDefinitions.length}`);
console.log(`REVIEWED_CALLBACK_AND_STARTUP_PATHS=${callbackInventory.length}`);
