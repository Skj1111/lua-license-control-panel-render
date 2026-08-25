import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-requested-removals.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const failures = [];
const removedMenuKeys = [
  "ModMenu_ESP1", "ModMenu_ESP3_Ex", "ModMenu_ESP3_Name", "ModMenu_ESP3_HP", "ModMenu_ESP4", "ModMenu_ESP6",
  "ModMenu_ESP7_TuThe", "ModMenu_EspItem_Link", "ModMenu_HRecoil_Ex", "ModMenu_HRecoil_Val", "ModMenu_VRecoil_Ex",
  "ModMenu_VRecoil_Val", "ModMenu_Accuracy", "ModMenu_AutoHead", "ModMenu_WallVehicle", "ModMenu_RemoveFog",
  "ModMenu_RemoveTrees", "ModMenu_FastCar",
];
for (const key of removedMenuKeys) {
  if (source.includes(key)) failures.push(`removed menu key remains: ${key}`);
}

const policyStart = source.indexOf("local REMOVED_FEATURE_CONFIG_KEYS = {");
const policyEnd = source.indexOf("local function ApplyRemovedFeaturePolicy()", policyStart);
const policy = policyStart >= 0 && policyEnd > policyStart ? source.slice(policyStart, policyEnd) : "";
const removedConfigKeys = [
  "EspVip", "EspVipPro", "Esp3ShowName", "Esp3ShowHP", "EspRadar", "EspLoai6", "Esp7_TuThe", "EspItem_Master",
  "CustomHRecoil", "CustomVRecoil", "Accuracy", "AutoHead", "WallVehicle", "RemoveFog", "RemoveTrees", "FastCar",
];
for (const key of removedConfigKeys) {
  if (!policy.includes(`"${key}"`)) failures.push(`removed config key missing from forced-off policy: ${key}`);
}

const mainLoopStart = source.indexOf("function MainLoop()");
const mainLoop = mainLoopStart >= 0 ? source.slice(mainLoopStart, mainLoopStart + 1500) : "";
if (!mainLoop.includes("ApplyRemovedFeaturePolicy()")) failures.push("MainLoop must enforce forced-off policy");

if (failures.length > 0) {
  console.error("REQUESTED_REMOVALS_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("REQUESTED_REMOVALS_CHECK=PASS");
console.log(`REMOVED_MENU_CONTROLS=${removedMenuKeys.length}`);
console.log(`FORCED_OFF_RUNTIME_KEYS=${removedConfigKeys.length}`);
