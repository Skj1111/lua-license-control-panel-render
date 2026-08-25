import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-removed-runtime-branches.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const failures = [];

const forbidden = [
  ["ESP Type 1", "EspVip"], ["ESP Type 3", "EspVipPro"], ["ESP Type 4", "EspRadar"],
  ["ESP Type 6", "EspLoai6"], ["ESP Type 7 posture", "Esp7_TuThe"], ["ESP item master", "EspItem_Master"],
  ["horizontal recoil", "CustomHRecoil"], ["vertical recoil", "CustomVRecoil"], ["perfect accuracy", "Accuracy"],
  ["aimbot head", "AutoHead"], ["vehicle wallhack", "WallVehicle"], ["tree removal", "RemoveTrees"],
  ["fog removal", "RemoveFog"], ["fast flying car", "FastCar"],
  ["ESP outline", "EspOutline"], ["outline thickness", "OutlineThickness"],
];
for (const [label, marker] of forbidden) {
  if (new RegExp(`\\b${marker}\\b`).test(source)) failures.push(`${label}: retained marker ${marker}`);
}

const preservedMarkers = ["EspAntenna", "EspLoai8", "EspLoai7", "Esp7_VuKhi", "RemoveGrass", "BlackSky", "CustomAimbot"];
for (const marker of preservedMarkers) {
  if (!source.includes(marker)) failures.push(`unrelated active feature marker missing: ${marker}`);
}

if (failures.length > 0) {
  console.error("REMOVED_RUNTIME_BRANCHES_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("REMOVED_RUNTIME_BRANCHES_CHECK=PASS");
console.log(`PHYSICALLY_ABSENT_LEGACY_MARKERS=${forbidden.length}`);
console.log(`PRESERVED_UNRELATED_FEATURE_MARKERS=${preservedMarkers.length}`);
