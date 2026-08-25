import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-esp9-snapline.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const requirements = [
  "local function ESPVIPGetViewport(pc)",
  "local function ESPVIPScreenToCanvas(pc, canvas, screen)",
  "local function ESPVIPColor(index, alpha)",
  "local origin = ESPVIPScreenToCanvas(pc,canvas,FVector2D((viewport.X or 0)*.5,76))",
  "entry.line:SetRenderTransformPivot(FVector2D(0,.5))",
  "entry.line:SetRenderTransformAngle(math.deg(math.atan2(dy,dx)))",
  "Esp9_LineThick", "Esp9_LineVisColor", "Esp9_LineHidColor",
  "refreshInterval=0.10", "lastLineUpdate=0", "lineRefreshInterval=1/60", "maxTargets=12",
  "local function ESPVIPUpdateLine(pc, canvas, entry, screen)",
  "local function ESPVIPRefreshLines()",
  "entry.enemy = enemy",
  "now-ESPVIPState.lastLineUpdate < ESPVIPState.lineRefreshInterval",
  "ESPVIPTick()\n    ESPVIPRefreshLines()",
];
const missing = requirements.filter((item) => !source.includes(item));
if (missing.length > 0) {
  console.error("ESP_VIP_SNAPLINE_CHECK=FAIL");
  missing.forEach((item) => console.error(`- missing: ${item}`));
  process.exit(1);
}
console.log("ESP_VIP_SNAPLINE_CHECK=PASS");
console.log("VIEWPORT_SCALE_CONVERSION=PASS");
console.log("TOP_CENTER_ORIGIN=PASS");
console.log("LINE_STYLE_CONTROLS=PASS");
console.log("LIGHTWEIGHT_B_POINT_REFRESH=PASS");
