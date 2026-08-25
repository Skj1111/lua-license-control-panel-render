import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-removed-runtime-branches.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const failures = [];

const policyStart = source.indexOf("local REMOVED_FEATURE_CONFIG_KEYS = {");
const policyEnd = source.indexOf("local function ApplyRemovedFeaturePolicy()", policyStart);
const policy = policyStart >= 0 && policyEnd > policyStart ? source.slice(policyStart, policyEnd) : "";
const checks = [
  ["ESP Type 1", "EspVip", "if _G.LexusConfig.EspVip then"],
  ["ESP Type 3", "EspVipPro", "if _G.LexusConfig.EspVipPro then"],
  ["ESP Type 4", "EspRadar", "if _G.LexusConfig.EspRadar then"],
  ["ESP Type 6", "EspLoai6", "if _G.LexusConfig.EspLoai6 then"],
  ["ESP Type 7 state", "Esp7_TuThe", "if _G.LexusConfig.Esp7_TuThe then"],
  ["horizontal recoil", "CustomHRecoil", "if _G.LexusConfig.CustomHRecoil then"],
  ["vertical recoil", "CustomVRecoil", "if _G.LexusConfig.CustomVRecoil then"],
  ["perfect accuracy", "Accuracy", "if _G.LexusConfig.Accuracy then"],
  ["aimbot head", "AutoHead", "if _G.LexusConfig.AutoHead then"],
  ["vehicle wallhack", "WallVehicle", "if _G.LexusConfig.WallVehicle then"],
  ["tree removal", "RemoveTrees", "if _G.LexusConfig.RemoveTrees"],
  ["fog removal", "RemoveFog", "if _G.LexusConfig.RemoveFog"],
  ["fast flying car", "FastCar", "if _G.LexusConfig.FastCar then"],
];

for (const [label, key, branch] of checks) {
  if (!policy.includes(`"${key}"`)) failures.push(`${label}: forced-off key missing`);
  if (!source.includes(branch)) failures.push(`${label}: expected runtime branch not found for review`);
}

for (const anchor of ["function MainLoop()", "local function InitAllModSystems()", "function _G.InitializeAutoHeadHooks()"]) {
  const start = source.indexOf(anchor);
  const block = start >= 0 ? source.slice(start, start + 1600) : "";
  if (!block.includes("ApplyRemovedFeaturePolicy()")) failures.push(`policy missing from ${anchor}`);
}

if (failures.length > 0) {
  console.error("REMOVED_RUNTIME_BRANCHES_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("REMOVED_RUNTIME_BRANCHES_CHECK=PASS");
console.log(`REVIEWED_FORCED_OFF_BRANCHES=${checks.length}`);
console.log("POLICY_ENFORCED_AT_MAIN_LOOP_AND_AUTHORIZED_BOOTSTRAP=PASS");
