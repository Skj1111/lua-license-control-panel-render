import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-gameplay-lua-ad-esp-lines.mjs <lua-file>");
const source = readFileSync(target, "utf8");

const legacyForbidden = [
  "ADESP_", "StackADESP =", "ModMenu_ADESP_", "InitializeADESPSystem",
  "UpdateAllESP", "StartEnemyCounter", "ESPLineWidgets", "EnemyCounterWidget",
  "StableEntityBoxes", "StableESPCanvas", "GetESPLineEndpoint",
];
const oldEsp9Forbidden = [
  "ESP9VisualState", "StackADESP9", "Cat_ADESP9", "ModMenu_ESP9_Master",
  "ModMenu_ESP9_Snapline", "ESP9VisualTick", "ESP9Clear", "ESP9_Weapon",
  "ESP9_Snapline", "ESP9_TeamBox",
];
const remaining = [...legacyForbidden, ...oldEsp9Forbidden].filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("ESP_VIP_REPLACEMENT_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- obsolete marker remains: ${marker}`));
  process.exit(1);
}

const required = [
  "local ESPVIPState = {", "local function ESPVIPCreateCounter()", "local function ESPVIPCreateMarker(id)",
  "local function ESPVIPTick()", "if not HasPanelAuthorization() then ESPVIPClear(); return end",
  "if not (_G.LexusConfig and _G.LexusConfig.EspLoai9) then ESPVIPClear(); return end",
  "local StackESPVIP = {", "ModMenu_ESPVIP_Ex", "ModMenu_ESPVIP_Count",
  "ModMenu_ESPVIP_Line_Thick", "ModMenu_ESPVIP_Line_VisColor", "ModMenu_ESPVIP_Line_HidColor",
  "GuardMenuStack(StackESPVIP)", "Cat_ESPVIP", "ESPVIPTick()", "ESPVIPClear()",
  "Player: %d   Bot: %d   Dist: %dm", "local function ESPVIPColor(index, alpha)",
  "refreshInterval=0.10", "maxTargets=12",
];
const missing = required.filter((marker) => !source.includes(marker));
if (missing.length > 0) {
  console.error("ESP_VIP_REPLACEMENT_CHECK=FAIL");
  missing.forEach((marker) => console.error(`- required VIP marker missing: ${marker}`));
  process.exit(1);
}

const vipStart = source.indexOf("local ESPVIPState = {");
const vipEnd = source.indexOf("function MainLoop()", vipStart);
const vipBlock = vipStart >= 0 && vipEnd > vipStart ? source.slice(vipStart, vipEnd) : "";
for (const excluded of ["FakeHWID", "AutoHead", "Esp7_TuThe", "CustomMagicBullet", "RemoveFog", "RemoveTrees", "FastCar", "WallVehicle"]) {
  if (vipBlock.includes(excluded)) {
    console.error("ESP_VIP_REPLACEMENT_CHECK=FAIL");
    console.error(`- excluded marker appears in ESP VIP module: ${excluded}`);
    process.exit(1);
  }
}
for (const preserved of ["ModMenu_ESP2", "ModMenu_ESP5", "ModMenu_ESP7_Ex", "ModMenu_ESP8", "ModMenu_ESPAntenna"]) {
  if (!source.includes(preserved)) {
    console.error("ESP_VIP_REPLACEMENT_CHECK=FAIL");
    console.error(`- normal ESP marker missing: ${preserved}`);
    process.exit(1);
  }
}

console.log("ESP_VIP_REPLACEMENT_CHECK=PASS");
console.log(`FORBIDDEN_LEGACY_MARKERS=${legacyForbidden.length + oldEsp9Forbidden.length}`);
console.log("NORMAL_ESP_PRESERVED=PASS");
