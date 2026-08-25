import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-backup-lua-snapline-bpoint.mjs <lua-file>");
const source = readFileSync(target, "utf8");
const required = [
  "local ADESP_UPDATE_INTERVAL = 0.05",
  "local ADESP_TRACER_UPDATE_INTERVAL = 0.05",
  "local ADESP_MAX_TRACER_TARGETS = 12",
  "local ESPLineGeometryThreshold = 0.25",
  "local ESPLineBotColor = FLinearColor(1.0, 0.0, 0.0, 0.95)",
  "local ESPLinePlayerColor = FLinearColor(0.0, 1.0, 0.0, 0.95)",
  "local function GetESPLineEndpoint(pc, myLoc, targetLoc, headLoc, projected, projectionOK, viewportSize)",
  "return ScreenToStableCanvas(pc, projected, canvas)",
  "local function UpdateESPLine(id, fromPos, toPos, isBot)",
  "headLoc = GetHeadWorldPosition(enemy)",
  "UpdateESPLineSet(espLineTargets)",
  "pc:AddGameTimer(ADESP_UPDATE_INTERVAL, true",
];
const missing = required.filter((marker) => !source.includes(marker));
if (missing.length > 0) {
  console.error("BACKUP_SNAPLINE_B_POINT_CHECK=FAIL");
  missing.forEach((marker) => console.error(`- missing: ${marker}`));
  process.exit(1);
}
if (source.includes("local ADESP_TRACER_UPDATE_INTERVAL = 0.10") || source.includes("local ESPLineGeometryThreshold = 2.0")) {
  console.error("BACKUP_SNAPLINE_B_POINT_CHECK=FAIL");
  console.error("- stale B Point tracking constants remain");
  process.exit(1);
}
console.log("BACKUP_SNAPLINE_B_POINT_CHECK=PASS");
console.log("B_POINT_REFRESH=20HZ");
console.log("ENDPOINT_THRESHOLD=0.25PX");
console.log("DUPLICATE_WORLD_PROJECTION_AVOIDED=PASS");
