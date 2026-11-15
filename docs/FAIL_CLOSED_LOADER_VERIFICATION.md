# Fail-Closed License Loader Verification

The current `BRPlayerCharacterBase-Merged.lua` and `BRPlayerCharacterBase-Reusable.lua` artifacts are license loaders, not a replacement for a gameplay runtime. Their continuation boundary is intentionally fail-closed:

1. The loader opens the key-entry flow or reads a saved key.
2. It sends the key and device identity to the canonical `/api/license/validate` endpoint.
3. The asynchronous response callback requires the matching request index, HTTP 200, a decodable response, `status == "valid"`, and `success == true`.
4. Only then does it persist the validated key, pass through the maintenance gate, and invoke `run_authorized_main`.
5. Invalid, expired, device-mismatch, device-limit, malformed, or transport-failure outcomes stop without invoking the main continuation.
6. The top-level loader returns `nil`; it does not use `loadfile` or run the main continuation directly.

The repeatable checks are:

```bash
node scripts/verify-fail-closed-license-loader.mjs BRPlayerCharacterBase-Merged.lua
node scripts/verify-fail-closed-license-loader.mjs BRPlayerCharacterBase-Reusable.lua
luac -p BRPlayerCharacterBase-Merged.lua
luac -p BRPlayerCharacterBase-Reusable.lua
pnpm vitest run server/failClosedLuaLoader.test.ts
```

The Android host is still required for runtime confirmation of popup rendering, key entry, and the actual valid/invalid response behavior. No Android runtime result is inferred from this static verification.
