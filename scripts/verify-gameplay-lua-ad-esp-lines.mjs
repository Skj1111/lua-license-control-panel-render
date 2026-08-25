import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-gameplay-lua-ad-esp-lines.mjs <lua-file>");
const source = readFileSync(target, "utf8");

function requireMatch(label, pattern) {
  if (!pattern.test(source)) throw new Error(`AD ESP line regression failed: ${label}`);
}

requireMatch("tracer-line threshold", /local ESPLineGeometryThreshold = 2\.0/);
requireMatch("cached player/bot line color", /local colorKey = isBot and "bot" or "player"/);
requireMatch("cached line endpoints", /lastFromX = nil,[\s\S]{0,180}?lastToY = nil/);
requireMatch("sub-threshold geometry guard", /local geometryChanged = entry\.lastFromX == nil[\s\S]{0,420}?ESPLineGeometryThreshold/);
requireMatch("redraw only when geometry changes", /if geometryChanged then[\s\S]{0,700}?entry\.widget:SetRenderAngle\(angle\)/);

console.log("AD_ESP_LINE_CHECK=PASS");
console.log("LINE_GEOMETRY_THRESHOLD=2.0");
console.log("COLOR_AND_ENDPOINT_CACHE=PASS");
