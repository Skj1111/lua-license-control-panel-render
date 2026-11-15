import fs from "node:fs";
import path from "node:path";

const target = process.argv[2];
if (!target) {
  console.error("Usage: node scripts/verify-fail-closed-license-loader.mjs /absolute/path/to/loader.lua");
  process.exit(2);
}

const file = path.resolve(target);
const source = fs.readFileSync(file, "utf8");
const failures = [];
const requireMatch = (label, pattern) => {
  if (!pattern.test(source)) failures.push(label);
};

requireMatch("canonical validation endpoint", /local API_URL\s*=\s*"https:\/\/[^"\s]+\/api\/license\/validate"/);
requireMatch("server response event callback", /local response_event\s*=\s*http_wrapper\.OnResponseEvent/);
requireMatch("callback request correlation", /if request_index == nil or reqIndex ~= request_index then return end/);
requireMatch("strict valid response condition", /if response\.status == "valid" and response\.success == true then/);
requireMatch("validated key persistence after valid response", /if response\.status == "valid" and response\.success == true then[\s\S]{0,900}?save_validated_key\(json, key\)/);
requireMatch("authorized continuation only after maintenance gate", /gate_server_maintenance\(json, response, key, on_authorized\)/);
requireMatch("main callback is passed as authorization continuation", /start_license_validation\(run_authorized_main, entered_raw_key/);
requireMatch("top-level does not execute main directly", /if rawget\(_G, KEY_ENTRY_SESSION_GUARD\) ~= true then[\s\S]{0,220}?open_key_entry\(\)[\s\S]{0,80}?end\s*return nil\s*$/m);

if (/^(?![ \t]*\-\-)[^\r\n]*\bloadfile\s*\(/m.test(source)) failures.push("loader must not use loadfile for gameplay continuation");
if (/response\.status\s*==\s*"valid"[\s\S]{0,1200}?on_authorized\([^)]*\)[\s\S]{0,400}?response\.status\s*~=/.test(source)) {
  failures.push("authorized continuation appears reachable outside the valid branch");
}

if (failures.length) {
  console.error("FAIL_CLOSED_LOADER_CHECK=FAIL");
  for (const failure of failures) console.error(`- ${failure}`);
  process.exit(1);
}

console.log("FAIL_CLOSED_LOADER_CHECK=PASS");
console.log(`FILE=${file}`);
console.log("AUTHORIZATION_BOUNDARY=canonical validation callback -> maintenance gate -> authorized main");
