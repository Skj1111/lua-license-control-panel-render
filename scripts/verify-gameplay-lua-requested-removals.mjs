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
const removedConfigKeys = [
  "EspVip", "EspVipPro", "Esp3ShowName", "Esp3ShowHP", "EspRadar", "EspLoai6", "Esp7_TuThe", "EspItem_Master",
  "CustomHRecoil", "CustomVRecoil", "Accuracy", "AutoHead", "WallVehicle", "RemoveFog", "RemoveTrees", "FastCar",
];

for (const key of removedMenuKeys) {
  if (source.includes(key)) failures.push(`removed menu key remains: ${key}`);
}
for (const key of removedConfigKeys) {
  if (new RegExp(`\\b${key}\\b`).test(source)) failures.push(`removed configuration/runtime marker remains: ${key}`);
}
for (const marker of ["REMOVED_FEATURE_CONFIG_KEYS", "ApplyRemovedFeaturePolicy", "_G.RunOptimizedItemAndVehicleESP", "_G.InitializeAutoHeadHooks"]) {
  if (source.includes(marker)) failures.push(`obsolete removal-policy marker remains: ${marker}`);
}

if (failures.length > 0) {
  console.error("REQUESTED_REMOVALS_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("REQUESTED_REMOVALS_CHECK=PASS");
console.log(`REMOVED_MENU_CONTROLS=${removedMenuKeys.length}`);
console.log(`PHYSICALLY_REMOVED_CONFIG_AND_RUNTIME_MARKERS=${removedConfigKeys.length}`);
