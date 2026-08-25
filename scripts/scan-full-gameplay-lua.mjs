import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) {
  throw new Error("Usage: node scripts/scan-full-gameplay-lua.mjs <lua-file>");
}

const source = readFileSync(luaPath, "utf8");
const lines = source.split(/\r?\n/);
const localFunctionLines = new Map();

for (const [index, line] of lines.entries()) {
  const match = line.match(/^\s*local\s+function\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(/);
  if (!match) continue;
  const entries = localFunctionLines.get(match[1]) ?? [];
  entries.push(index + 1);
  localFunctionLines.set(match[1], entries);
}

const duplicates = [...localFunctionLines.entries()].filter(([, lineNumbers]) => lineNumbers.length > 1);
const findings = [];

if (duplicates.length > 0) {
  findings.push({
    severity: "error",
    code: "DUPLICATE_LOCAL_FUNCTION",
    detail: duplicates.map(([name, lineNumbers]) => `${name}@${lineNumbers.join(",")}`).join("; "),
  });
}

if (source.includes("string.sformat")) {
  findings.push({
    severity: "error",
    code: "INVALID_STRING_API_GUARD",
    detail: "Found string.sformat; Lua provides string.format, so this guard always falls back.",
  });
}

const requiredInvariants = [
  ["CANONICAL_ENDPOINT", 'local VERIFY_URL = "https://luacontrol-udkog8a5.manus.space/api/license/validate"'],
  ["RESPONSE_CALLBACK_SELF", "PCall(add_response, response_event, function"],
  ["VALID_RESPONSE_GATE", 'response.success == true\n            and response.status == "valid"'],
  ["KEY_WRITE_AFTER_VALID_RESPONSE", "if approved then\n            if not save_validated_panel_key"],
  ["AUTH_MAIN_LOOP_GATE", "function MainLoop() \n    local isAuthorized = HasPanelAuthorization()"],
  ["LOGIN_STARTUP_TIMER", "timer.AddTimerOnce(1.0, CheckLoginAndInit)"],
];

for (const [code, marker] of requiredInvariants) {
  if (!source.includes(marker)) {
    findings.push({ severity: "error", code, detail: "Required runtime invariant is missing." });
  }
}

const unsafeTopLevelPCall = lines
  .slice(0, 21)
  .map((line, index) => ({ line, lineNumber: index + 1 }))
  .filter(({ line }) => /^\s*PCall\(/.test(line));
if (unsafeTopLevelPCall.length > 0) {
  findings.push({
    severity: "warning",
    code: "EARLY_PCALL_INVOCATION",
    detail: unsafeTopLevelPCall.map(({ lineNumber }) => `line ${lineNumber}`).join(", "),
  });
}

const status = findings.some(({ severity }) => severity === "error") ? "FAIL" : "PASS";
console.log(`FULL_LUA_DIAGNOSTIC=${status}`);
console.log(`SOURCE_LINES=${lines.length}`);
console.log(`LOCAL_FUNCTIONS=${localFunctionLines.size}`);
console.log(`DUPLICATE_LOCAL_FUNCTIONS=${duplicates.length}`);
console.log(`FINDINGS=${findings.length}`);
for (const finding of findings) {
  console.log(`${finding.severity.toUpperCase()} ${finding.code}: ${finding.detail}`);
}

if (status === "FAIL") process.exitCode = 1;
