import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-gameplay-lua-ad-esp-lines.mjs <lua-file>");
const source = readFileSync(target, "utf8");
const forbidden = [
  "ADESP_", "StackADESP =", "ModMenu_ADESP_", "InitializeADESPSystem",
  "UpdateAllESP", "StartEnemyCounter", "ESPLineWidgets", "EnemyCounterWidget",
  "StableEntityBoxes", "StableESPCanvas", "GetESPLineEndpoint",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("AD_ESP_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining AD ESP marker: ${marker}`));
  process.exit(1);
}
for (const required of [
  "local ESP9VisualState = {", "refreshInterval = 0.10", "maxTargets = 12",
  "local function ESP9VisualTick()", "if not HasPanelAuthorization() then ESP9Clear(); return end",
  "if not (_G.LexusConfig and _G.LexusConfig.ESP9Visual) then ESP9Clear(); return end",
  "local StackADESP9 = {", "ModMenu_ESP9_Master", "ModMenu_ESP9_Snapline",
  "GuardMenuStack(StackADESP9)", "Cat_ADESP9", "ESP9VisualTick()", "ESP9Clear()",
]) {
  if (!source.includes(required)) {
    console.error("ESP9_AD_ESP_CHECK=FAIL");
    console.error(`- required ESP9 marker missing: ${required}`);
    process.exit(1);
  }
}
const esp9BlockStart = source.indexOf("local ESP9VisualState = {");
const esp9BlockEnd = source.indexOf("function MainLoop()", esp9BlockStart);
const esp9Block = esp9BlockStart >= 0 && esp9BlockEnd > esp9BlockStart ? source.slice(esp9BlockStart, esp9BlockEnd) : "";
for (const excluded of ["FakeHWID", "AutoHead", "Esp7_TuThe", "CustomMagicBullet", "RemoveFog", "RemoveTrees", "FastCar"]) {
  if (esp9Block.includes(excluded)) {
    console.error("ESP9_AD_ESP_CHECK=FAIL");
    console.error(`- excluded feature marker appears in ESP9 module: ${excluded}`);
    process.exit(1);
  }
}
for (const preserved of ["ModMenu_ESP2", "ModMenu_ESP5", "ModMenu_ESP7_Ex", "ModMenu_ESP8", "ModMenu_ESPAntenna"]) {
  if (!source.includes(preserved)) {
    console.error("AD_ESP_REMOVAL_CHECK=FAIL");
    console.error(`- normal ESP marker missing: ${preserved}`);
    process.exit(1);
  }
}
console.log("ESP9_AD_ESP_CHECK=PASS");
console.log(`FORBIDDEN_LEGACY_AD_ESP_MARKERS=${forbidden.length}`);
console.log("NORMAL_ESP_PRESERVED=PASS");
