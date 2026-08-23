# Local Verification Record

The project was validated locally after copying the supplied canonical implementation into an independent full-stack panel and applying the fresh schema.

| Check | Result |
|---|---|
| Database schema | Applied successfully. The starter authentication table was evolved to the Super Owner, Owner, and Admin access model; license, hashed device-binding, referral, audit, maintenance, announcement, and Telegram tables were created. |
| Canonical API smoke test | `POST /api/license/validate` with an empty key and device ID returned HTTP `400`, the documented fail-closed JSON response contract, rate-limit headers, and `Cache-Control: no-store`. |
| Automated tests | `22` Vitest files and `145` assertions passed. The suite covers validation decisions, role boundaries, device behavior, announcements, maintenance, Lua integration order, Telegram isolation, release-domain configuration, dashboard access states, diagnostics, visible client-side form-validation guidance, and behavior-level blocking of invalid generation and management submissions. |
| Component interaction tests | The jsdom interaction test passed: invalid key-generation and managed-license device limits display the validation alert and do not invoke the corresponding `create` or `update` tRPC mutation. |
| TypeScript | `pnpm check` completed successfully. |
| Production build | `pnpm build` completed successfully. The build reports a non-blocking JavaScript bundle-size warning for the approximately `851 kB` minified client bundle. |
| Lua syntax | `luac -p` completed successfully for `BRPlayerCharacterBase-Merged.lua` and `BRPlayerCharacterBase-Reusable.lua`. |
| Interface | The protected dashboard, sign-in gate, mobile layout, Super Owner controls, username onboarding modal, key generation, empty registry, access-state loading/error gates, diagnostics copy, permission-denied screens, and invalid-form guidance were reviewed from the implemented interface and regression suite. The automated browser context did not retain the owner's session cookie, so an authenticated live-session visual capture remains an owner-side check. |

## Remaining owner actions

Before publishing, configure the project title and confirm the configured owner identity. After publishing, bind a stable HTTPS domain, run the Lua release generator with that domain, and conduct a real authorized runtime test using the target host's documented callback contract. Also confirm the authenticated management screens using the owner browser; the automated browser session used for this record displayed the access gate even after owner-side sign-in, so it cannot certify that last visual step. The project does not seed licenses, users, referrals, raw device IDs, shared secrets, or Telegram credentials.
