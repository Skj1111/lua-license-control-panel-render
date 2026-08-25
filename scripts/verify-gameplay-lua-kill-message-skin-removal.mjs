import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-kill-message-skin-removal.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const forbidden = [
  "_G.get_skin_id",
  "CauserWeaponAvatarID",
  "CauserClothAvatarID",
  "_G.OutfitMap.Suit",
  "local skinID",
  "KillMessageEnable",
  "MOD SKIN",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("KILL_MESSAGE_SKIN_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining skin-substitution marker: ${marker}`));
  process.exit(1);
}

const required = [
  "Vehicle, weapon, and outfit skin substitutions are removed.",
  "self:UpdateMainKillCounterUI(true, WeaponID, WeaponID)",
  "local counterAvatarID = weaponID",
];
const missing = required.filter((marker) => !source.includes(marker));
if (missing.length > 0) {
  console.error("KILL_MESSAGE_SKIN_REMOVAL_CHECK=FAIL");
  missing.forEach((marker) => console.error(`- missing preserved counter marker: ${marker}`));
  process.exit(1);
}

console.log("KILL_MESSAGE_SKIN_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_SKIN_SUBSTITUTION_MARKERS=${forbidden.length}`);
