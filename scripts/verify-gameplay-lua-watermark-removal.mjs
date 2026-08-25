import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-watermark-removal.mjs <lua-file>");
const source = readFileSync(luaPath, "utf8");
const forbidden = [
  "PERMANENT RAINBOW WATERMARK", "GetRainbowLinearColor", "ApplyFeature03Watermark",
  "TextBlock_quality", 'SetText("@NEXA")', "UpdateArtQualityUI",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("WATERMARK_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining watermark marker: ${marker}`));
  process.exit(1);
}
for (const preserved of ["TickRefreshBatteryInfo", "ProgressBar_Battery", "CopyKillOrPutDownMessageDataUserDataToLuaTable"]) {
  if (!source.includes(preserved)) {
    console.error("WATERMARK_REMOVAL_CHECK=FAIL");
    console.error(`- unrelated Feature 03 behavior missing: ${preserved}`);
    process.exit(1);
  }
}
console.log("WATERMARK_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_WATERMARK_MARKERS=${forbidden.length}`);
