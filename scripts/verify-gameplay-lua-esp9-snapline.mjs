import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-esp9-snapline.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const requirements = [
  "local function ESP9GetViewportSizeAndScale(pc)",
  "local function ESP9ScreenToCanvasLocal(pc, canvas, screen)",
  "local absolute = FVector2D((screen.X or 0) * scale, (screen.Y or 0) * scale)",
  "local canvasPos, viewport = ESP9ScreenToCanvasLocal(pc, canvas, screen)",
  "local originScreen = FVector2D((viewport.X or 0) * 0.5, 50)",
  "local origin = ESP9ScreenToCanvasLocal(pc, canvas, originScreen)",
  "entry.line:SetRenderTransformPivot(FVector2D(0.0, 0.5))",
  "entry.line:SetRenderTransformAngle(math.deg(math.atan2(dy, dx)))",
  "refreshInterval = 0.10",
  "maxTargets = 12",
];
const missing = requirements.filter((item) => !source.includes(item));
if (missing.length > 0) {
  console.error("ESP9_SNAPLINE_CHECK=FAIL");
  missing.forEach((item) => console.error(`- missing: ${item}`));
  process.exit(1);
}
console.log("ESP9_SNAPLINE_CHECK=PASS");
console.log("VIEWPORT_SCALE_CONVERSION=PASS");
console.log("TOP_CENTER_ORIGIN=PASS");
console.log("LINE_PIVOT_AND_ANGLE=PASS");
