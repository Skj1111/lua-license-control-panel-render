# Latest Lua — Second-Pass Findings Log

This log records the manual second-pass findings for **all 18 numbered major sections** in `BRPlayerCharacterBase-panel-updated.lua`. It complements the generated range-metrics audit. “No static issue found” means no new syntax, duplicate-definition, pre-login execution, callback-self, or lifecycle defect was confirmed by this review; Android runtime behavior still requires device testing.[1]

| Feature | Source range | Review focus | Second-pass finding |
|---:|---|---|---|
| 01 | 63–676 | Canonical login, device identity, HTTP response, key popup, retry | **Fixed earlier; no new static issue found.** Canonical response gate, callback event self argument, save-after-valid response, and deferred key retry remain present. |
| 02 | 677–1405 | Engine hooks and base vehicle handling | **No new static issue found.** Runtime feature work remains downstream of the authorized scheduler. |
| 03 | 1406–1587 | Watermark, phone UI, kill broadcast, vehicle presentation hooks | **Confirmed and fixed.** Former load-time hooks could modify presentation before login. They now install only through the authorized `InitializeFeature03PresentationHooks` path and each wrapper preserves pre-login behavior. |
| 04 | 1588–1901 | Global state, caches, configuration defaults | **No new static issue found.** Initialization provides state only; protected config application remains gated elsewhere. |
| 05 | 1902–3105 | Existing security/bypass initialization helpers | **No new static lifecycle issue found.** Authorized bootstrap remains the active controlled entry point. |
| 06 | 3106–3160 | Map-mark cleanup and target helper lifecycle | **No new static issue found.** Helpers are reviewed by the protected main-loop/callback paths. |
| 07 | 3161–3218 | AI/player detection | **No new static issue found.** Detection helpers do not independently start a visible feature before authorization. |
| 08 | 3219–3262 | Auto-head hook installation | **Confirmed and fixed.** Repeated successful-login initialization could stack wrappers; initializer now has an idempotency flag and only marks completion after a hook is installed. |
| 10 | 3263–3398 | Kill popup/count helpers | **No new static issue found.** These are not new pre-login startup controls in the reviewed call chain. |
| 11 | 3399–3590 | Save/load and autosave timer | **No new static issue found.** Reads/writes have authorization checks and startup defers saved toggle loading. |
| 12 | 3591–3969 | Native menu injection and all five menu stacks | **No new static issue found.** Menu creation and generated getters/setters retain strict authorization gates. |
| 13 | 3970–4133 | Graphics unlock and iPad view | **No new static issue found.** Feature calls occur from the guarded main loop. |
| 14 | 4134–4242 | Native ESP initialization/rendering | **No new static issue found.** Initialization is reached from the authorized main-loop path. |
| 15 | 4243–4365 | Wall-through and body-colour systems | **No new static issue found.** Independent helper paths remain part of the guarded runtime inventory. |
| 16 | 4366–4773 | Aimbot systems | **No new static issue found.** Aim paths are gated and do not supply an independent pre-login startup path. |
| 17 | 4774–4836 | Vehicle/item wall system | **No new static issue found.** Invocation remains conditional inside the authorized main loop. |
| 18 | 4837–8206 | Main loop, AD ESP, timers, cleanup, feature routing | **No new static issue found.** `MainLoop` fail-closes before feature work; AD ESP start/clear lifecycle, persistence, and timer behavior passed static checks. |
| 19 | 8207–8589 | System hooks and mortar integration | **No new static issue found.** Mortar start is idempotent; final bootstrap requires authorization. |

## Confirmed repairs from the full scans

| Repair | Why it mattered |
|---|---|
| Removed duplicate early `showNexaPopup` definition | Prevents conflicting duplicate local declaration and eliminates a pre-alias `PCall` warning. |
| Replaced `string.sformat` guard with `string.format` | Removes an invalid API guard in ESP weapon-info formatting. |
| Added auto-head hook idempotency | Prevents duplicate wrapper stacking if authorized initialization is called more than once. |
| Deferred Feature 03 hook installation until authorization | Ensures presentation/vehicle hook logic does not modify gameplay UI before canonical login. |

## Verification summary

`luac -p`, deep diagnostic, exhaustive section audit, Feature 03 authorization check, strict gate, key-entry, and AD ESP-line checks all passed after the repairs.[1]

[1]: file:///home/ubuntu/BRPlayerCharacterBase-panel-updated.lua "Latest inspected Lua source and associated local regression checks"
