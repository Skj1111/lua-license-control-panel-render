# Local Verification Record

The project was validated locally after copying the supplied canonical implementation into an independent full-stack panel and applying the fresh schema.

| Check | Result |
|---|---|
| Database schema | Applied successfully. The starter authentication table was evolved to the Super Owner, Owner, and Admin access model; license, hashed device-binding, referral, audit, maintenance, announcement, and Telegram tables were created. |
| Canonical API smoke test | `POST /api/license/validate` with an empty key and device ID returned HTTP `400`, the documented fail-closed JSON response contract, rate-limit headers, and `Cache-Control: no-store`. |
| Automated tests | `25` Vitest files and `153` assertions passed. The suite covers validation decisions, role boundaries, device behavior, announcements, maintenance, Lua integration order, Telegram isolation, release-domain configuration, dashboard access states, diagnostics, visible client-side form-validation guidance, behavior-level blocking of invalid generation and management submissions, authenticated Super Owner command surfaces, and direct protected maintenance, announcement, and Telegram save mutations. |
| Component interaction tests | The jsdom interaction test passed: invalid key-generation and managed-license device limits display the validation alert and do not invoke the corresponding `create` or `update` tRPC mutation. |
| TypeScript | `pnpm check` completed successfully. |
| Production build | `pnpm build` completed successfully. The build reports a non-blocking JavaScript bundle-size warning for the approximately `860 kB` minified client bundle. |
| Lua syntax | `luac -p` completed successfully for `BRPlayerCharacterBase-Merged.lua` and `BRPlayerCharacterBase-Reusable.lua`. |
| Interface | The protected dashboard, sign-in gate, mobile layout, Super Owner overview, Announcement Control, username onboarding modal, key registry, role controls, diagnostics copy, permission-denied screens, direct-route authorization-loading states, and invalid-form guidance were reviewed through the authenticated preview and regression suite. The Super Owner direct route now displays an explicit authorization-loading state rather than a transient false-denied screen while the identity query is resolving. The project owner also confirmed that the reported **Super Owner access required** save error is fixed after the active-role authorization correction. See `docs/AUTHENTICATED_UI_VERIFICATION.md` for the readable review record. |

## Remaining owner actions

Before publishing, configure the project title and confirm the configured owner identity. After publishing, bind a stable HTTPS domain, run the Lua release generator with that domain, and conduct a real authorized runtime test using the target host's documented callback contract. The project does not seed licenses, users, referrals, raw device IDs, shared secrets, or Telegram credentials.
