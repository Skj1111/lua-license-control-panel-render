# ESP Antenna Source Audit

**Scope.** This note records a source-only audit of `/home/ubuntu/BRPlayerCharacterBase-panel-updated.lua`. No gameplay Lua behavior was changed.

| Area | Verified finding |
|---|---|
| Default configuration | `EspAntenna` is present and defaults to `false`. |
| Menu control | The ESP menu exposes `ModMenu_ESPAntenna`; its getter and setter are wired to `_G.LexusConfig.EspAntenna`. |
| Runtime branch | When enabled, the branch draws eight green vertical `|` markers and one `I` marker over an entity through the HUD debug-text path. |
| Range bound | The rendering branch has a `distM <= 400` condition. |
| Login protection | The menu construction path is covered by the existing canonical panel authorization gate, so the menu remains hidden before successful panel authorization. |

The Lua syntax check passed. No confirmed **source-level** defect was found in the configuration, menu binding, or visible rendering branch during this audit.

> This is not an Android runtime confirmation. Only the owner can verify on the target device that the markers appear correctly, toggle off cleanly, and do not create an in-match performance issue.
