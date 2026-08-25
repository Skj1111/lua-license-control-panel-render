import { readFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) {
  throw new Error("Usage: node scripts/verify-gameplay-lua-feature03-auth-gate.mjs <lua-file>");
}

const source = readFileSync(luaPath, "utf8");
const failures = [];
const start = source.indexOf("_G.InitializeFeature03PresentationHooks = function()");
const end = source.indexOf("-- FEATURE 04:", start);
const feature03 = start >= 0 && end > start ? source.slice(start, end) : "";

if (!feature03) failures.push("missing deferred Feature 03 initializer");
if (!/if not HasPanelAuthorization\(\) then return false end/.test(feature03)) {
  failures.push("Feature 03 initializer must refuse pre-login installation");
}
if (!/if _G\.Feature03PresentationHooksInitialized then return true end/.test(feature03)) {
  failures.push("Feature 03 initializer must be idempotent");
}
const watermarkHelperStart = source.indexOf("local function ApplyFeature03Watermark(self)");
const watermarkHelper = watermarkHelperStart >= 0 ? source.slice(watermarkHelperStart, start) : "";
if (!/if not HasPanelAuthorization\(\) or not \(self and self\.UIRoot and self\.UIRoot\.TextBlock_quality\) then return false end/.test(watermarkHelper)) {
  failures.push("centralized watermark helper must refuse unauthorized or unavailable UI state");
}
if (!/UpdateArtQualityUI = function\(self, \.\.\.\)[\s\S]{0,420}?ApplyFeature03Watermark\(self\)/.test(feature03)) {
  failures.push("quality wrapper must use the guarded watermark helper");
}
if (!/TickRefreshBatteryInfo = function\(self, \.\.\.\)[\s\S]{0,520}?ApplyFeature03Watermark\(self\)/.test(feature03)) {
  failures.push("periodic battery wrapper must use the guarded watermark helper");
}
if ((feature03.match(/HasPanelAuthorization\(\)/g) ?? []).length < 3) {
  failures.push("Feature 03 initializer, battery, and kill-message paths must remain authorization-gated");
}

const initAllStart = source.indexOf("local function InitAllModSystems()");
const initAll = initAllStart >= 0 ? source.slice(initAllStart, initAllStart + 1000) : "";
if (!/_G\.InitializeFeature03PresentationHooks\(\)/.test(initAll)) {
  failures.push("authorized system bootstrap must initialize Feature 03 hooks");
}

const feature03SectionStart = source.indexOf("-- FEATURE 03: PERMANENT RAINBOW WATERMARK");
const preFeature03 = feature03SectionStart >= 0 ? source.slice(feature03SectionStart, start) : "";
if (feature03SectionStart < 0) failures.push("missing Feature 03 section marker");
if (/^\s*(?:local\s+\w+\s*=\s*require\(|\w+\.__inner_impl\.[A-Za-z_]+\s*=\s*function)/m.test(preFeature03)) {
  failures.push("Feature 03 must not install hooks before the deferred initializer");
}

if (failures.length > 0) {
  console.error("FEATURE03_AUTH_GATE_CHECK=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}

console.log("FEATURE03_AUTH_GATE_CHECK=PASS");
console.log("DEFERRED_FEATURE03_HOOKS=PASS");
console.log("FEATURE03_WRAPPER_GATES=PASS");
