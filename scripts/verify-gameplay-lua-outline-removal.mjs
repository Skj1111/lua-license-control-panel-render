import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-outline-removal.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const forbidden = [
  "ModMenu_ESPOutline_Ex", "ModMenu_ESPOutline_Thickness", "EspOutline", "OutlineThickness",
  "SetDrawIdeaOutline", "OverrideIdeaOutlineColor", "OverrideIdeaOutlineThickness", "EnableAvatarOutline", "Cached_PPM",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("ESP_OUTLINE_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining outline marker: ${marker}`));
  process.exit(1);
}
for (const preserved of ["ModMenu_ESPAntenna", "EspAntenna", "EspLoai8"]) {
  if (!source.includes(preserved)) {
    console.error("ESP_OUTLINE_REMOVAL_CHECK=FAIL");
    console.error(`- unrelated ESP marker missing: ${preserved}`);
    process.exit(1);
  }
}
console.log("ESP_OUTLINE_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_OUTLINE_MARKERS=${forbidden.length}`);
