# Lua License Control Panel

This project is an **independent full-stack license-management platform**. It provides the canonical `POST /api/license/validate` contract, server-authoritative keys and device bindings, referral-based role activation, Super Owner controls, maintenance and announcement presentation flows, and an optional isolated Telegram extension.

> **Independent deployment requirement:** This project must use its own database, owner identity, OAuth application, secrets, and stable HTTPS domain. Do not copy a prior panel's credentials, OAuth session, database records, raw device identifiers, or Telegram tokens.

## Core guarantees

| Area | Behavior |
|---|---|
| License validation | `POST /api/license/validate` accepts JSON `{ "key": string, "device_id": string }`, fails closed, rate-limits requests, and returns `Cache-Control: no-store`. |
| Device privacy | The server derives a keyed hash from the submitted device identity and stores only the hash. Raw device IDs are never stored in the database or audit history. |
| Device ownership | A device belongs to one license at a time. It can move only from a revoked or expired source key; active-key replacement is rejected. |
| Roles | Super Owner, Owner, and Admin permissions are enforced on the server. New signed-in accounts remain pending until an authorized referral is redeemed. |
| Presentation features | Maintenance and announcement routes are isolated from license decisions. Telegram tickets are short-lived, hashed, single-use, device-bound, and require an explicit mapping. |
| Lua execution gate | The Merged Lua flow performs storage preflight, identity resolution, canonical validation, maintenance and announcement presentation, then invokes protected main code only after authorization succeeds. |

## Required configuration

The managed environment supplies database, OAuth, owner, and session variables. Before publishing, configure the project title/logo and confirm the configured owner identity. After publishing, bind a stable HTTPS domain in project settings and update the production Lua wrappers to point at that domain's `/api/license/validate`, `/api/maintenance/active`, `/api/announcement/active`, and optional Telegram endpoints.

| Configuration | Purpose |
|---|---|
| `DATABASE_URL` | Separate MySQL/TiDB database for this panel. |
| `JWT_SECRET` | Server-side session signing. |
| `OWNER_OPEN_ID` | The approved Super Owner identity. |
| OAuth settings | Supported sign-in and callback flow for this project. |
| HTTPS domain | Stable public endpoint used by browser clients and released Lua wrappers. |
| Optional Telegram values | Only if a separate bot or channel policy is later configured; never place tokens in Lua or browser code. |

## Database and verification

The fresh schema defines users, licenses, device bindings, referrals, audit logs, maintenance, announcements, and Telegram extension tables. The starter user table is evolved to the extended role model. No production business data is seeded.

Run the verification suite before release:

```bash
pnpm test
pnpm check
pnpm build
luac -p BRPlayerCharacterBase-Merged.lua
```

The browser preview is intentionally protected by sign-in. A Super Owner must sign in using the configured owner account to view the full control surface; other accounts must redeem an authorized referral before management procedures become available.

## Lua integration release process

The repository includes `BRPlayerCharacterBase-Merged.lua` and `BRPlayerCharacterBase-Reusable.lua` as integration artifacts. Keep the wrapper above protected main code and retain the tested sequence:

```text
folder preflight → device resolution → canonical validation → maintenance gate
→ announcement → optional Telegram ticket/redeem → protected main code
```

The wrappers are not downloadable through the panel API. Release them through an approved build process only after replacing the inherited reference domain with this new project's stable HTTPS domain and validating the target Dynamic Lua host's documented HTTP callback contract. A missing callback, malformed response, transport error, unknown status, or failed validation must block protected code.

## Runtime transport safety boundary

The approved client bridge is `slua_GameFrontendHUD:GetHttpWrapper()`, `OnResponseEvent:Add(...)`, and `RequestForLua(..., "POST", ...)`. Protected main code runs only after a parseable HTTP-200 validation callback with `success=true` and the canonical valid status. If the callback reports `success=false`, the loader stops with `license server unreachable` and does not attempt protected execution.

The wrapper does not fall back to `HTTP_POST`, LuaSocket, LuaSec, hidden transport discovery, or undocumented native calls. The project does not implement client-side approval, remote response execution, hidden transport extraction, or memory/process injection.

### Getter-return probe decision table

| Observation | Required decision |
|---|---|
| A supported getter returns a primitive value | Treat it as local diagnostic evidence only; it does not prove HTTP support. |
| No `.getter_return_probe` is created and the Lua host remains alive | Treat the result as inconclusive and do not infer authorization. |
| The host closes, crashes, or silently stops during the test | Stop probing that method and obtain an official host contract instead. |

`pcall` can contain ordinary Lua errors but cannot guarantee recovery from a native host crash. In particular, observed names such as `GameMasterGetWebUIUrl` and `GetFileDirPath` require arguments; it does not establish their argument semantics, callback behavior, or response format.

References to `Http`, `WebRequest`, and `SimpleHttp` are not sufficient proof of a compatible request contract. Any friend or sample code that places a key in a URL or executes a remote response through `loadstring` is unsafe and must not be used for authorization.

## Deployment readiness

Create a final project checkpoint after checks pass, then use the project publishing control to deploy. Publishing is intentionally not automated from this project. Test the public HTTPS endpoint, OAuth callback, canonical validation responses, role boundaries, and the authorized Android runtime separately before describing the new panel as production-equivalent.
