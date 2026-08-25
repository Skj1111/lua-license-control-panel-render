import { readFileSync } from "node:fs";

const target = process.argv[2];
if (!target) throw new Error("Usage: node scripts/verify-gameplay-lua-ad-esp-lines.mjs <lua-file>");
const source = readFileSync(target, "utf8");
const forbidden = [
  "ADESP", "AD ESP", "StackADESP", "ModMenu_ADESP_", "InitializeADESPSystem",
  "UpdateAllESP", "StartEnemyCounter", "ESPLineWidgets", "EnemyCounterWidget",
  "StableEntityBoxes", "StableESPCanvas", "GetESPLineEndpoint",
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("AD_ESP_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining AD ESP marker: ${marker}`));
  process.exit(1);
}
for (const preserved of ["ModMenu_ESP2", "ModMenu_ESP5", "ModMenu_ESP7_Ex", "ModMenu_ESP8", "ModMenu_ESPAntenna"]) {
  if (!source.includes(preserved)) {
    console.error("AD_ESP_REMOVAL_CHECK=FAIL");
    console.error(`- normal ESP marker missing: ${preserved}`);
    process.exit(1);
  }
}
console.log("AD_ESP_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_AD_ESP_MARKERS=${forbidden.length}`);
console.log("NORMAL_ESP_PRESERVED=PASS");
