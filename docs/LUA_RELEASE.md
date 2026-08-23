# Lua Wrapper Release Configuration

The repository keeps the inherited wrapper source for regression comparison. **Do not use its inherited endpoint in an independent deployment.** Create release copies only after the new panel has its own stable HTTPS domain.

Use the following command from the project directory, replacing the example origin with the new public panel origin:

```bash
PANEL_ORIGIN=https://panel.example.com node scripts/render-lua-release.mjs
```

The command writes these separately configured artifacts:

```text
release/BRPlayerCharacterBase-Merged.lua
release/BRPlayerCharacterBase-Reusable.lua
```

Only an HTTPS origin without a path is accepted. The generated wrappers point to the new project's canonical validation and post-validation routes. Before distributing either artifact, run `luac -p` on the rendered files and validate the target host's documented HTTP callback contract with a non-sensitive test key.

