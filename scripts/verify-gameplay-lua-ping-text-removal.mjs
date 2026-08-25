import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-ping-text-removal.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const forbidden = [
  "originalPing = IPS.__inner_impl.SetPingText",
  "IPS.__inner_impl.SetPingText = function",
  'SetText("TG @NEXALORD")',
];
const remaining = forbidden.filter((marker) => source.includes(marker));
if (remaining.length > 0) {
  console.error("PING_TEXT_REMOVAL_CHECK=FAIL");
  remaining.forEach((marker) => console.error(`- remaining custom ping marker: ${marker}`));
  process.exit(1);
}
if (!source.includes("The custom ping-text override was removed.")) {
  console.error("PING_TEXT_REMOVAL_CHECK=FAIL");
  console.error("- missing explicit ping-text removal policy");
  process.exit(1);
}

console.log("PING_TEXT_REMOVAL_CHECK=PASS");
console.log(`FORBIDDEN_PING_MARKERS=${forbidden.length}`);
