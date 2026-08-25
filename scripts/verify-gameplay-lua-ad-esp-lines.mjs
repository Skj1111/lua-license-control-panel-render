import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-gameplay-lua-ad-esp-lines.mjs <lua-file>");
const source = readFileSync(target, "utf8");

function requireMatch(label, pattern) {
  if (!pattern.test(source)) throw new Error(`AD ESP line regression failed: ${label}`);
}

requireMatch("tracer-line threshold", /local ESPLineGeometryThreshold = 2\.0/);
requireMatch("cached player/bot line color", /local colorKey = isBot and "bot" or "player"/);
requireMatch("cached line visibility", /isVisible = true/);
requireMatch("cached line endpoints", /lastFromX = nil,[\s\S]{0,180}?lastToY = nil/);
requireMatch("sub-threshold geometry guard", /local geometryChanged = entry\.lastFromX == nil[\s\S]{0,420}?ESPLineGeometryThreshold/);
requireMatch("redraw only when geometry changes", /if geometryChanged then[\s\S]{0,700}?entry\.widget:SetRenderAngle\(angle\)/);
requireMatch("visibility redraw only on state change", /if entry\.isVisible ~= true then[\s\S]{0,240}?SetWidgetVisibility\(UEnums\.ESlateVisibility\.SelfHitTestInvisible\)[\s\S]{0,160}?entry\.isVisible = true/);
requireMatch("tracer refresh cadence", /local ADESP_TRACER_UPDATE_INTERVAL = 0\.10/);
requireMatch("tracer target cap", /local ADESP_MAX_TRACER_TARGETS = 12/);
requireMatch("tracer refresh gate", /local tracerRefreshDue = _G\.LexusConfig\.ADESP_ShowTracers[\s\S]{0,180}?ADESP_TRACER_UPDATE_INTERVAL/);
requireMatch("deferred tracer endpoint work", /local espLineCandidates = tracerRefreshDue and \{\} or nil[\s\S]{0,1800}?if tracerRefreshDue then[\s\S]{0,520}?GetESPLineEndpoint/);
requireMatch("nearest-first capped tracer targets", /table\.sort\(espLineCandidates, function\(a, b\) return a\.distRaw < b\.distRaw end\)[\s\S]{0,420}?math\.min\(#espLineCandidates, ADESP_MAX_TRACER_TARGETS\)/);
requireMatch("no repeated disabled-tracer clear scan", /if next\(ESPLineWidgets\) then _G\.ClearESPLineWidgets\(\) end/);

console.log("AD_ESP_LINE_CHECK=PASS");
console.log("LINE_GEOMETRY_THRESHOLD=2.0");
console.log("COLOR_AND_ENDPOINT_CACHE=PASS");
console.log("TRACER_CADENCE_AND_TARGET_CAP=PASS");
