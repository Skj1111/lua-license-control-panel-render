import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-esp-antenna.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const failures = [];
const branchStart = source.indexOf("if _G.LexusConfig.EspAntenna then");
const branch = branchStart >= 0 ? source.slice(branchStart, branchStart + 1800) : "";

for (const marker of [
  'Key = "ModMenu_ESPAntenna"',
  "if _G.LexusConfig.EspAntenna then",
  'type(MyHUD.AddDebugText) == "function"',
  "local antennaColor = {R=0, G=255, B=0, A=255}",
  "antennaColor, true, false, true",
  "distM <= 400",
]) {
  if (!source.includes(marker) && !branch.includes(marker)) failures.push(`missing antenna marker: ${marker}`);
}
if (branch.includes("C_GREEN, true, false, true")) failures.push("antenna renderer must not use legacy transparent C_GREEN");
if (!source.includes("shared legacy C_GREEN table has A=0")) failures.push("opaque-color rationale is missing");

if (failures.length > 0) {
  console.error("ESP_ANTENNA_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("ESP_ANTENNA_CHECK=PASS");
console.log("OPAQUE_RENDER_COLOR=PASS");
console.log("HUD_METHOD_GUARD=PASS");
