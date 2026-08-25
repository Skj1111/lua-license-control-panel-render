import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/verify-gameplay-lua-announcement.mjs <lua-file>");

const source = readFileSync(luaPath, "utf8");
const required = [
  ["canonical endpoint", 'local ANNOUNCEMENT_URL = "https://luacontrol-udkog8a5.manus.space/api/announcement/active"'],
  ["non-blocking fetch helper", "fetchActivePanelAnnouncement = function()"],
  ["POST bridge request", 'request_for_lua(http_wrapper, ANNOUNCEMENT_URL, "POST", headers, body, 0, 0)'],
  ["active response gate", "response.active ~= true"],
  ["once-per-day persistence", "ANNOUNCEMENT_SEEN_FILE"],
  ["native popup delivery", "showPanelAnnouncement(title, message)"],
  ["post-validation trigger", "if fetchActivePanelAnnouncement then PCall(fetchActivePanelAnnouncement) end"],
];

const missing = required.filter(([, marker]) => !source.includes(marker)).map(([name]) => name);
if (missing.length > 0) {
  console.error("ANNOUNCEMENT_LUA_CHECK=FAIL");
  missing.forEach((name) => console.error(`- missing ${name}`));
  process.exit(1);
}

const fetchStart = source.indexOf("fetchActivePanelAnnouncement = function()");
const fetchEnd = source.indexOf("function NexaLicenseClient.verify", fetchStart);
const fetchBlock = fetchStart >= 0 && fetchEnd > fetchStart ? source.slice(fetchStart, fetchEnd) : "";
const popupStart = source.indexOf("local function showPanelAnnouncement(title, message)");
const popupEnd = source.indexOf("fetchActivePanelAnnouncement = function()", popupStart);
const popupBlock = popupStart >= 0 && popupEnd > popupStart ? source.slice(popupStart, popupEnd) : "";
const presentationChecks = [
  ["active payload gate", "response.active ~= true then return end"],
  ["request-index isolation", "reqIndex ~= request_index"],
  ["once-per-day de-duplication", "read_announcement_seen() == seenToken"],
  ["active response does not alter license state", "setNexaState("],
];
for (const [name, marker] of presentationChecks) {
  const found = fetchBlock.includes(marker);
  if (name === "active response does not alter license state") {
    if (found) {
      console.error("ANNOUNCEMENT_LUA_CHECK=FAIL");
      console.error("- active announcement path must not alter authorization state");
      process.exit(1);
    }
  } else if (!found) {
    console.error("ANNOUNCEMENT_LUA_CHECK=FAIL");
    console.error(`- missing presentation safeguard: ${name}`);
    process.exit(1);
  }
}

if (!popupBlock.includes("Msg.Show(1, safeTitle, safeMessage")) {
  console.error("ANNOUNCEMENT_LUA_CHECK=FAIL");
  console.error("- missing presentation safeguard: native message invocation");
  process.exit(1);
}

console.log("ANNOUNCEMENT_LUA_CHECK=PASS");
console.log(`ANNOUNCEMENT_WIRING_MARKERS=${required.length + presentationChecks.length}`);
console.log("ACTIVE_ANNOUNCEMENT_PRESENTATION_PATH=PASS");
