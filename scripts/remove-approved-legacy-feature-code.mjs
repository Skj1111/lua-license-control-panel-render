import { readFileSync, writeFileSync } from "node:fs";

const [luaPath, mode] = process.argv.slice(2);
if (!luaPath) {
  throw new Error("Usage: node scripts/remove-approved-legacy-feature-code.mjs <lua-file> [--dry-run]");
}

let source = readFileSync(luaPath, "utf8");
const changes = [];

function requireOnce(marker, label) {
  const count = source.split(marker).length - 1;
  if (count !== 1) throw new Error(`${label}: expected one marker, found ${count}`);
}

function replaceOnce(oldText, newText, label) {
  requireOnce(oldText, label);
  source = source.replace(oldText, newText);
  changes.push(label);
}

function replaceCount(oldText, newText, expectedCount, label) {
  const count = source.split(oldText).length - 1;
  if (count !== expectedCount) throw new Error(`${label}: expected ${expectedCount} markers, found ${count}`);
  source = source.split(oldText).join(newText);
  changes.push(label);
}

function removeBetween(startMarker, endMarker, label) {
  const start = source.indexOf(startMarker);
  if (start < 0) throw new Error(`${label}: start marker missing`);
  const end = source.indexOf(endMarker, start + startMarker.length);
  if (end < 0) throw new Error(`${label}: end marker missing`);
  source = `${source.slice(0, start)}${source.slice(end)}`;
  changes.push(label);
}

// Remove every stale bootstrap invocation now that the policy definition is gone.
replaceCount("    ApplyRemovedFeaturePolicy()\n", "", 3, "obsolete removed-feature policy invocations");

// Auto-head behavior: hook installer, runtime aim mutation, entity aim mutation, and authorized bootstrap call.
removeBetween(
  "-- ========================================== \n-- ============================================================\n-- FEATURE 08: AUTO-HEAD DAMAGE HOOKS\n",
  `-- ==============================================================================
-- ============================================================
-- FEATURE 10:`,
  "auto-head hook section",
);
removeBetween(
  "    PCall(function()\n        local autoComp = localPlayer.AutoAimComp\n",
  "    if _G.LexusConfig.WallClimb then",
  "auto-head main-loop mutation",
);
replaceOnce(
  "                local anyWeaponModOn = _G.LexusConfig.CustomHRecoil or _G.LexusConfig.CustomVRecoil or _G.LexusConfig.LessShake or _G.LexusConfig.Accuracy or _G.LexusConfig.Crosshair or _G.LexusConfig.GodMode or _G.LexusConfig.AutoHead or _G.LexusConfig.CustomAimbot or _G.LexusConfig.CustomAimbotClose or _G.LexusConfig.AimbotMode ~= \"None\" or _G.LexusConfig.LessRecoil or _G.LexusConfig.VerticalRecoil\n",
  "                local anyWeaponModOn = _G.LexusConfig.LessShake or _G.LexusConfig.Crosshair or _G.LexusConfig.GodMode or _G.LexusConfig.CustomAimbot or _G.LexusConfig.CustomAimbotClose or _G.LexusConfig.AimbotMode ~= \"None\" or _G.LexusConfig.LessRecoil or _G.LexusConfig.VerticalRecoil\n",
  "weapon-modifier activation list",
);
for (const [oldText, label] of [
  ["                    if _G.LexusConfig.CustomHRecoil then entity.AccessoriesHRecoilFactor = _G.LexusState.CustomTextData.HRecoil or 0.3 \n                    elseif _G.LexusConfig.LessRecoil then entity.AccessoriesHRecoilFactor = 0.3 end\n", "horizontal recoil branch"],
  ["                    if _G.LexusConfig.CustomVRecoil then entity.AccessoriesVRecoilFactor = _G.LexusState.CustomTextData.VRecoil or 0.3\n                    elseif _G.LexusConfig.VerticalRecoil then entity.AccessoriesVRecoilFactor = 0.3 end\n", "vertical recoil branch"],
  ["                    if _G.LexusConfig.Accuracy then entity.GameDeviationAccuracy = 0.0 end\n", "perfect accuracy branch"],
  ["                        if _G.LexusConfig.AutoHead then\n                            PCall(function() entity.AutoAimingConfig.Bones = { \"Head\", \"Head\", \"Head\" } end)\n                        end\n", "entity auto-head branch"],
  ["        if _G.InitializeAutoHeadHooks then _G.InitializeAutoHeadHooks() end\n", "authorized auto-head bootstrap"],
]) {
  replaceOnce(oldText, "", label);
}

// Vehicle wallhack and fast-car branches are deleted without disturbing other vehicle or movement features.
removeBetween(
  "-- ========================================== \n-- PURPOSE: Vehicle-wall visual handling aur vehicle-related rendering control karta hai.\n",
  "-- ========================================== \n-- ============================================================\n-- FEATURE 18:",
  "vehicle-wall helper section",
);
replaceOnce(
  "    -- [GỌI LOGIC WALL VEHICLE VÀO VÒNG LẶP]\n    if _G.LexusConfig.WallVehicle then\n        _G.RunOptimizedItemAndVehicleESP(pc)\n    end\n",
  "",
  "vehicle-wall main-loop invocation",
);
removeBetween(
  "    if _G.LexusConfig.FastCar then\n",
  "    -- HOÀN TRẢ ĐỒ HỌA NGAY LẬP TỨC NẾU TẮT (TẮT LÀ TẮT LIỀN)\n",
  "fast-car branch",
);

// Requested graphics removals: trees and fog only; grass and BlackSky remain untouched.
removeBetween(
  "            -- LOGIC XÓA CÂY\n",
  "            -- LOGIC BLACKSKY\n",
  "tree-and-fog removal branches",
);

// ESP types 1, 3, 4, 6 and ESP Type 7 posture text are removed; antenna, outline, Type 5/7 weapon info, Type 8, and distance remain.
replaceOnce(
  "                    local showFrameUI = _G.LexusConfig.EspLoai5 or _G.LexusConfig.EspVipPro or _G.LexusConfig.EspVip\n",
  "                    local showFrameUI = _G.LexusConfig.EspLoai5\n",
  "frame-UI dependency list",
);
removeBetween(
  "                    if _G.LexusConfig.EspLoai6 then\n",
  "                    if _G.LexusConfig.EspLoai7 then\n",
  "ESP Type 6 branch",
);
removeBetween(
  "                                    -- 1. Xử lý Tư Thế\n",
  "                                    -- 2. Xử lý Vũ Khí\n",
  "ESP Type 7 posture branch",
);
replaceOnce(
  "                                    -- 2. Xử lý Vũ Khí\n",
  "                                    -- Weapon display remains; the removed posture/state display is not retained.\n                                    -- Xử lý Vũ Khí\n",
  "ESP Type 7 weapon-display heading",
);
removeBetween(
  "                    if _G.LexusConfig.EspVipPro then\n",
  "                    if _G.LexusConfig.EspDistance then\n",
  "ESP Type 3 branch",
);
removeBetween(
  "                    -- [ESP LOẠI 1 (Đã Fix Lỗi)]: Giữ nguyên thanh máu (hpMark) và khoảng cách (distMark)\n",
  "                    -- [ESP LOẠI 8 ĐỘC LẬP",
  "ESP Type 1 branch",
);
removeBetween(
  "                    if _G.LexusConfig.EspRadar then\n",
  "                    -- [ESP OUTLINE",
  "ESP Type 4 branch",
);

if (mode === "--dry-run") {
  console.log("LEGACY_FEATURE_CLEANUP_DRY_RUN=PASS");
  console.log(`PLANNED_CHANGES=${changes.length}`);
  changes.forEach((change) => console.log(`- ${change}`));
} else {
  writeFileSync(luaPath, source);
  console.log("LEGACY_FEATURE_CLEANUP=PASS");
  console.log(`APPLIED_CHANGES=${changes.length}`);
  changes.forEach((change) => console.log(`- ${change}`));
}
