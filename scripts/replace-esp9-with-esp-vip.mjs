import { readFileSync, writeFileSync } from "node:fs";

const [luaPath] = process.argv.slice(2);
if (!luaPath) throw new Error("Usage: node scripts/replace-esp9-with-esp-vip.mjs <lua-file>");

let source = readFileSync(luaPath, "utf8");
const failures = [];

function replaceOnce(start, end, replacement, label) {
  const startAt = source.indexOf(start);
  if (startAt < 0) return failures.push(`${label}: start marker missing`);
  const endAt = source.indexOf(end, startAt);
  if (endAt < 0) return failures.push(`${label}: end marker missing`);
  source = source.slice(0, startAt) + replacement + source.slice(endAt);
}

const oldConfig = `    ESP9Visual = false,
    ESP9_Name = true,
    ESP9_Distance = true,
    ESP9_HP = true,
    ESP9_TeamBox = true,
    ESP9_Weapon = true,
    ESP9_Snapline = true,`;
const newConfig = `    EspLoai9 = false,
    Esp9_Count = true,
    Esp9_Name = true,
    Esp9_Distance = true,
    Esp9_HP = true,
    Esp9_Team = true,
    Esp9_Weapon = true,
    Esp9_Line = true,`;
if (!source.includes(oldConfig)) failures.push("ESP VIP config boundary missing");
else source = source.replace(oldConfig, newConfig);

source = source.replace(`[999010] = "AD ESP 9 VISUAL",`, `[999010] = "ESP VIP (RedBox & Marker)",`);

const vipMenu = `        local StackESPVIP = {
            { Key = "ModMenu_ESPVIP_Ex", UI = AliasMap.TitleSwitcher, Text = "▶ ESP VIP (RedBox & Marker)", ExpandIndex = 0, GetFunc = function() return _G.LexusConfig.EspLoai9 == true end, SetFunc = function(c,v) _G.LexusConfig.EspLoai9 = v return true end },
            { Key = "ModMenu_ESPVIP_Count", UI = AliasMap.Switcher, Text = "   Show Player Count", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_Count == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Count = v return true end },
            { Key = "ModMenu_ESPVIP_Name", UI = AliasMap.Switcher, Text = "   Show Player Name", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_Name == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Name = v return true end },
            { Key = "ModMenu_ESPVIP_Distance", UI = AliasMap.Switcher, Text = "   Show Distance", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_Distance == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Distance = v return true end },
            { Key = "ModMenu_ESPVIP_HP", UI = AliasMap.Switcher, Text = "   Show Health Bar", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_HP == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_HP = v return true end },
            { Key = "ModMenu_ESPVIP_Team", UI = AliasMap.Switcher, Text = "   Show Team Color Box", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_Team == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Team = v return true end },
            { Key = "ModMenu_ESPVIP_Weapon", UI = AliasMap.Switcher, Text = "   Show Weapon Icon", ExpandHandle = "ModMenu_ESPVIP_Ex", GetFunc = function() return _G.LexusConfig.Esp9_Weapon == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Weapon = v return true end },
            { Key = "ModMenu_ESPVIP_Line", UI = AliasMap.TitleSwitcher, Text = "   ▶ Show Snapline", ExpandHandle = "ModMenu_ESPVIP_Ex", ExpandIndex = 0, GetFunc = function() return _G.LexusConfig.Esp9_Line == true end, SetFunc = function(c,v) _G.LexusConfig.Esp9_Line = v return true end },
            { Key = "ModMenu_ESPVIP_Line_Thick", UI = AliasMap.Slider, Text = "      Line Thickness", ExpandHandle = "ModMenu_ESPVIP_Line", MinValue = 1, MaxValue = 10, min = 1, max = 10, GetFunc = function() return _G.LexusState.CustomTextData.Esp9_LineThick or 1 end, SetFunc = function(c,v) _G.LexusState.CustomTextData.Esp9_LineThick = v return true end },
            { Key = "ModMenu_ESPVIP_Line_VisColor", UI = AliasMap.Slider, Text = "      Visible Color (1-30)", ExpandHandle = "ModMenu_ESPVIP_Line", MinValue = 1, MaxValue = 30, min = 1, max = 30, GetFunc = function() return _G.LexusState.CustomTextData.Esp9_LineVisColor or 2 end, SetFunc = function(c,v) _G.LexusState.CustomTextData.Esp9_LineVisColor = v return true end },
            { Key = "ModMenu_ESPVIP_Line_HidColor", UI = AliasMap.Slider, Text = "      Hidden Color (1-30)", ExpandHandle = "ModMenu_ESPVIP_Line", MinValue = 1, MaxValue = 30, min = 1, max = 30, GetFunc = function() return _G.LexusState.CustomTextData.Esp9_LineHidColor or 1 end, SetFunc = function(c,v) _G.LexusState.CustomTextData.Esp9_LineHidColor = v return true end }
        }

`;
replaceOnce("        local StackADESP9 = {", "        local StackESP = {", vipMenu, "ESP VIP menu");
source = source.replace("GuardMenuStack(StackADESP9)", "GuardMenuStack(StackESPVIP)");
source = source.replace("{ Key = \"Cat_ADESP9\", Text = 999010, Stack = StackADESP9 }", "{ Key = \"Cat_ESPVIP\", Text = 999010, Stack = StackESPVIP }");

const vipRuntime = `-- ==============================================================================
-- ESP VIP REDBOX + MARKER VISUAL-ONLY AD ESP
-- Replaces the prior ESP9 renderer. Bounded marker cards, RedBox count banner,
-- configurable Snapline style, authorization gating, and no bypass behavior.
-- ==============================================================================
local ESPVIPState = {canvas=nil, entries={}, counter=nil, counterSlot=nil, counterText=nil, lastUpdate=0, refreshInterval=0.10, maxTargets=12, weaponCacheInterval=1.50, lastCounterText=""}

local function ESPVIPIsValid(object)
    return object and slua and slua.isValid and slua.isValid(object)
end

local function ESPVIPCollapse(widget)
    if ESPVIPIsValid(widget) then PCall(function() widget:SetWidgetVisibility(UEnums.ESlateVisibility.Collapsed) end) end
end

local function ESPVIPGetCanvas()
    if ESPVIPIsValid(ESPVIPState.canvas) then return ESPVIPState.canvas end
    local tools = nil
    PCall(function() tools = require("GameLua.Mod.BaseMod.Common.UI.InGameUITools") end)
    if not tools or not tools.GetMainControlBaseUI then return nil end
    local mainUI = nil
    PCall(function() mainUI = tools.GetMainControlBaseUI() end)
    if not ESPVIPIsValid(mainUI) then return nil end
    PCall(function()
        if ESPVIPIsValid(mainUI.CanvasPanel_0) then ESPVIPState.canvas = mainUI.CanvasPanel_0
        elseif ESPVIPIsValid(mainUI.CanvasPanel_42) then ESPVIPState.canvas = mainUI.CanvasPanel_42 end
    end)
    return ESPVIPState.canvas
end

local function ESPVIPMakeWidget(path, owner)
    if not CGame or not CGame.NewObjectFromPath then return nil end
    local widget = nil
    PCall(function() widget = CGame:NewObjectFromPath(path, owner) end)
    return ESPVIPIsValid(widget) and widget or nil
end

local function ESPVIPAddChild(parent, child)
    if not ESPVIPIsValid(parent) or not ESPVIPIsValid(child) then return nil end
    local slot = nil
    PCall(function() slot = parent:AddChildToCanvas(child) end)
    return ESPVIPIsValid(slot) and slot or nil
end

local function ESPVIPGetViewport(pc)
    local viewport, scale = FVector2D(0, 0), 1.0
    PCall(function() if pc and pc.GetViewportSize then pc:GetViewportSize(viewport) end end)
    PCall(function()
        local layout = import("WidgetLayoutLibrary")
        if (not viewport.X or viewport.X <= 200) and layout and layout.GetViewportSize then viewport = layout.GetViewportSize(pc) end
        if layout and layout.GetViewportScale then local s = layout.GetViewportScale(pc); if type(s) == "number" and s > 0 then scale = s end end
    end)
    return viewport, scale
end

local function ESPVIPScreenToCanvas(pc, canvas, screen)
    local viewport, scale = ESPVIPGetViewport(pc)
    local absolute, localPoint = FVector2D((screen.X or 0) * scale, (screen.Y or 0) * scale), nil
    PCall(function()
        local slate, geometry = import("SlateBlueprintLibrary"), canvas:GetCachedGeometry()
        if slate and slate.AbsoluteToLocal and geometry then localPoint = slate.AbsoluteToLocal(geometry, absolute) end
    end)
    return localPoint or screen, viewport
end

local function ESPVIPColor(index, alpha)
    local colors = {{1,0,0},{0,1,0},{0,0,1},{1,1,0},{1,0,1},{1,1,1},{0,1,1},{1,.5,0},{1,.4,.7},{.6,.3,0},{.5,1,0},{0,.5,.5},{0,0,.5},{.5,0,0},{.5,.5,0},{.75,.75,.75},{1,.84,0},{.5,0,1},{.53,.81,.92},{1,.5,.31},{.98,.5,.45},{.94,.9,.55},{.87,.63,.87},{.85,.44,.84},{.29,0,.51},{.25,.88,.82},{.6,1,.6},{.86,.08,.24},{.82,.41,.12},{.5,1,.83}}
    index = math.floor(tonumber(index) or 1); if index < 1 or index > #colors then index = 1 end
    local c = colors[index]
    return FLinearColor(c[1], c[2], c[3], alpha or 1.0)
end

local function ESPVIPTeamColor(teamID, isBot)
    if isBot then return FLinearColor(1.0, 0.34, 0.10, 0.95) end
    local palette = {FLinearColor(0.0,0.85,1.0,0.95),FLinearColor(0.65,0.35,1.0,0.95),FLinearColor(1.0,0.8,0.0,0.95),FLinearColor(0.2,1.0,0.35,0.95),FLinearColor(1.0,0.25,0.55,0.95),FLinearColor(0.9,0.9,0.95,0.95)}
    return palette[((tonumber(teamID) or 1) % #palette) + 1]
end

local function ESPVIPCreateCounter()
    if ESPVIPIsValid(ESPVIPState.counter) then return true end
    local canvas = ESPVIPGetCanvas(); if not ESPVIPIsValid(canvas) then return false end
    local root, rootSlot = ESPVIPMakeWidget("/Script/UMG.CanvasPanel", canvas), nil
    if root then rootSlot = ESPVIPAddChild(canvas, root) end
    if not rootSlot then return false end
    local layers = {{300,42,.003,.003,.006,.62},{288,40,.90,.005,.018,.12},{276,38,.62,.003,.014,.24},{264,36,.70,.004,.018,.42},{252,34,.78,.006,.022,.64},{240,32,.84,.008,.026,.86},{228,30,.88,.010,.030,1.0}}
    for z, cfg in ipairs(layers) do
        local border, slot = ESPVIPMakeWidget("/Script/UMG.Border", root), nil
        if border then slot = ESPVIPAddChild(root, border) end
        if slot then PCall(function() border:SetBrushColor(FLinearColor(cfg[3],cfg[4],cfg[5],cfg[6])); border:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible); slot:SetPosition(FVector2D((300-cfg[1])*0.5,(42-cfg[2])*0.5)); slot:SetSize(FVector2D(cfg[1],cfg[2])); slot:SetZOrder(z) end) end
    end
    local text, textSlot = ESPVIPMakeWidget("/Script/UMG.TextBlock", root), nil
    if text then textSlot = ESPVIPAddChild(root, text) end
    if not textSlot then return false end
    PCall(function()
        rootSlot:SetAutoSize(false); rootSlot:SetAlignment(FVector2D(0.5,0)); rootSlot:SetSize(FVector2D(300,42)); rootSlot:SetZOrder(10790)
        textSlot:SetAutoSize(true); textSlot:SetAlignment(FVector2D(0.5,0.5)); textSlot:SetPosition(FVector2D(150,21)); textSlot:SetZOrder(1000)
        text:SetColorAndOpacity(FSlateColor(FLinearColor(1,1,1,1))); text:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible)
        if text.Font then local font = text.Font; font.Size = 15; text:SetFont(font) end
        root:SetWidgetVisibility(UEnums.ESlateVisibility.Collapsed)
    end)
    ESPVIPState.counter, ESPVIPState.counterSlot, ESPVIPState.counterText = root, rootSlot, text
    return true
end

local function ESPVIPUpdateCounter(pc, players, bots, nearest)
    if not _G.LexusConfig.Esp9_Count then ESPVIPCollapse(ESPVIPState.counter); return end
    if not ESPVIPCreateCounter() then return end
    local canvas = ESPVIPGetCanvas(); if not canvas then return end
    local viewport = ESPVIPGetViewport(pc)
    local origin = ESPVIPScreenToCanvas(pc, canvas, FVector2D((viewport.X or 0)*0.5, 46))
    local text = string.format("Player: %d   Bot: %d   Dist: %dm", players, bots, nearest or 0)
    PCall(function()
        if ESPVIPState.lastCounterText ~= text then ESPVIPState.counterText:SetText(text); ESPVIPState.lastCounterText = text end
        ESPVIPState.counterSlot:SetPosition(FVector2D(origin.X, origin.Y)); ESPVIPState.counter:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible)
    end)
end

local function ESPVIPRemoveEntry(id, entry)
    if entry then
        ESPVIPCollapse(entry.root); ESPVIPCollapse(entry.line)
        PCall(function() if entry.root and entry.root.RemoveFromParent then entry.root:RemoveFromParent() end end)
        PCall(function() if entry.line and entry.line.RemoveFromParent then entry.line:RemoveFromParent() end end)
    end
    ESPVIPState.entries[id] = nil
end

local function ESPVIPClear()
    for id, entry in pairs(ESPVIPState.entries) do ESPVIPRemoveEntry(id, entry) end
    ESPVIPState.entries = {}
    ESPVIPCollapse(ESPVIPState.counter)
    PCall(function() if ESPVIPState.counter and ESPVIPState.counter.RemoveFromParent then ESPVIPState.counter:RemoveFromParent() end end)
    ESPVIPState.canvas, ESPVIPState.counter, ESPVIPState.counterSlot, ESPVIPState.counterText = nil, nil, nil, nil
    ESPVIPState.lastCounterText, ESPVIPState.lastUpdate = "", 0
end

local function ESPVIPCreateMarker(id)
    local existing = ESPVIPState.entries[id]; if existing and ESPVIPIsValid(existing.root) then return existing end
    local canvas = ESPVIPGetCanvas(); if not ESPVIPIsValid(canvas) then return nil end
    local root, rootSlot = ESPVIPMakeWidget("/Script/UMG.CanvasPanel", canvas), nil
    if root then rootSlot = ESPVIPAddChild(canvas, root) end
    if not rootSlot then return nil end
    local border, borderSlot = ESPVIPMakeWidget("/Script/UMG.Border", root), nil; if border then borderSlot = ESPVIPAddChild(root,border) end
    local label, labelSlot = ESPVIPMakeWidget("/Script/UMG.TextBlock", root), nil; if label then labelSlot = ESPVIPAddChild(root,label) end
    local hpTrack, hpTrackSlot = ESPVIPMakeWidget("/Script/UMG.Border", root), nil; if hpTrack then hpTrackSlot = ESPVIPAddChild(root,hpTrack) end
    local hpBar, hpBarSlot = ESPVIPMakeWidget("/Script/UMG.ProgressBar", root), nil; if hpBar then hpBarSlot = ESPVIPAddChild(root,hpBar) end
    local weapon, weaponSlot = ESPVIPMakeWidget("/Script/UMG.Image", root), nil; if weapon then weaponSlot = ESPVIPAddChild(root,weapon) end
    local line, lineSlot = ESPVIPMakeWidget("/Script/UMG.Border", canvas), nil; if line then lineSlot = ESPVIPAddChild(canvas,line) end
    if not borderSlot or not labelSlot then return nil end
    PCall(function()
        rootSlot:SetAutoSize(false); rootSlot:SetAlignment(FVector2D(.5,1)); rootSlot:SetSize(FVector2D(128,56)); rootSlot:SetZOrder(10760)
        borderSlot:SetPosition(FVector2D(0,0)); borderSlot:SetSize(FVector2D(128,56)); borderSlot:SetZOrder(0)
        labelSlot:SetPosition(FVector2D(64,10)); labelSlot:SetAutoSize(true); labelSlot:SetAlignment(FVector2D(.5,.5)); labelSlot:SetZOrder(10)
        if label.Font then local font = label.Font; font.Size=10; label:SetFont(font) end
        if hpTrackSlot then hpTrackSlot:SetPosition(FVector2D(4,43)); hpTrackSlot:SetSize(FVector2D(120,5)); hpTrackSlot:SetZOrder(11) end
        if hpTrack then hpTrack:SetBrushColor(FLinearColor(.02,.02,.02,1)) end
        if hpBarSlot then hpBarSlot:SetPosition(FVector2D(4,43)); hpBarSlot:SetSize(FVector2D(120,5)); hpBarSlot:SetZOrder(12) end
        if weaponSlot then weaponSlot:SetPosition(FVector2D(49,-20)); weaponSlot:SetSize(FVector2D(30,18)); weaponSlot:SetZOrder(15) end
        if lineSlot then lineSlot:SetZOrder(10755); lineSlot:SetSize(FVector2D(0,1)) end
        root:SetWidgetVisibility(UEnums.ESlateVisibility.Collapsed); ESPVIPCollapse(weapon); ESPVIPCollapse(line)
    end)
    local entry = {root=root,rootSlot=rootSlot,border=border,label=label,hpTrack=hpTrack,hpBar=hpBar,weapon=weapon,weaponSlot=weaponSlot,line=line,lineSlot=lineSlot,lastText="",lastWeapon="",nextWeaponCheck=0}
    ESPVIPState.entries[id] = entry
    return entry
end

local function ESPVIPWeaponIcon(enemy, entry, now)
    if not _G.LexusConfig.Esp9_Weapon then return nil end
    if now < (entry.nextWeaponCheck or 0) then return entry.cachedWeaponIcon end
    entry.nextWeaponCheck = now + ESPVIPState.weaponCacheInterval
    local iconPath = nil; PCall(function() if _G.AK_GetCharacterWeapon then iconPath = _G.AK_GetCharacterWeapon(enemy) end end)
    entry.cachedWeaponIcon = iconPath
    return iconPath
end

local function ESPVIPUpdateMarker(pc, enemy, screen, distanceRaw, isBot, now)
    local id, entry = tostring(enemy), ESPVIPCreateMarker(tostring(enemy)); if not entry then return false end
    local canvas = ESPVIPGetCanvas(); local pos, viewport = ESPVIPScreenToCanvas(pc, canvas, screen)
    local meters = math.max(0, math.floor(distanceRaw / 100)); local label = ""
    if _G.LexusConfig.Esp9_Name then label = isBot and "BOT" or "ENEMY" end
    if _G.LexusConfig.Esp9_Distance then label = label == "" and string.format("[%dm]",meters) or string.format("%s  [%dm]",label,meters) end
    local health, maxHealth = tonumber(enemy.Health) or 0, tonumber(enemy.HealthMax or enemy.MaxHealth) or 100; if maxHealth <= 0 then maxHealth=100 end
    local hp = math.max(0,math.min(1,health/maxHealth)); local teamColor = ESPVIPTeamColor(enemy.TeamID,isBot)
    local hpColor = hp < .3 and FLinearColor(1,.1,.1,1) or (hp < .7 and FLinearColor(1,.85,0,1) or FLinearColor(0,1,.15,1))
    PCall(function()
        entry.rootSlot:SetPosition(pos); if entry.lastText ~= label then entry.label:SetText(label); entry.lastText=label end
        entry.label:SetColorAndOpacity(FSlateColor(teamColor)); entry.label:SetWidgetVisibility(label ~= "" and UEnums.ESlateVisibility.SelfHitTestInvisible or UEnums.ESlateVisibility.Collapsed)
        entry.border:SetBrushColor(_G.LexusConfig.Esp9_Team and teamColor or FLinearColor(.01,.01,.02,0)); entry.border:SetWidgetVisibility(_G.LexusConfig.Esp9_Team and UEnums.ESlateVisibility.SelfHitTestInvisible or UEnums.ESlateVisibility.Collapsed)
        if _G.LexusConfig.Esp9_HP then if entry.hpTrack then entry.hpTrack:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible) end; if entry.hpBar then entry.hpBar:SetPercent(hp);entry.hpBar:SetFillColorAndOpacity(hpColor);entry.hpBar:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible) end else ESPVIPCollapse(entry.hpTrack);ESPVIPCollapse(entry.hpBar) end
        local icon = ESPVIPWeaponIcon(enemy,entry,now)
        if icon and entry.weapon then if entry.lastWeapon ~= icon then local texture=slua.loadObject(icon);if texture then if entry.weapon.SetBrushFromTexture then entry.weapon:SetBrushFromTexture(texture) elseif entry.weapon.SetBrushResourceObject then entry.weapon:SetBrushResourceObject(texture) end end;entry.lastWeapon=icon end;entry.weapon:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible) else ESPVIPCollapse(entry.weapon) end
        entry.root:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible)
    end)
    if _G.LexusConfig.Esp9_Line and entry.line and entry.lineSlot then
        local origin = ESPVIPScreenToCanvas(pc,canvas,FVector2D((viewport.X or 0)*.5,76)); local dx,dy=pos.X-origin.X,pos.Y-origin.Y; local length=math.sqrt(dx*dx+dy*dy)
        local data=_G.LexusState and _G.LexusState.CustomTextData or {}; local color=ESPVIPColor(data.Esp9_LineVisColor or 2,.82); local thickness=math.max(1,math.min(10,tonumber(data.Esp9_LineThick) or 1))
        PCall(function() entry.lineSlot:SetPosition(FVector2D(origin.X,origin.Y-thickness*.5));entry.lineSlot:SetSize(FVector2D(length,thickness));entry.line:SetRenderTransformPivot(FVector2D(0,.5));entry.line:SetRenderTransformAngle(math.deg(math.atan2(dy,dx)));entry.line:SetBrushColor(color);entry.line:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible) end)
    else ESPVIPCollapse(entry.line) end
    return true
end

local function ESPVIPTick()
    if not HasPanelAuthorization() then ESPVIPClear(); return end
    if not (_G.LexusConfig and _G.LexusConfig.EspLoai9) then ESPVIPClear(); return end
    local now=os.clock(); if now-ESPVIPState.lastUpdate < ESPVIPState.refreshInterval then return end; ESPVIPState.lastUpdate=now
    PCall(function()
        local pc=GameplayData and GameplayData.GetPlayerController and GameplayData.GetPlayerController();local localPlayer=pc and pc.GetPlayerCharacterSafety and pc:GetPlayerCharacterSafety();local canvas=ESPVIPGetCanvas()
        if not ESPVIPIsValid(pc) or not ESPVIPIsValid(localPlayer) or not ESPVIPIsValid(canvas) then return end
        local chars=GameplayData.GetAllPlayerCharacters and GameplayData.GetAllPlayerCharacters() or GameplayData.GameCharacters;if type(chars)~="table" then return end
        local localTeam=localPlayer.TeamID or 0;local localLocation=localPlayer:K2_GetActorLocation();local candidates,active={},{};local players,bots,nearest=0,0,nil
        for _,enemy in pairs(chars) do if ESPVIPIsValid(enemy) and enemy~=localPlayer and (enemy.TeamID or 0)~=localTeam then local location=enemy:K2_GetActorLocation();local dist=FVector.Dist(localLocation,location);if dist<=40000 then table.insert(candidates,{enemy=enemy,location=location,distance=dist}) end end end
        table.sort(candidates,function(a,b)return a.distance<b.distance end);local targetCount=math.min(#candidates,ESPVIPState.maxTargets)
        for i=1,targetCount do local target=candidates[i];local head=FVector(target.location.X,target.location.Y,target.location.Z+90);local screen=FVector2D(0,0);local projected=false;PCall(function() projected=pc:ProjectWorldLocationToScreen(head,screen,false) end);if projected and screen.X>=0 and screen.Y>=0 then local isBot=CheckIsAI(target.enemy,target.enemy.AK_MarkData or {});if isBot then bots=bots+1 else players=players+1 end;local meters=math.floor(target.distance/100);if not nearest or meters<nearest then nearest=meters end;if ESPVIPUpdateMarker(pc,target.enemy,screen,target.distance,isBot,now) then active[tostring(target.enemy)]=true end end end
        for id,entry in pairs(ESPVIPState.entries) do if not active[id] then ESPVIPRemoveEntry(id,entry) end end
        ESPVIPUpdateCounter(pc,players,bots,nearest or 0)
    end)
end
`;
replaceOnce("-- ==============================================================================\n-- ESP9 VISUAL-ONLY AD ESP", "function MainLoop()", vipRuntime, "ESP VIP runtime");
source = source.replaceAll("ESP9Clear()", "ESPVIPClear()");
source = source.replace("ESP9VisualTick()", "ESPVIPTick()");

if (failures.length > 0) {
  console.error("ESP_VIP_REPLACE_PLAN=FAIL");
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exit(1);
}
writeFileSync(luaPath, source);
console.log("ESP_VIP_REPLACE_PLAN=APPLIED");
