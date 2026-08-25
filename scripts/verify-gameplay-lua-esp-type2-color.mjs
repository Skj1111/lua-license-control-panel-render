import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-esp-type2-color.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const failures = [];

if (!source.includes("local C_BLUE_TEXT = {R=0, G=170, B=255, A=255}")) {
  failures.push("ESP Type 2 must use the visible opaque blue C_BLUE_TEXT value");
}
if (!source.includes('hud:AddDebugText(string.format("[%dm]", math.floor(distM))')) {
  failures.push("ESP Type 2 distance draw call is missing");
}
if (!source.includes("C_BLUE_TEXT, true, false, true")) {
  failures.push("ESP Type 2 distance draw call must use C_BLUE_TEXT");
}
if (source.includes("local C_BLUE_TEXT = {R=0, G=0, B=0, A=0}")) {
  failures.push("legacy transparent Type 2 color remains");
}
if (failures.length > 0) {
  console.error("ESP_TYPE2_COLOR_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}
console.log("ESP_TYPE2_COLOR_CHECK=PASS");
console.log("TYPE2_COLOR=RGBA(0,170,255,255)");
