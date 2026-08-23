import fs from "node:fs";
import path from "node:path";

const target = process.argv[2];
if (!target) {
  console.error("Usage: node scripts/verify-gameplay-lua-key-entry.mjs /absolute/path/to/BRPlayerCharacterBase.lua");
  process.exit(2);
}

const source = fs.readFileSync(path.resolve(target), "utf8");
const failures = [];

function requireMatch(label, pattern) {
  if (!pattern.test(source)) failures.push(label);
}

requireMatch("native input blueprint", /KEY_ENTRY_BLUEPRINT\s*=\s*"\/Game\/UMG\/UI_BP\/Home\/Common\/Popup\/Small\/Home_Input_Popups_UIBP/);
requireMatch("popup title", /SetData\(self, "ENTER YOUR KEY"\)/);
requireMatch("popup controls", /Button_OK[\s\S]{0,450}?Button_Cancel/);
requireMatch("key character validation", /key:match\("\^\[A-Za-z0-9%-\]\+\$"\)/);
requireMatch("key json encoder", /save_validated_panel_key\(json, key\)[\s\S]{0,650}?json\.encode\(\{ key = key \}\)/);
requireMatch("key json target", /write_key_file\(KEY_FILE_CANDIDATES\[1\], encoded\)/);
requireMatch("valid response before write", /if approved then[\s\S]{0,600}?save_validated_panel_key\(json, key\)[\s\S]{0,600}?setNexaState\("authorized", response\)/);
requireMatch("invalid response does not write", /else[\s\S]{0,500}?setNexaState\("disabled", response\)/);
requireMatch("canonical payload", /json\.encode\(\{[\s\S]{0,120}?key = key,[\s\S]{0,120}?device_id = installation_id/);
requireMatch("popup opens without saved key", /if not read_saved_panel_key\(get_json\(\)\) then[\s\S]{0,250}?OpenKeyEntryPopup\(\)/);
requireMatch("popup submit enters validation", /ValidateKeyAndExpiry\(function\(valid, data\)[\s\S]{0,600}?end, entered_key\)/);
requireMatch("cancel provides retry", /function Popup:OnCancel\(\)[\s\S]{0,350}?_G\.ScheduleNexaKeyEntryRetry\(0\.2\)/);
requireMatch("retry entry point", /_G\.OpenNexaKeyEntry = OpenKeyEntryPopup/);
requireMatch("bounded retry scheduler", /function _G\.ScheduleNexaKeyEntryRetry\(delay\)[\s\S]{0,1200}?_G\.NexaKeyEntryRetryCount > 3/);
requireMatch("popup unavailable retry", /key entry UI unavailable[\s\S]{0,200}?ScheduleNexaKeyEntryRetry\(1\.0\)/);
requireMatch("invalid saved key retry", /Msg\.Show\(1, "LOGIN FAILED"[\s\S]{0,750}?ScheduleNexaKeyEntryRetry\(0\.2\)/);
requireMatch("submitted invalid key retry", /local function on_submit\(entered_key\)[\s\S]{0,1000}?else[\s\S]{0,250}?ScheduleNexaKeyEntryRetry\(0\.2\)/);

if (failures.length > 0) {
  console.error("KEY_ENTRY_CHECK=FAIL");
  for (const failure of failures) console.error(`- ${failure}`);
  process.exit(1);
}

console.log("KEY_ENTRY_CHECK=PASS");
console.log("INPUT_POPUP=PASS");
console.log("KEY_JSON_WRITE_AFTER_VALID_RESPONSE_ONLY=PASS");
console.log("CANONICAL_VALIDATION_HANDOFF=PASS");
console.log("CANCEL_RETRY_FLOW=PASS");
