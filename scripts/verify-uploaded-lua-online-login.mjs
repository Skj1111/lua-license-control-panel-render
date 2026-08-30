import fs from "node:fs";

const file = process.argv[2];
if (!file) throw new Error("Usage: node verify-uploaded-lua-online-login.mjs <lua-file>");
const source = fs.readFileSync(file, "utf8");
const required = [
  'local ONLINE_VERIFY_URL = "https://luacontrol-udkog8a5.manus.space/api/license/validate"',
  'function _G.NexaLoginPanel.IsAuthorized()',
  'local function online_authorized()',
  'local function StartOnlineLogin()',
  'AddTimerOnce(1.0, StartOnlineLogin)',
  'if not online_authorized() then return false end',
  'if not online_authorized() then return end',
  'response.success == true',
  'response.status == "valid"',
  'NEXA_settings.txt'
];
for (const marker of required) {
  if (!source.includes(marker)) throw new Error(`Missing required online-login marker: ${marker}`);
}
const forbidden = [
  'local limitTime = os.time',
  'isExpired = (currentTime > limitTime)',
  'MOD EXPIRED!',
  'BYPASSED LOCAL LIMIT TIME'
];
for (const marker of forbidden) {
  if (source.includes(marker)) throw new Error(`Offline-expiry marker remains: ${marker}`);
}
if (!source.includes('if not online_authorized() then\n        online_hide_features()\n        return\n    end')) {
  throw new Error("MainLoop is not fail-closed before feature execution");
}
console.log(`PASS online-only login and fail-closed gate: ${file}`);
