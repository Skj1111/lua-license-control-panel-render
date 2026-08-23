# Gameplay Lua Authorization Boundary

The distributed `BRPlayerCharacterBase-panel-updated.lua` uses one canonical check, `HasPanelAuthorization()`. It returns true only when the current session’s `NexaLoginPanel` state is `authorized`, which is set only after the panel’s canonical validation response succeeds.

| Entry point or path | Unauthorized behavior |
|---|---|
| `InitModMenuTab` | Refuses initialization and returns `false`. |
| Settings `UIManager.ShowUI` hook | Removes the `ModMenu` page from the settings catalog. |
| Every generated control in the five menu stacks | The shared wrapper returns `false` from both `GetFunc` and `SetFunc`. |
| `MainLoop`, called by `FastTick` | Clears active AD ESP visuals if necessary and returns before any feature work. |
| `ApplyTPPView`, `InitializeADESPSystem`, and `UpdateAllESP` timer callback | Each returns immediately before login. |
| Standalone global gameplay helpers | `InitializeAutoHeadHooks`, `ForceEnableKillCounterUI`, `ForceEnableKillMessage`, `GetEnemyTargetsFromActors`, `AimTouch`, `RunOptimizedItemAndVehicleESP`, `InitializeADESPSystem`, and `MortarAimTick` each have an explicit pre-login return. |
| Installed auto-head hook callbacks | Each checks authorization at callback time, so a hook installed during a previously valid session cannot apply the feature after authorization is lost. |
| `InitAllModSystems` | Returns immediately before login. |
| `LoadModSettings`, `SaveModSettings`, and `ReadLiveConfig` | Refuse to load or apply saved toggles, or write config, before login. Startup deliberately defers saved-toggle loading. |
| Login scheduler | Only runs `CheckLoginAndInit`; it does not start feature work. |
| Autosave scheduler | May reschedule, but its guarded save callback cannot apply or write feature configuration before login. |

## Complete scheduled and local feature-start inventory

The inspection script also reviews the scheduled/local paths below. Engine-maintenance callbacks for vehicle attachment have no `LexusConfig` or feature renderer access and are classified as engine-only. The CoronaLab timer override is reached from the authorized `InitAllModSystems` chain through its guarded bypass initializer.

| Path | Classification | Authorization boundary |
|---|---|---|
| `FastTick` | Main feature scheduler | Calls `MainLoop`, whose first operation is the fail-closed return. |
| `MainLoop` → graphics/native ESP initialization | Local feature-start chain | Runs only after the `MainLoop` authorization boundary. |
| `UpdateAllESP` / its game timer | AD ESP feature callback | Direct authorization return before any renderer work. |
| `MortarAim.Start` / repeating `MortarAimTick` | Feature timer | Started by guarded `InitAllModSystems`; tick also checks authorization directly. |
| `AutoSaveLoop` | Configuration I/O scheduler | Its save function refuses unauthorised calls. |
| `CheckLoginAndInit` delayed startup | Authentication only | It validates the saved panel key and does not start feature work itself. |
| `ExpiredTick` delayed callback | Disabled notification path | The enclosing condition is permanently false; it does not apply feature effects. |
| Vehicle attachment timers | Engine-only maintenance | No mod configuration or visual/aim feature path. |

The full structural regression validates the **14** standalone global helpers and **8** callback/startup paths listed above, plus the menu and persisted-config boundaries.

The static regression command is:

```bash
luac -p /path/to/BRPlayerCharacterBase-panel-updated.lua
node scripts/verify-gameplay-lua-auth-gate.mjs /path/to/BRPlayerCharacterBase-panel-updated.lua
```

## Owner-run Android smoke checks

The structural regression passed in the sandbox, but the target Android runtime is not available here. Run these two checks with the delivered Lua file before using it broadly:

| Check | Setup | Expected result |
|---|---|---|
| Invalid/missing-key check | Remove the saved key file, or use a deliberately invalid panel key; then launch the script. | The authorization popup reports failure. **NEXA MOD MENU** is absent from Settings, saved toggles are not loaded, and no visual, aim, vehicle, graphics, or counter effect operates. |
| Valid-key check | Place an active panel key in `key.json`, launch the script, and wait for panel validation success. | The success popup appears first. Only then does **NEXA MOD MENU** become visible; toggles can be enabled and their selected effects operate. |

For the key-entry popup, pressing **Cancel**, a popup-open failure, or an invalid saved-key response resets the login-attempt flag and provides a bounded retry path. The prompt automatically retries up to three times when the UI is temporarily unavailable; an invalid key shows a **RETRY** action that reopens the prompt.

If either result differs, capture the first popup plus the Settings page and send them for investigation.

The checked-in inspection currently verifies **14** standalone global helpers, **5** menu stacks, **12** independent feature paths, and **3** persisted-configuration paths, in addition to Lua parsing through `luac -p`.
