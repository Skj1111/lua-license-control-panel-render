# Generated Latest Lua Runtime Inventory

**Generated from:** `/home/ubuntu/BRPlayerCharacterBase-panel-updated.lua`  
**Scope:** Complete lexical scan of the delivered Lua source. This appendix enumerates runtime declarations, menu stacks, and `LexusConfig` flags. It is an evidence index; a declaration alone does not prove that an external game engine invokes it.

| Metric | Count |
|---|---:|
| Source lines scanned | 8595 |
| Function declarations found | 348 |
| Exported `_G` helper declarations | 23 |
| Local/runtime helper declarations | 325 |
| Registered menu stacks | 5 |
| Referenced `LexusConfig` flags | 80 |
| Declaration-only lexical candidates | 117 |
| Active internal declarations | 166 |
| External-callback declarations | 176 |
| Compatibility-only declarations | 3 |
| Legacy/unreachable declarations | 3 |

## Registered menu stacks

- `StackADESP` — line 3680
- `StackESP` — line 3688
- `StackAimbot` — line 3726
- `StackAimbotV2` — line 3750
- `StackCombat` — line 3806

## Exported global helpers

- `_G.NexaLoginPanel.IsAuthorized` — line 199; source occurrences: 14
- `_G.ScheduleNexaKeyEntryRetry` — line 576; source occurrences: 15
- `_G.require` — line 1958; source occurrences: 2
- `_G.FileHashChecker.GetHash` — line 2535; source occurrences: 1
- `_G.STExtraBlueprintFunctionLibrary.GetMD5` — line 2539; source occurrences: 1
- `_G.LocalMain` — line 2654; source occurrences: 1
- `_G.require` — line 3059; source occurrences: 2
- `_G.InitializeAutoHeadHooks` — line 3239; source occurrences: 4
- `_G.ForceEnableKillCounterUI` — line 3280; source occurrences: 1
- `_G.ForceEnableKillMessage` — line 3348; source occurrences: 1
- `_G.SaveModSettings` — line 3459; source occurrences: 18
- `_G.LoadModSettings` — line 3509; source occurrences: 3
- `_G.ReadLiveConfig` — line 3591; source occurrences: 1
- `_G.InitModMenuTab` — line 3609; source occurrences: 7
- `_G.GetEnemyTargetsFromActors` — line 4383; source occurrences: 3
- `_G.AimTouch` — line 4417; source occurrences: 11
- `_G.RunOptimizedItemAndVehicleESP` — line 4794; source occurrences: 2
- `_G.InitializeADESPSystem` — line 4940; source occurrences: 3
- `_G.RemoveCounterVisualLayers` — line 6191; source occurrences: 5
- `_G.ClearESPLineWidgets` — line 6432; source occurrences: 5
- `_G.ClearAllADESPWidgets` — line 6691; source occurrences: 9
- `_G.GetOriginalHWID` — line 6798; source occurrences: 1
- `_G.MortarAimTick` — line 8348; source occurrences: 5

## Local and namespaced function declarations

- `showNexaPopup` — line 2; source occurrences: 9
- `showNexaPopup` — line 115; source occurrences: 9
- `setNexaState` — line 123; source occurrences: 16
- `DisableAllFeatureFlags` — line 124; source occurrences: 2
- `HasPanelAuthorization` — line 203; source occurrences: 25
- `get_json` — line 214; source occurrences: 6
- `normalize_license_key` — line 224; source occurrences: 6
- `read_saved_panel_key` — line 233; source occurrences: 4
- `write_key_file` — line 256; source occurrences: 2
- `save_validated_panel_key` — line 279; source occurrences: 2
- `NexaLicenseClient.verify` — line 285; source occurrences: 2
- `normalize_device_identity` — line 400; source occurrences: 3
- `get_host_phone_device_id` — line 406; source occurrences: 2
- `get_device_id` — line 415; source occurrences: 2
- `ValidateKeyAndExpiry` — line 453; source occurrences: 4
- `Popup:ctor` — line 475; source occurrences: 1
- `Popup:RegistEvents` — line 482; source occurrences: 1
- `Popup:OnInitialize` — line 488; source occurrences: 1
- `Popup:OnShow` — line 496; source occurrences: 1
- `Popup:OnKeyChanged` — line 502; source occurrences: 1
- `Popup:OnConfirm` — line 514; source occurrences: 1
- `Popup:OnCancel` — line 527; source occurrences: 1
- `OpenKeyEntryPopup` — line 535; source occurrences: 3
- `on_submit` — line 549; source occurrences: 12
- `retry_now` — line 586; source occurrences: 4
- `LoginWithKey` — line 605; source occurrences: 2
- `CheckLoginAndInit` — line 616; source occurrences: 5
- `BRPlayerCharacterBase:ctor` — line 737; source occurrences: 2
- `BRPlayerCharacterBase:_PostConstruct` — line 743; source occurrences: 3
- `BRPlayerCharacterBase:ReceiveBeginPlay` — line 753; source occurrences: 4
- `BRPlayerCharacterBase:HandleOnAttachedToVehicle` — line 790; source occurrences: 3
- `BRPlayerCharacterBase:HandleOnDetachedFromVehicle` — line 816; source occurrences: 3
- `BRPlayerCharacterBase:UpdatePlayerAttachToVehicle` — line 830; source occurrences: 3
- `BRPlayerCharacterBase:FixMeshContainerOffsetIfNeeded` — line 885; source occurrences: 3
- `BRPlayerCharacterBase:ClearAttachToVehicleTimer` — line 910; source occurrences: 2
- `BRPlayerCharacterBase:CharacterAttrChangeEvent` — line 924; source occurrences: 2
- `BRPlayerCharacterBase:OnPawnStateChange` — line 940; source occurrences: 3
- `BRPlayerCharacterBase:HandleFinishedState` — line 955; source occurrences: 3
- `BRPlayerCharacterBase:CheckAddCheckFallingDistanceComponent` — line 965; source occurrences: 3
- `BRPlayerCharacterBase:LuaHandleParachuteStateChanged` — line 982; source occurrences: 2
- `BRPlayerCharacterBase:OnLanded` — line 1007; source occurrences: 3
- `BRPlayerCharacterBase:ReceiveEndPlay` — line 1028; source occurrences: 2
- `BRPlayerCharacterBase:IsWarGameMode` — line 1038; source occurrences: 2
- `BRPlayerCharacterBase:BPOnRecycled` — line 1053; source occurrences: 2
- `BRPlayerCharacterBase:BPOnRespawned` — line 1063; source occurrences: 2
- `BRPlayerCharacterBase:ReceiveOnRecycle` — line 1073; source occurrences: 2
- `BRPlayerCharacterBase:ReceiveOnSpawn` — line 1084; source occurrences: 2
- `BRPlayerCharacterBase:ResetMeshRelativeLocationAndRotation` — line 1095; source occurrences: 2
- `BRPlayerCharacterBase:HandleOnMovementModeChangedNew` — line 1113; source occurrences: 3
- `BRPlayerCharacterBase:BPOnMissPlayerDamageRecord` — line 1129; source occurrences: 2
- `BRPlayerCharacterBase:ClientRPC_TriggerHighlightMoment` — line 1135; source occurrences: 3
- `BRPlayerCharacterBase:ParachuteJump` — line 1143; source occurrences: 4
- `BRPlayerCharacterBase:OnMovementBaseChangedEvent` — line 1165; source occurrences: 3
- `BRPlayerCharacterBase:GetMedievalCraneFromBase` — line 1184; source occurrences: 2
- `BRPlayerCharacterBase:CheckForbidFlaregun` — line 1201; source occurrences: 2
- `BRPlayerCharacterBase:ServerRPC_NearDeathGiveupRescue` — line 1218; source occurrences: 2
- `BRPlayerCharacterBase:HandleNearDeathGiveupRescue` — line 1225; source occurrences: 2
- `BRPlayerCharacterBase:RPC_Server_GmPlayAction` — line 1239; source occurrences: 4
- `BRPlayerCharacterBase:MulticastRPC_GmPlayAction` — line 1251; source occurrences: 4
- `BRPlayerCharacterBase:RPC_Client_SetShouldCheckPassWall` — line 1283; source occurrences: 3
- `BRPlayerCharacterBase:OnPlayerEnterCarryBoxState` — line 1293; source occurrences: 3
- `BRPlayerCharacterBase:OnPlayerLeaveCarryBoxState` — line 1305; source occurrences: 3
- `BRPlayerCharacterBase:ServerRPC_CarryDeadBox` — line 1317; source occurrences: 2
- `BRPlayerCharacterBase:SetAreaID` — line 1326; source occurrences: 2
- `BRPlayerCharacterBase:GetAreaID` — line 1333; source occurrences: 2
- `BRPlayerCharacterBase:CannotChangeIntoPetSpectator` — line 1340; source occurrences: 3
- `BRPlayerCharacterBase:DoModChangeToBT` — line 1348; source occurrences: 4
- `BRPlayerCharacterBase:SwitchCameraToParachuteOpening` — line 1359; source occurrences: 4
- `BRPlayerCharacterBase:SwitchCameraToParachuteFalling` — line 1371; source occurrences: 4
- `BRPlayerCharacterBase:SwitchCameraToNormal` — line 1383; source occurrences: 3
- `BRPlayerCharacterBase:SwitchWeaponCheck` — line 1394; source occurrences: 3
- `GetRainbowLinearColor` — line 1425; source occurrences: 3
- `IPS.__inner_impl.UpdateArtQualityUI` — line 1437; source occurrences: 2
- `IngamePhoneStateUI.__inner_impl.SetPingText` — line 1463; source occurrences: 2
- `BattleKillBroadcastSubSystem.CopyKillOrPutDownMessageDataUserDataToLuaTable` — line 1476; source occurrences: 2
- `VehicleAvatarComponent.__inner_impl.CheckCanPlaySkinSwitchEffect` — line 1511; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.ShowVehicleSwitchEffect` — line 1515; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.ResetAnimationState` — line 1556; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.ReceiveBeginPlay` — line 1569; source occurrences: 2
- `Notify` — line 1581; source occurrences: 26
- `Valid` — line 1592; source occurrences: 249
- `ToggleTPPView` — line 1621; source occurrences: 2
- `ApplyTPPView` — line 1636; source occurrences: 4
- `UnlockTPPSwitchButton` — line 1668; source occurrences: 3
- `UIT.IsFPP` — line 1674; source occurrences: 2
- `nop` — line 1923; source occurrences: 235
- `CreateSafeModule` — line 1928; source occurrences: 3
- `__index` — line 1931; source occurrences: 1
- `nopstr` — line 1969; source occurrences: 3
- `nopfalse` — line 1973; source occurrences: 22
- `noptrue` — line 1977; source occurrences: 5
- `nopnil` — line 1981; source occurrences: 4
- `retFalse` — line 1985; source occurrences: 12
- `retTrue` — line 1989; source occurrences: 21
- `ClientEntryBypass` — line 2071; source occurrences: 3
- `Client.IsConnected` — line 2077; source occurrences: 1
- `Client.MD5LuaString` — line 2079; source occurrences: 1
- `Client.GetDSVersion` — line 2080; source occurrences: 1
- `EventSystem.postEvent` — line 2092; source occurrences: 2
- `io.open` — line 2142; source occurrences: 12
- `os.remove` — line 2154; source occurrences: 3
- `HiggsBosonBypass` — line 2166; source occurrences: 3
- `HawkEyeBypass` — line 2219; source occurrences: 3
- `ClientHawkEyePatrolSubsystem._InitHawkEyePatrolSubsystem` — line 2236; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetForbidNextPatrolRemainingTimeInSeconds` — line 2259; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetUsedDailyTimeInSeconds` — line 2260; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetInspectorBroadcastCount` — line 2261; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetMaxInspectorBroadcastCount` — line 2262; source occurrences: 1
- `ClientHawkEyePatrolSubsystem._PostConstruct` — line 2266; source occurrences: 1
- `BanLogicBypass` — line 2290; source occurrences: 3
- `ClientBanLogic.TryOpenVoice` — line 2298; source occurrences: 1
- `RealTimeBan.Init` — line 2313; source occurrences: 1
- `RealTimeBan.GetUIDInspectorRank` — line 2320; source occurrences: 1
- `RealTimeBan.GetUIDInspectorBroadcastCount` — line 2322; source occurrences: 1
- `RealTimeBan.GetTipsIDOffset` — line 2323; source occurrences: 3
- `RealTimeBan.GetTipsIDOffsetWithUID` — line 2324; source occurrences: 1
- `RealTimeBan.GetTipsIDOffsetInspector` — line 2325; source occurrences: 1
- `BanSystem.GetBanReason` — line 2343; source occurrences: 1
- `BanSystem.GetBanTime` — line 2344; source occurrences: 1
- `ReportSystemBypass` — line 2353; source occurrences: 3
- `ClientReportPlayerSubsystem.GetFatalDamagerMap` — line 2371; source occurrences: 2
- `ClientReportPlayerSubsystem.GetFatalDamagerMapSize` — line 2372; source occurrences: 1
- `ClientReportPlayerSubsystem.GetName2InfoMap` — line 2373; source occurrences: 1
- `ClientReportPlayerSubsystem.GetCachedTeammateName2InfoMap` — line 2374; source occurrences: 1
- `ClientReportPlayerSubsystem.GetTeammateName2InfoMapDuringBattle` — line 2375; source occurrences: 1
- `ClientReportPlayerSubsystem.GetCurrentNotInTeamHistoricalTeammateMap` — line 2376; source occurrences: 1
- `ClientReportPlayerSubsystem.GetInTeamIndexFromHistoricalTeammateInfo` — line 2377; source occurrences: 1
- `ClientReportPlayerSubsystem.GetGameModeType` — line 2379; source occurrences: 1
- `ClientReportPlayerSubsystem.GetMainModeID` — line 2380; source occurrences: 1
- `ClientReportPlayerSubsystem.GetSubModeID` — line 2381; source occurrences: 1
- `DSReportPlayerSubsystem._FindCacheByUID` — line 2417; source occurrences: 1
- `DSReportPlayerSubsystem._GetFatalDamagerMap` — line 2421; source occurrences: 1
- `ui_complaint.SubmitReportData` — line 2428; source occurrences: 1
- `ui_complaint._OnClickReport` — line 2429; source occurrences: 1
- `ui_complaint._AddCommonTypesOfPlayerForReport` — line 2430; source occurrences: 1
- `ui_complaint.AddPlayerForReport` — line 2431; source occurrences: 1
- `ui_complaint.GetSelectedReasonAsArray` — line 2432; source occurrences: 1
- `ui_complaint.GetSelectedSubReasonAsArray` — line 2433; source occurrences: 1
- `ui_complaint.BlockPlayerChat` — line 2434; source occurrences: 1
- `ui_complaint.IsBlockChatCheck` — line 2435; source occurrences: 1
- `ui_complaint.CheckBoxBlack` — line 2436; source occurrences: 1
- `ui_complaint.UpdateMatchBlackList` — line 2437; source occurrences: 1
- `LogicComplaint.Submit` — line 2445; source occurrences: 1
- `TLogBypass` — line 2454; source occurrences: 3
- `ToolReportUtil.ReParseError` — line 2476; source occurrences: 1
- `MD5Bypass` — line 2508; source occurrences: 4
- `CMode.MD5HashByteArray` — line 2524; source occurrences: 1
- `CMode.MD5HashFile` — line 2525; source occurrences: 1
- `CMode.GetContentDiffData` — line 2526; source occurrences: 1
- `DNSDeviceBypass` — line 2549; source occurrences: 3
- `DeviceID.GetDeviceID` — line 2553; source occurrences: 1
- `DeviceID.GetAndroidID` — line 2554; source occurrences: 1
- `DeviceID.GetIMEI` — line 2555; source occurrences: 1
- `DeviceID.GetMACAddress` — line 2556; source occurrences: 1
- `DeviceID.GetUniqueDeviceID` — line 2557; source occurrences: 1
- `DeviceID.GetDeviceName` — line 2558; source occurrences: 1
- `DeviceID.GetDeviceModel` — line 2559; source occurrences: 1
- `DeviceID.GetDeviceBrand` — line 2560; source occurrences: 1
- `DeviceID.GetDeviceManufacturer` — line 2561; source occurrences: 1
- `DeviceID.GetDeviceBoard` — line 2562; source occurrences: 1
- `DeviceID.GetDeviceBootloader` — line 2563; source occurrences: 1
- `DeviceID.GetDeviceHardware` — line 2564; source occurrences: 1
- `DeviceID.GetDeviceHost` — line 2565; source occurrences: 1
- `DeviceID.GetDeviceFingerprint` — line 2566; source occurrences: 1
- `DeviceID.GetDeviceSerial` — line 2567; source occurrences: 1
- `DNS.Resolve` — line 2571; source occurrences: 1
- `DNS.GetHostName` — line 2572; source occurrences: 1
- `DNS.GetIPAddress` — line 2573; source occurrences: 1
- `Network.GetIPAddress` — line 2577; source occurrences: 1
- `Network.GetMACAddress` — line 2578; source occurrences: 1
- `Network.GetSSID` — line 2579; source occurrences: 1
- `Network.GetBSSID` — line 2580; source occurrences: 1
- `GokubaBypass` — line 2589; source occurrences: 3
- `Gokuba.ForwardFeature` — line 2593; source occurrences: 1
- `RacingAntiCheatBypass` — line 2623; source occurrences: 3
- `RacingAntiCheatLogic._CreateVehicleData` — line 2636; source occurrences: 1
- `CoronaLabBypass` — line 2652; source occurrences: 3
- `uOuterController.AddGameTimer` — line 2661; source occurrences: 3
- `CHiggsBosonComponent.SecurityCoronaLabClientDataPointer` — line 2669; source occurrences: 1
- `CHiggsBosonComponent.SetFloatValueByName` — line 2670; source occurrences: 1
- `LoginModuleBypass` — line 2695; source occurrences: 3
- `login_module.aq_ban` — line 2700; source occurrences: 1
- `login_module.device_num_limit` — line 2702; source occurrences: 1
- `login_module.Login_Failed` — line 2706; source occurrences: 1
- `login_module.aas_ban` — line 2707; source occurrences: 1
- `login_module.PakMonitorStart` — line 2708; source occurrences: 1
- `login_module.SetupFilenameHideKeywords` — line 2709; source occurrences: 1
- `login_module.on_login_failed` — line 2710; source occurrences: 1
- `login_module.DelaybanLoginCancelCallback` — line 2711; source occurrences: 1
- `SwiftHawkBypass` — line 2722; source occurrences: 3
- `ShootVerificationBypass` — line 2741; source occurrences: 3
- `ModifierExceptionBypass` — line 2763; source occurrences: 3
- `SimulateCharacterBypass` — line 2780; source occurrences: 3
- `PlayerSecurityBypass` — line 2795; source occurrences: 3
- `ClientFlowBypass` — line 2826; source occurrences: 3
- `GameplayCallbackBypass` — line 2849; source occurrences: 3
- `GC.OnDSPlayerStateChanged` — line 2875; source occurrences: 2
- `KillAllSubsystems` — line 2902; source occurrences: 3
- `SLUABypass` — line 2956; source occurrences: 3
- `ReplayTelemetryBypass` — line 2983; source occurrences: 3
- `FinalProtection` — line 3029; source occurrences: 3
- `InitializeAllBypass` — line 3084; source occurrences: 2
- `SafeAddMark` — line 3126; source occurrences: 6
- `SafeRemoveMark` — line 3141; source occurrences: 18
- `GetSafeEnemyKey` — line 3161; source occurrences: 4
- `CheckIsAI` — line 3181; source occurrences: 6
- `hitLogic.GetHitBodyType` — line 3254; source occurrences: 4
- `hitLogic.GetHitBodyTypeByHitPos` — line 3260; source occurrences: 2
- `kcImpl.CheckSupportKCUI` — line 3286; source occurrences: 1
- `kcImpl.CheckNeedMainKillCounterUI` — line 3287; source occurrences: 1
- `kcImpl.UpdateMainKillCounterUI` — line 3294; source occurrences: 2
- `LogicKillCounter.CheckSupportKC` — line 3305; source occurrences: 1
- `LogicKillCounter.CheckSupportKillCounterAvatar` — line 3306; source occurrences: 1
- `LogicKillCounter.CheckHasWeaponKillCounter` — line 3307; source occurrences: 1
- `LogicKillCounter.GetBaseKillCounterIdByWeaponId` — line 3308; source occurrences: 1
- `LogicKillCounter.GetEquipedKillCounterId` — line 3309; source occurrences: 1
- `LogicKillCounter.GetMyEquipedKillCounterId` — line 3310; source occurrences: 1
- `LogicKillCounter.GetOneWeaponKillCountInBattle` — line 3311; source occurrences: 1
- `LogicKillCounter.GetWeaponKillCountByUid` — line 3312; source occurrences: 1
- `mode.__inner_impl.CheckShowKCIcon` — line 3328; source occurrences: 1
- `KillInfo.__inner_impl.FileItem` — line 3356; source occurrences: 2
- `GetConfigPaths` — line 3416; source occurrences: 5
- `AutoSaveLoop` — line 3569; source occurrences: 4
- `GuardMenuStack` — line 3861; source occurrences: 6
- `item.GetFunc` — line 3865; source occurrences: 2
- `item.SetFunc` — line 3870; source occurrences: 2
- `UIManager.ShowUI` — line 3903; source occurrences: 2
- `ShowLexusVIPMenu` — line 3947; source occurrences: 3
- `Step_ScamAlert` — line 3956; source occurrences: 2
- `Step_Welcome` — line 3962; source occurrences: 2
- `InitializeGraphicsUnlock` — line 3987; source occurrences: 3
- `logic_setting_graphics.SetFPS` — line 4014; source occurrences: 2
- `fps_impl:GetMaxFPSLevel` — line 4025; source occurrences: 1
- `fps_impl:InitRealSupportFPS` — line 4026; source occurrences: 1
- `fps_impl:UpdateSelectedFPSState` — line 4032; source occurrences: 1
- `clamp` — line 4052; source occurrences: 2
- `lerp` — line 4057; source occurrences: 5
- `_getColorByPercent` — line 4058; source occurrences: 2
- `ft_impl.ShowOrHide` — line 4063; source occurrences: 1
- `ft_impl.InitFPSFTSwitch` — line 4068; source occurrences: 1
- `ft_impl.InitFPSFTValue165` — line 4076; source occurrences: 1
- `ft_impl.OnFPSFTValueChange3` — line 4109; source occurrences: 1
- `ft_impl.OnFPSFTSliderValueChange3` — line 4120; source occurrences: 3
- `InitializeNativeESP` — line 4151; source occurrences: 3
- `ApplyCfg` — line 4156; source occurrences: 3
- `GetAllSkeletalMeshes` — line 4213; source occurrences: 7
- `UndoWallXuyenTuong` — line 4260; source occurrences: 3
- `ApplyWallXuyenTuong` — line 4284; source occurrences: 3
- `GK_DestroyWidget` — line 4862; source occurrences: 3
- `GK_GetOrCreateWidget` — line 4870; source occurrences: 2
- `GK_UpdateWidget` — line 4918; source occurrences: 2
- `SafeToString` — line 5016; source occurrences: 2
- `CleanPlayerName` — line 5036; source occurrences: 2
- `GetActualPlayerName` — line 5049; source occurrences: 3
- `IsAIPawn` — line 5066; source occurrences: 8
- `IsPawnAlive` — line 5091; source occurrences: 2
- `IsPawnKnocked` — line 5106; source occurrences: 3
- `IsPawnRenderable` — line 5110; source occurrences: 2
- `GetHPString` — line 5114; source occurrences: 2
- `GetDynamicScale` — line 5123; source occurrences: 3
- `GetHeadWorldPosition` — line 5132; source occurrences: 2
- `AddDebugTextSafe` — line 5158; source occurrences: 3
- `GetHeadRelativeOffset` — line 5180; source occurrences: 3
- `UpdateDistanceText` — line 5194; source occurrences: 2
- `UpdateHealthBar` — line 5210; source occurrences: 2
- `SetNativeColor` — line 5241; source occurrences: 2
- `SetNativeText` — line 5269; source occurrences: 2
- `SetNativeHealth` — line 5278; source occurrences: 2
- `GetStableESPCanvas` — line 5302; source occurrences: 7
- `UpdateStableCanvasTransform` — line 5323; source occurrences: 3
- `ProjectToStableCanvas` — line 5368; source occurrences: 3
- `AK_GetFirstWeaponInBackpack` — line 5381; source occurrences: 2
- `AK_GetCharacterWeapon` — line 5395; source occurrences: 3
- `StableBoxWidth` — line 5424; source occurrences: 2
- `QuantizeTagCoordinate` — line 5430; source occurrences: 3
- `GetSmoothedTagPosition` — line 5436; source occurrences: 2
- `CreateStableEntityBox` — line 5467; source occurrences: 2
- `UpdateStableEntityBox` — line 5579; source occurrences: 2
- `RemoveStableEntityBox` — line 5723; source occurrences: 3
- `HideNativeLabelControls` — line 5733; source occurrences: 1
- `CreateOrGetNativeHeadWidget` — line 5744; source occurrences: 2
- `UpdateNativeHeadWidget` — line 5779; source occurrences: 2
- `RemoveNativeHeadWidget` — line 5832; source occurrences: 3
- `SetCounterChildVisibility` — line 5870; source occurrences: 13
- `HideCounterNonBackgroundControls` — line 5879; source occurrences: 3
- `SetCounterBackgroundColor` — line 5891; source occurrences: 3
- `ShowCounterTextControls` — line 5903; source occurrences: 2
- `ApplyCounterTextAppearance` — line 5915; source occurrences: 2
- `StretchCounterBackground` — line 5943; source occurrences: 2
- `PositionCounterLayer` — line 5958; source occurrences: 3
- `CreateCounterBackgroundLayer` — line 5970; source occurrences: 1
- `SetCounterText` — line 5994; source occurrences: 3
- `CreateCounterTextLayer` — line 6019; source occurrences: 1
- `RemoveCounterBorderOverlay` — line 6041; source occurrences: 2
- `UpdateCounterVisualEffects` — line 6055; source occurrences: 3
- `UpdateCounterBorderOverlay` — line 6081; source occurrences: 4
- `CreateCounterBorderOverlay` — line 6121; source occurrences: 2
- `CreateEnemyCounterWidget` — line 6212; source occurrences: 2
- `ScreenToStableCanvas` — line 6252; source occurrences: 2
- `IsTargetInFront180` — line 6261; source occurrences: 2
- `GetScreenEdgeEndpoint` — line 6281; source occurrences: 2
- `GetESPLineEndpoint` — line 6298; source occurrences: 2
- `CreateESPLineWidget` — line 6314; source occurrences: 2
- `RemoveESPLineWidget` — line 6354; source occurrences: 4
- `UpdateESPLine` — line 6366; source occurrences: 4
- `UpdateESPLineSet` — line 6408; source occurrences: 2
- `UpdateAllESP` — line 6445; source occurrences: 4
- `StartEnemyCounter` — line 6658; source occurrences: 6
- `initTimer` — line 6663; source occurrences: 3
- `MainLoop` — line 6729; source occurrences: 4
- `SystemLib.GetDeviceId` — line 6774; source occurrences: 4
- `GetFirstElemSafe` — line 7397; source occurrences: 1
- `DrawBombs` — line 7992; source occurrences: 3
- `ExpiredTick` — line 8160; source occurrences: 4
- `FastTick` — line 8187; source occurrences: 5
- `MortarNotify` — line 8241; source occurrences: 7
- `MortarValid` — line 8251; source occurrences: 9
- `MortarNormalizeAngle` — line 8264; source occurrences: 6
- `MortarAtan2` — line 8273; source occurrences: 5
- `MortarSolveBallisticPitch` — line 8286; source occurrences: 3
- `MortarReverseMapPitch` — line 8303; source occurrences: 3
- `MortarGetBallistics` — line 8313; source occurrences: 3
- `IsMortarWeapon` — line 8335; source occurrences: 3
- `MortarAim.Start` — line 8496; source occurrences: 4
- `InitAllModSystems` — line 8509; source occurrences: 9

## Declaration-only lexical candidates

These entries appear once in the Lua text at their declaration. They are candidates for legacy-only, engine-callback, or externally invoked behavior. They are **not automatically treated as active user features** without an audited startup, menu, main-loop, or game-engine entry path.

- `Popup:ctor` — line 475; source occurrences: 1
- `Popup:RegistEvents` — line 482; source occurrences: 1
- `Popup:OnInitialize` — line 488; source occurrences: 1
- `Popup:OnShow` — line 496; source occurrences: 1
- `Popup:OnKeyChanged` — line 502; source occurrences: 1
- `Popup:OnConfirm` — line 514; source occurrences: 1
- `Popup:OnCancel` — line 527; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.CheckCanPlaySkinSwitchEffect` — line 1511; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.ShowVehicleSwitchEffect` — line 1515; source occurrences: 1
- `VehicleAvatarComponent.__inner_impl.ResetAnimationState` — line 1556; source occurrences: 1
- `__index` — line 1931; source occurrences: 1
- `Client.IsConnected` — line 2077; source occurrences: 1
- `Client.MD5LuaString` — line 2079; source occurrences: 1
- `Client.GetDSVersion` — line 2080; source occurrences: 1
- `ClientHawkEyePatrolSubsystem._InitHawkEyePatrolSubsystem` — line 2236; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetForbidNextPatrolRemainingTimeInSeconds` — line 2259; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetUsedDailyTimeInSeconds` — line 2260; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetInspectorBroadcastCount` — line 2261; source occurrences: 1
- `ClientHawkEyePatrolSubsystem.GetMaxInspectorBroadcastCount` — line 2262; source occurrences: 1
- `ClientHawkEyePatrolSubsystem._PostConstruct` — line 2266; source occurrences: 1
- `ClientBanLogic.TryOpenVoice` — line 2298; source occurrences: 1
- `RealTimeBan.Init` — line 2313; source occurrences: 1
- `RealTimeBan.GetUIDInspectorRank` — line 2320; source occurrences: 1
- `RealTimeBan.GetUIDInspectorBroadcastCount` — line 2322; source occurrences: 1
- `RealTimeBan.GetTipsIDOffsetWithUID` — line 2324; source occurrences: 1
- `RealTimeBan.GetTipsIDOffsetInspector` — line 2325; source occurrences: 1
- `BanSystem.GetBanReason` — line 2343; source occurrences: 1
- `BanSystem.GetBanTime` — line 2344; source occurrences: 1
- `ClientReportPlayerSubsystem.GetFatalDamagerMapSize` — line 2372; source occurrences: 1
- `ClientReportPlayerSubsystem.GetName2InfoMap` — line 2373; source occurrences: 1
- `ClientReportPlayerSubsystem.GetCachedTeammateName2InfoMap` — line 2374; source occurrences: 1
- `ClientReportPlayerSubsystem.GetTeammateName2InfoMapDuringBattle` — line 2375; source occurrences: 1
- `ClientReportPlayerSubsystem.GetCurrentNotInTeamHistoricalTeammateMap` — line 2376; source occurrences: 1
- `ClientReportPlayerSubsystem.GetInTeamIndexFromHistoricalTeammateInfo` — line 2377; source occurrences: 1
- `ClientReportPlayerSubsystem.GetGameModeType` — line 2379; source occurrences: 1
- `ClientReportPlayerSubsystem.GetMainModeID` — line 2380; source occurrences: 1
- `ClientReportPlayerSubsystem.GetSubModeID` — line 2381; source occurrences: 1
- `DSReportPlayerSubsystem._FindCacheByUID` — line 2417; source occurrences: 1
- `DSReportPlayerSubsystem._GetFatalDamagerMap` — line 2421; source occurrences: 1
- `ui_complaint.SubmitReportData` — line 2428; source occurrences: 1
- `ui_complaint._OnClickReport` — line 2429; source occurrences: 1
- `ui_complaint._AddCommonTypesOfPlayerForReport` — line 2430; source occurrences: 1
- `ui_complaint.AddPlayerForReport` — line 2431; source occurrences: 1
- `ui_complaint.GetSelectedReasonAsArray` — line 2432; source occurrences: 1
- `ui_complaint.GetSelectedSubReasonAsArray` — line 2433; source occurrences: 1
- `ui_complaint.BlockPlayerChat` — line 2434; source occurrences: 1
- `ui_complaint.IsBlockChatCheck` — line 2435; source occurrences: 1
- `ui_complaint.CheckBoxBlack` — line 2436; source occurrences: 1
- `ui_complaint.UpdateMatchBlackList` — line 2437; source occurrences: 1
- `LogicComplaint.Submit` — line 2445; source occurrences: 1
- `ToolReportUtil.ReParseError` — line 2476; source occurrences: 1
- `CMode.MD5HashByteArray` — line 2524; source occurrences: 1
- `CMode.MD5HashFile` — line 2525; source occurrences: 1
- `CMode.GetContentDiffData` — line 2526; source occurrences: 1
- `_G.FileHashChecker.GetHash` — line 2535; source occurrences: 1
- `_G.STExtraBlueprintFunctionLibrary.GetMD5` — line 2539; source occurrences: 1
- `DeviceID.GetDeviceID` — line 2553; source occurrences: 1
- `DeviceID.GetAndroidID` — line 2554; source occurrences: 1
- `DeviceID.GetIMEI` — line 2555; source occurrences: 1
- `DeviceID.GetMACAddress` — line 2556; source occurrences: 1
- `DeviceID.GetUniqueDeviceID` — line 2557; source occurrences: 1
- `DeviceID.GetDeviceName` — line 2558; source occurrences: 1
- `DeviceID.GetDeviceModel` — line 2559; source occurrences: 1
- `DeviceID.GetDeviceBrand` — line 2560; source occurrences: 1
- `DeviceID.GetDeviceManufacturer` — line 2561; source occurrences: 1
- `DeviceID.GetDeviceBoard` — line 2562; source occurrences: 1
- `DeviceID.GetDeviceBootloader` — line 2563; source occurrences: 1
- `DeviceID.GetDeviceHardware` — line 2564; source occurrences: 1
- `DeviceID.GetDeviceHost` — line 2565; source occurrences: 1
- `DeviceID.GetDeviceFingerprint` — line 2566; source occurrences: 1
- `DeviceID.GetDeviceSerial` — line 2567; source occurrences: 1
- `DNS.Resolve` — line 2571; source occurrences: 1
- `DNS.GetHostName` — line 2572; source occurrences: 1
- `DNS.GetIPAddress` — line 2573; source occurrences: 1
- `Network.GetIPAddress` — line 2577; source occurrences: 1
- `Network.GetMACAddress` — line 2578; source occurrences: 1
- `Network.GetSSID` — line 2579; source occurrences: 1
- `Network.GetBSSID` — line 2580; source occurrences: 1
- `Gokuba.ForwardFeature` — line 2593; source occurrences: 1
- `RacingAntiCheatLogic._CreateVehicleData` — line 2636; source occurrences: 1
- `_G.LocalMain` — line 2654; source occurrences: 1
- `CHiggsBosonComponent.SecurityCoronaLabClientDataPointer` — line 2669; source occurrences: 1
- `CHiggsBosonComponent.SetFloatValueByName` — line 2670; source occurrences: 1
- `login_module.aq_ban` — line 2700; source occurrences: 1
- `login_module.device_num_limit` — line 2702; source occurrences: 1
- `login_module.Login_Failed` — line 2706; source occurrences: 1
- `login_module.aas_ban` — line 2707; source occurrences: 1
- `login_module.PakMonitorStart` — line 2708; source occurrences: 1
- `login_module.SetupFilenameHideKeywords` — line 2709; source occurrences: 1
- `login_module.on_login_failed` — line 2710; source occurrences: 1
- `login_module.DelaybanLoginCancelCallback` — line 2711; source occurrences: 1
- `_G.ForceEnableKillCounterUI` — line 3280; source occurrences: 1
- `kcImpl.CheckSupportKCUI` — line 3286; source occurrences: 1
- `kcImpl.CheckNeedMainKillCounterUI` — line 3287; source occurrences: 1
- `LogicKillCounter.CheckSupportKC` — line 3305; source occurrences: 1
- `LogicKillCounter.CheckSupportKillCounterAvatar` — line 3306; source occurrences: 1
- `LogicKillCounter.CheckHasWeaponKillCounter` — line 3307; source occurrences: 1
- `LogicKillCounter.GetBaseKillCounterIdByWeaponId` — line 3308; source occurrences: 1
- `LogicKillCounter.GetEquipedKillCounterId` — line 3309; source occurrences: 1
- `LogicKillCounter.GetMyEquipedKillCounterId` — line 3310; source occurrences: 1
- `LogicKillCounter.GetOneWeaponKillCountInBattle` — line 3311; source occurrences: 1
- `LogicKillCounter.GetWeaponKillCountByUid` — line 3312; source occurrences: 1
- `mode.__inner_impl.CheckShowKCIcon` — line 3328; source occurrences: 1
- `_G.ForceEnableKillMessage` — line 3348; source occurrences: 1
- `_G.ReadLiveConfig` — line 3591; source occurrences: 1
- `fps_impl:GetMaxFPSLevel` — line 4025; source occurrences: 1
- `fps_impl:InitRealSupportFPS` — line 4026; source occurrences: 1
- `fps_impl:UpdateSelectedFPSState` — line 4032; source occurrences: 1
- `ft_impl.ShowOrHide` — line 4063; source occurrences: 1
- `ft_impl.InitFPSFTSwitch` — line 4068; source occurrences: 1
- `ft_impl.InitFPSFTValue165` — line 4076; source occurrences: 1
- `ft_impl.OnFPSFTValueChange3` — line 4109; source occurrences: 1
- `HideNativeLabelControls` — line 5733; source occurrences: 1
- `CreateCounterBackgroundLayer` — line 5970; source occurrences: 1
- `CreateCounterTextLayer` — line 6019; source occurrences: 1
- `_G.GetOriginalHWID` — line 6798; source occurrences: 1
- `GetFirstElemSafe` — line 7397; source occurrences: 1

## Declaration-by-declaration classification

The table below classifies **every function declaration found in the complete source scan**. The category is static/lexical evidence: it distinguishes direct internal references from game/UI callback candidates and identifies known compatibility or bypass-adjacent paths. It does not claim Android-engine invocation without a device runtime trace.

| Line | Declaration | Source occurrences | Static classification | Evidence |
|---:|---|---:|---|---|
| 2 | `showNexaPopup` | 9 | active-internal | has one or more lexical references beyond its declaration |
| 115 | `showNexaPopup` | 9 | active-internal | has one or more lexical references beyond its declaration |
| 123 | `setNexaState` | 16 | active-internal | has one or more lexical references beyond its declaration |
| 124 | `DisableAllFeatureFlags` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 199 | `_G.NexaLoginPanel.IsAuthorized` | 14 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 203 | `HasPanelAuthorization` | 25 | active-internal | has one or more lexical references beyond its declaration |
| 214 | `get_json` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 224 | `normalize_license_key` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 233 | `read_saved_panel_key` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 256 | `write_key_file` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 279 | `save_validated_panel_key` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 285 | `NexaLicenseClient.verify` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 400 | `normalize_device_identity` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 406 | `get_host_phone_device_id` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 415 | `get_device_id` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 453 | `ValidateKeyAndExpiry` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 475 | `Popup:ctor` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 482 | `Popup:RegistEvents` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 488 | `Popup:OnInitialize` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 496 | `Popup:OnShow` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 502 | `Popup:OnKeyChanged` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 514 | `Popup:OnConfirm` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 527 | `Popup:OnCancel` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 535 | `OpenKeyEntryPopup` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 549 | `on_submit` | 12 | active-internal | has one or more lexical references beyond its declaration |
| 576 | `_G.ScheduleNexaKeyEntryRetry` | 15 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 586 | `retry_now` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 605 | `LoginWithKey` | 2 | compatibility-only | explicit compatibility helper; not an authorization or visible menu feature |
| 616 | `CheckLoginAndInit` | 5 | active-internal | has one or more lexical references beyond its declaration |
| 737 | `BRPlayerCharacterBase:ctor` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 743 | `BRPlayerCharacterBase:_PostConstruct` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 753 | `BRPlayerCharacterBase:ReceiveBeginPlay` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 790 | `BRPlayerCharacterBase:HandleOnAttachedToVehicle` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 816 | `BRPlayerCharacterBase:HandleOnDetachedFromVehicle` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 830 | `BRPlayerCharacterBase:UpdatePlayerAttachToVehicle` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 885 | `BRPlayerCharacterBase:FixMeshContainerOffsetIfNeeded` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 910 | `BRPlayerCharacterBase:ClearAttachToVehicleTimer` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 924 | `BRPlayerCharacterBase:CharacterAttrChangeEvent` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 940 | `BRPlayerCharacterBase:OnPawnStateChange` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 955 | `BRPlayerCharacterBase:HandleFinishedState` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 965 | `BRPlayerCharacterBase:CheckAddCheckFallingDistanceComponent` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 982 | `BRPlayerCharacterBase:LuaHandleParachuteStateChanged` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1007 | `BRPlayerCharacterBase:OnLanded` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1028 | `BRPlayerCharacterBase:ReceiveEndPlay` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1038 | `BRPlayerCharacterBase:IsWarGameMode` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1053 | `BRPlayerCharacterBase:BPOnRecycled` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1063 | `BRPlayerCharacterBase:BPOnRespawned` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1073 | `BRPlayerCharacterBase:ReceiveOnRecycle` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1084 | `BRPlayerCharacterBase:ReceiveOnSpawn` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1095 | `BRPlayerCharacterBase:ResetMeshRelativeLocationAndRotation` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1113 | `BRPlayerCharacterBase:HandleOnMovementModeChangedNew` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1129 | `BRPlayerCharacterBase:BPOnMissPlayerDamageRecord` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1135 | `BRPlayerCharacterBase:ClientRPC_TriggerHighlightMoment` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1143 | `BRPlayerCharacterBase:ParachuteJump` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1165 | `BRPlayerCharacterBase:OnMovementBaseChangedEvent` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1184 | `BRPlayerCharacterBase:GetMedievalCraneFromBase` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1201 | `BRPlayerCharacterBase:CheckForbidFlaregun` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1218 | `BRPlayerCharacterBase:ServerRPC_NearDeathGiveupRescue` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1225 | `BRPlayerCharacterBase:HandleNearDeathGiveupRescue` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1239 | `BRPlayerCharacterBase:RPC_Server_GmPlayAction` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1251 | `BRPlayerCharacterBase:MulticastRPC_GmPlayAction` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1283 | `BRPlayerCharacterBase:RPC_Client_SetShouldCheckPassWall` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1293 | `BRPlayerCharacterBase:OnPlayerEnterCarryBoxState` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1305 | `BRPlayerCharacterBase:OnPlayerLeaveCarryBoxState` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1317 | `BRPlayerCharacterBase:ServerRPC_CarryDeadBox` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1326 | `BRPlayerCharacterBase:SetAreaID` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1333 | `BRPlayerCharacterBase:GetAreaID` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1340 | `BRPlayerCharacterBase:CannotChangeIntoPetSpectator` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1348 | `BRPlayerCharacterBase:DoModChangeToBT` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1359 | `BRPlayerCharacterBase:SwitchCameraToParachuteOpening` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1371 | `BRPlayerCharacterBase:SwitchCameraToParachuteFalling` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1383 | `BRPlayerCharacterBase:SwitchCameraToNormal` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1394 | `BRPlayerCharacterBase:SwitchWeaponCheck` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1425 | `GetRainbowLinearColor` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 1437 | `IPS.__inner_impl.UpdateArtQualityUI` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1463 | `IngamePhoneStateUI.__inner_impl.SetPingText` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1476 | `BattleKillBroadcastSubSystem.CopyKillOrPutDownMessageDataUserDataToLuaTable` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1511 | `VehicleAvatarComponent.__inner_impl.CheckCanPlaySkinSwitchEffect` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 1515 | `VehicleAvatarComponent.__inner_impl.ShowVehicleSwitchEffect` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 1556 | `VehicleAvatarComponent.__inner_impl.ResetAnimationState` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 1569 | `VehicleAvatarComponent.__inner_impl.ReceiveBeginPlay` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1581 | `Notify` | 26 | active-internal | has one or more lexical references beyond its declaration |
| 1592 | `Valid` | 249 | active-internal | has one or more lexical references beyond its declaration |
| 1621 | `ToggleTPPView` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1636 | `ApplyTPPView` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 1668 | `UnlockTPPSwitchButton` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 1674 | `UIT.IsFPP` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 1923 | `nop` | 235 | active-internal | has one or more lexical references beyond its declaration |
| 1928 | `CreateSafeModule` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 1931 | `__index` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 1958 | `_G.require` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 1969 | `nopstr` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 1973 | `nopfalse` | 22 | active-internal | has one or more lexical references beyond its declaration |
| 1977 | `noptrue` | 5 | active-internal | has one or more lexical references beyond its declaration |
| 1981 | `nopnil` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 1985 | `retFalse` | 12 | active-internal | has one or more lexical references beyond its declaration |
| 1989 | `retTrue` | 21 | active-internal | has one or more lexical references beyond its declaration |
| 2071 | `ClientEntryBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2077 | `Client.IsConnected` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2079 | `Client.MD5LuaString` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2080 | `Client.GetDSVersion` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2092 | `EventSystem.postEvent` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 2142 | `io.open` | 12 | active-internal | has one or more lexical references beyond its declaration |
| 2154 | `os.remove` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2166 | `HiggsBosonBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2219 | `HawkEyeBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2236 | `ClientHawkEyePatrolSubsystem._InitHawkEyePatrolSubsystem` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2259 | `ClientHawkEyePatrolSubsystem.GetForbidNextPatrolRemainingTimeInSeconds` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2260 | `ClientHawkEyePatrolSubsystem.GetUsedDailyTimeInSeconds` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2261 | `ClientHawkEyePatrolSubsystem.GetInspectorBroadcastCount` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2262 | `ClientHawkEyePatrolSubsystem.GetMaxInspectorBroadcastCount` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2266 | `ClientHawkEyePatrolSubsystem._PostConstruct` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2290 | `BanLogicBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2298 | `ClientBanLogic.TryOpenVoice` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2313 | `RealTimeBan.Init` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2320 | `RealTimeBan.GetUIDInspectorRank` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2322 | `RealTimeBan.GetUIDInspectorBroadcastCount` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2323 | `RealTimeBan.GetTipsIDOffset` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2324 | `RealTimeBan.GetTipsIDOffsetWithUID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2325 | `RealTimeBan.GetTipsIDOffsetInspector` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2343 | `BanSystem.GetBanReason` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2344 | `BanSystem.GetBanTime` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2353 | `ReportSystemBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2371 | `ClientReportPlayerSubsystem.GetFatalDamagerMap` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 2372 | `ClientReportPlayerSubsystem.GetFatalDamagerMapSize` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2373 | `ClientReportPlayerSubsystem.GetName2InfoMap` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2374 | `ClientReportPlayerSubsystem.GetCachedTeammateName2InfoMap` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2375 | `ClientReportPlayerSubsystem.GetTeammateName2InfoMapDuringBattle` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2376 | `ClientReportPlayerSubsystem.GetCurrentNotInTeamHistoricalTeammateMap` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2377 | `ClientReportPlayerSubsystem.GetInTeamIndexFromHistoricalTeammateInfo` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2379 | `ClientReportPlayerSubsystem.GetGameModeType` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2380 | `ClientReportPlayerSubsystem.GetMainModeID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2381 | `ClientReportPlayerSubsystem.GetSubModeID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2417 | `DSReportPlayerSubsystem._FindCacheByUID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2421 | `DSReportPlayerSubsystem._GetFatalDamagerMap` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2428 | `ui_complaint.SubmitReportData` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2429 | `ui_complaint._OnClickReport` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2430 | `ui_complaint._AddCommonTypesOfPlayerForReport` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2431 | `ui_complaint.AddPlayerForReport` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2432 | `ui_complaint.GetSelectedReasonAsArray` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2433 | `ui_complaint.GetSelectedSubReasonAsArray` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2434 | `ui_complaint.BlockPlayerChat` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2435 | `ui_complaint.IsBlockChatCheck` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2436 | `ui_complaint.CheckBoxBlack` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2437 | `ui_complaint.UpdateMatchBlackList` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2445 | `LogicComplaint.Submit` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2454 | `TLogBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2476 | `ToolReportUtil.ReParseError` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2508 | `MD5Bypass` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 2524 | `CMode.MD5HashByteArray` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2525 | `CMode.MD5HashFile` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2526 | `CMode.GetContentDiffData` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2535 | `_G.FileHashChecker.GetHash` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 2539 | `_G.STExtraBlueprintFunctionLibrary.GetMD5` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 2549 | `DNSDeviceBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2553 | `DeviceID.GetDeviceID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2554 | `DeviceID.GetAndroidID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2555 | `DeviceID.GetIMEI` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2556 | `DeviceID.GetMACAddress` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2557 | `DeviceID.GetUniqueDeviceID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2558 | `DeviceID.GetDeviceName` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2559 | `DeviceID.GetDeviceModel` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2560 | `DeviceID.GetDeviceBrand` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2561 | `DeviceID.GetDeviceManufacturer` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2562 | `DeviceID.GetDeviceBoard` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2563 | `DeviceID.GetDeviceBootloader` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2564 | `DeviceID.GetDeviceHardware` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2565 | `DeviceID.GetDeviceHost` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2566 | `DeviceID.GetDeviceFingerprint` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2567 | `DeviceID.GetDeviceSerial` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2571 | `DNS.Resolve` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2572 | `DNS.GetHostName` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2573 | `DNS.GetIPAddress` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2577 | `Network.GetIPAddress` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2578 | `Network.GetMACAddress` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2579 | `Network.GetSSID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2580 | `Network.GetBSSID` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2589 | `GokubaBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2593 | `Gokuba.ForwardFeature` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2623 | `RacingAntiCheatBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2636 | `RacingAntiCheatLogic._CreateVehicleData` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2652 | `CoronaLabBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2654 | `_G.LocalMain` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 2661 | `uOuterController.AddGameTimer` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2669 | `CHiggsBosonComponent.SecurityCoronaLabClientDataPointer` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2670 | `CHiggsBosonComponent.SetFloatValueByName` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2695 | `LoginModuleBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2700 | `login_module.aq_ban` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2702 | `login_module.device_num_limit` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2706 | `login_module.Login_Failed` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2707 | `login_module.aas_ban` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2708 | `login_module.PakMonitorStart` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2709 | `login_module.SetupFilenameHideKeywords` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2710 | `login_module.on_login_failed` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2711 | `login_module.DelaybanLoginCancelCallback` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 2722 | `SwiftHawkBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2741 | `ShootVerificationBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2763 | `ModifierExceptionBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2780 | `SimulateCharacterBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2795 | `PlayerSecurityBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2826 | `ClientFlowBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2849 | `GameplayCallbackBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2875 | `GC.OnDSPlayerStateChanged` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 2902 | `KillAllSubsystems` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2956 | `SLUABypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 2983 | `ReplayTelemetryBypass` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 3029 | `FinalProtection` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 3059 | `_G.require` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3084 | `InitializeAllBypass` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3126 | `SafeAddMark` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 3141 | `SafeRemoveMark` | 18 | active-internal | has one or more lexical references beyond its declaration |
| 3161 | `GetSafeEnemyKey` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 3181 | `CheckIsAI` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 3239 | `_G.InitializeAutoHeadHooks` | 4 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3254 | `hitLogic.GetHitBodyType` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 3260 | `hitLogic.GetHitBodyTypeByHitPos` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3280 | `_G.ForceEnableKillCounterUI` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3286 | `kcImpl.CheckSupportKCUI` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3287 | `kcImpl.CheckNeedMainKillCounterUI` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3294 | `kcImpl.UpdateMainKillCounterUI` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3305 | `LogicKillCounter.CheckSupportKC` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3306 | `LogicKillCounter.CheckSupportKillCounterAvatar` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3307 | `LogicKillCounter.CheckHasWeaponKillCounter` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3308 | `LogicKillCounter.GetBaseKillCounterIdByWeaponId` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3309 | `LogicKillCounter.GetEquipedKillCounterId` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3310 | `LogicKillCounter.GetMyEquipedKillCounterId` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3311 | `LogicKillCounter.GetOneWeaponKillCountInBattle` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3312 | `LogicKillCounter.GetWeaponKillCountByUid` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3328 | `mode.__inner_impl.CheckShowKCIcon` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 3348 | `_G.ForceEnableKillMessage` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3356 | `KillInfo.__inner_impl.FileItem` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3416 | `GetConfigPaths` | 5 | compatibility-only | explicit compatibility helper; not an authorization or visible menu feature |
| 3459 | `_G.SaveModSettings` | 18 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3509 | `_G.LoadModSettings` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3569 | `AutoSaveLoop` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 3591 | `_G.ReadLiveConfig` | 1 | compatibility-only | explicit compatibility helper; not an authorization or visible menu feature |
| 3609 | `_G.InitModMenuTab` | 7 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 3861 | `GuardMenuStack` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 3865 | `item.GetFunc` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3870 | `item.SetFunc` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3903 | `UIManager.ShowUI` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3947 | `ShowLexusVIPMenu` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 3956 | `Step_ScamAlert` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3962 | `Step_Welcome` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 3987 | `InitializeGraphicsUnlock` | 3 | legacy/unreachable | declaration is adjacent to a bypassed or removed runtime path |
| 4014 | `logic_setting_graphics.SetFPS` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 4025 | `fps_impl:GetMaxFPSLevel` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4026 | `fps_impl:InitRealSupportFPS` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4032 | `fps_impl:UpdateSelectedFPSState` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4052 | `clamp` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 4057 | `lerp` | 5 | active-internal | has one or more lexical references beyond its declaration |
| 4058 | `_getColorByPercent` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 4063 | `ft_impl.ShowOrHide` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 4068 | `ft_impl.InitFPSFTSwitch` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 4076 | `ft_impl.InitFPSFTValue165` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 4109 | `ft_impl.OnFPSFTValueChange3` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 4120 | `ft_impl.OnFPSFTSliderValueChange3` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4151 | `InitializeNativeESP` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4156 | `ApplyCfg` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4213 | `GetAllSkeletalMeshes` | 7 | active-internal | has one or more lexical references beyond its declaration |
| 4260 | `UndoWallXuyenTuong` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4284 | `ApplyWallXuyenTuong` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4383 | `_G.GetEnemyTargetsFromActors` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4417 | `_G.AimTouch` | 11 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4794 | `_G.RunOptimizedItemAndVehicleESP` | 2 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 4862 | `GK_DestroyWidget` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 4870 | `GK_GetOrCreateWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 4918 | `GK_UpdateWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 4940 | `_G.InitializeADESPSystem` | 3 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 5016 | `SafeToString` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5036 | `CleanPlayerName` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5049 | `GetActualPlayerName` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5066 | `IsAIPawn` | 8 | active-internal | has one or more lexical references beyond its declaration |
| 5091 | `IsPawnAlive` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5106 | `IsPawnKnocked` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5110 | `IsPawnRenderable` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5114 | `GetHPString` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5123 | `GetDynamicScale` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5132 | `GetHeadWorldPosition` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5158 | `AddDebugTextSafe` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5180 | `GetHeadRelativeOffset` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5194 | `UpdateDistanceText` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5210 | `UpdateHealthBar` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5241 | `SetNativeColor` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5269 | `SetNativeText` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5278 | `SetNativeHealth` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5302 | `GetStableESPCanvas` | 7 | active-internal | has one or more lexical references beyond its declaration |
| 5323 | `UpdateStableCanvasTransform` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5368 | `ProjectToStableCanvas` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5381 | `AK_GetFirstWeaponInBackpack` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5395 | `AK_GetCharacterWeapon` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5424 | `StableBoxWidth` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5430 | `QuantizeTagCoordinate` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5436 | `GetSmoothedTagPosition` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5467 | `CreateStableEntityBox` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5579 | `UpdateStableEntityBox` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5723 | `RemoveStableEntityBox` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5733 | `HideNativeLabelControls` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 5744 | `CreateOrGetNativeHeadWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5779 | `UpdateNativeHeadWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5832 | `RemoveNativeHeadWidget` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5870 | `SetCounterChildVisibility` | 13 | active-internal | has one or more lexical references beyond its declaration |
| 5879 | `HideCounterNonBackgroundControls` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5891 | `SetCounterBackgroundColor` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5903 | `ShowCounterTextControls` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5915 | `ApplyCounterTextAppearance` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5943 | `StretchCounterBackground` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 5958 | `PositionCounterLayer` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 5970 | `CreateCounterBackgroundLayer` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 5994 | `SetCounterText` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 6019 | `CreateCounterTextLayer` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 6041 | `RemoveCounterBorderOverlay` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6055 | `UpdateCounterVisualEffects` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 6081 | `UpdateCounterBorderOverlay` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6121 | `CreateCounterBorderOverlay` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6191 | `_G.RemoveCounterVisualLayers` | 5 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 6212 | `CreateEnemyCounterWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6252 | `ScreenToStableCanvas` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6261 | `IsTargetInFront180` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6281 | `GetScreenEdgeEndpoint` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6298 | `GetESPLineEndpoint` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6314 | `CreateESPLineWidget` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6354 | `RemoveESPLineWidget` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6366 | `UpdateESPLine` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6408 | `UpdateESPLineSet` | 2 | active-internal | has one or more lexical references beyond its declaration |
| 6432 | `_G.ClearESPLineWidgets` | 5 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 6445 | `UpdateAllESP` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6658 | `StartEnemyCounter` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 6663 | `initTimer` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 6691 | `_G.ClearAllADESPWidgets` | 9 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 6729 | `MainLoop` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6774 | `SystemLib.GetDeviceId` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 6798 | `_G.GetOriginalHWID` | 1 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 7397 | `GetFirstElemSafe` | 1 | external-callback | declaration-only candidate; treated as a possible engine callback rather than a current user feature |
| 7992 | `DrawBombs` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 8160 | `ExpiredTick` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 8187 | `FastTick` | 5 | legacy/unreachable | declaration is adjacent to a bypassed or removed runtime path |
| 8241 | `MortarNotify` | 7 | active-internal | has one or more lexical references beyond its declaration |
| 8251 | `MortarValid` | 9 | active-internal | has one or more lexical references beyond its declaration |
| 8264 | `MortarNormalizeAngle` | 6 | active-internal | has one or more lexical references beyond its declaration |
| 8273 | `MortarAtan2` | 5 | active-internal | has one or more lexical references beyond its declaration |
| 8286 | `MortarSolveBallisticPitch` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 8303 | `MortarReverseMapPitch` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 8313 | `MortarGetBallistics` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 8335 | `IsMortarWeapon` | 3 | active-internal | has one or more lexical references beyond its declaration |
| 8348 | `_G.MortarAimTick` | 5 | external-callback | global export or object method; may be invoked by the game/UI framework |
| 8496 | `MortarAim.Start` | 4 | active-internal | has one or more lexical references beyond its declaration |
| 8509 | `InitAllModSystems` | 9 | legacy/unreachable | declaration is adjacent to a bypassed or removed runtime path |

## Referenced `LexusConfig` flags

`Accuracy`, `ADESP_Enable`, `ADESP_ShowCounter`, `ADESP_ShowTags`, `ADESP_ShowTracers`, `ADESP_ShowWeapon`, `AimbotMode`, `AimTouchEnable`, `AimTouchHipfire`, `AimTouchHipIgBot`, `AimTouchHipIgKnock`, `AimTouchHipVisCheck`, `AimTouchScopeAll`, `AimTouchScopeIgBot`, `AimTouchScopeIgKnock`, `AimTouchScopeSniper`, `AimTouchScopeVisCheck`, `AimTouchSG`, `AimTouchSGAutoFire`, `AimTouchSGIgBot`, `AimTouchSGIgKnock`, `AimTouchSGVisCheck`, `AimTouchSniperIgBot`, `AimTouchSniperIgKnock`, `AimTouchSniperVisCheck`, `AutoHead`, `BlackSky`, `Crosshair`, `CustomAimbot`, `CustomAimbotClose`, `CustomHRecoil`, `CustomVRecoil`, `Esp3ShowHP`, `Esp3ShowName`, `Esp7_SoLuong`, `Esp7_TuThe`, `Esp7_VuKhi`, `EspActiveBom`, `EspAntenna`, `EspBomMaster`, `EspDistance`, `EspItemBom`, `EspLoai5`, `EspLoai6`, `EspLoai7`, `EspLoai8`, `EspOutline`, `EspRadar`, `EspVeh_Buggy`, `EspVeh_Coupe`, `EspVeh_Dacia`, `EspVeh_Mirado`, `EspVeh_Motor`, `EspVeh_Other`, `EspVeh_UAZ`, `EspVehicle`, `EspVip`, `EspVipPro`, `FakeHWID`, `FastCar`, `GodMode`, `IpadView`, `KillMessageEnable`, `LessRecoil`, `LessShake`, `MortarAim`, `OutlineThickness`, `RemoveFog`, `RemoveGrass`, `RemoveTrees`, `TPPView`, `UnlockFPS`, `VerticalRecoil`, `WallClimb`, `WallV2_Bot`, `WallV2_CustomColor`, `WallV2_Player`, `WallVehicle`, `WallXuyenTuong`, `WeaponGlow`
