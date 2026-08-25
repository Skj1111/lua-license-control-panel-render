import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-vehicle-skin-removal.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const forbidden = [
  "VehicleAvatarComponent",
  "CheckCanPlaySkinSwitchEffect",
  "ShowVehicleSwitchEffect",
  "ChangeFakeSwitchVehicleAvatar",
  "CauserVehicleSkinID",
  "CurrentEquipVehicleID",
  "VehiclePlateLicenseUtil",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("VEHICLE_SKIN_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining vehicle-skin marker: ${marker}`));
  process.exit(1);
}

if (!source.includes("Vehicle skin customization has been removed by request")) {
  console.error("VEHICLE_SKIN_REMOVAL_CHECK=FAIL");
  console.error("- missing explicit vehicle-skin removal policy");
  process.exit(1);
}

console.log("VEHICLE_SKIN_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_SKIN_MARKERS=${forbidden.length}`);
