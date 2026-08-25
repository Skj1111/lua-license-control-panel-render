import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-watermark.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const requirements = [
  "local function ApplyFeature03Watermark(self)",
  'watermark:SetText("@NEXA")',
  "watermark:SetColorAndOpacity(FSlateColor(GetRainbowLinearColor()))",
  "watermark:SetRenderOpacity(1.0)",
  "watermark:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible)",
  "IPS.__inner_impl.UpdateArtQualityUI = function(self, ...)",
  "IPS.__inner_impl.TickRefreshBatteryInfo = function(self, ...)",
  "ApplyFeature03Watermark(self)",
  "if not HasPanelAuthorization()",
];
const missing = requirements.filter((item) => !source.includes(item));
if (missing.length > 0) {
  console.error("WATERMARK_CHECK=FAIL");
  missing.forEach((item) => console.error(`- missing: ${item}`));
  process.exit(1);
}
const batteryBlock = source.slice(source.indexOf("IPS.__inner_impl.TickRefreshBatteryInfo"), source.indexOf("-- The custom ping-text override"));
if (!batteryBlock.includes("ApplyFeature03Watermark(self)")) {
  console.error("WATERMARK_CHECK=FAIL");
  console.error("- periodic battery refresh does not apply watermark");
  process.exit(1);
}
console.log("WATERMARK_CHECK=PASS");
console.log("QUALITY_AND_PERIODIC_REFRESH=PASS");
console.log("FORCED_VISIBLE_OPAQUE_TEXT=PASS");
