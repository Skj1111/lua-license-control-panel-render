# Latest Lua — Complete Feature Details

**Lua file:** `BRPlayerCharacterBase-panel-updated.lua`  
**Language:** Hinglish / Urdu-style explanation, with exact menu labels preserved.  
**Scope:** Is reference mein current Lua ke **visible menu controls**, **login/persistence/startup behavior**, aur audited **non-menu runtime systems** cover hain. Commented, legacy-only, ya sirf declaration-level paths ko current user feature nahi maana gaya.[1] [2]

> **Sabse important rule:** Pehle panel se valid login hona zaroori hai. Valid server response ke bina menu show nahi hoga aur protected feature paths run nahi honge.[1]

## 1. Login, key, aur authorization system

| System | Kaise kaam karta hai |
|---|---|
| **ENTER YOUR KEY popup** | Agar usable saved key nahi milti, native popup khulta hai. Key paste karke **OK** press karo. |
| **Canonical panel validation** | Lua published panel ke `/api/license/validate` endpoint par key aur current device identity bhejta hai. |
| **Server final decision leta hai** | Login tabhi approved hota hai jab response `success: true`, `status: valid`, aur non-empty expiry time deta hai. Local menu khud login enable nahi kar sakta. |
| **`key.json` automatic save** | Sirf valid server approval ke baad `key.json` write hoti hai. Wrong key ko file mein save nahi karta. |
| **Next launch auto-login** | Next startup par pehle saved key validate hoti hai. Valid ho to authorization aati hai; invalid ho to popup/retry flow aata hai. |
| **Key compatibility** | Reader pehle `key.json` dekhta hai; purane `key.txt`, `key.in`, aur `key.ini` fallback ke liye read hote hain. Current write target `key.json` hai. |
| **Device identity** | Host phone identity ko priority milti hai; phir supported provider; phir local fallback. Raw device value ka menu mein display/log nahi hota. |
| **Fail-closed security** | Login fail, expiry, network error, ya unauthorized state par menu hide hota hai, boolean feature flags off hote hain, aur AD ESP widgets clear ho sakte hain. |
| **Retry** | Popup cancel, invalid saved key, failed submit, ya UI-open issue par bounded retry/reopen logic hai. Maximum retry ke baad popup manual reopen ka message deta hai. |

## 2. Current visible menu tabs

| Tab | Isme kya hai |
|---|---|
| **AD ESP** | Stable tags, HP bar, top tracer line, enemy counter, aur weapon info. |
| **ESP** | Multiple ESP styles, grenade alert, vehicle filter, antenna aur outline. |
| **Aimbot** | Long/close aimbot, recoil options, crosshair, rapid fire, mortar auto aim. |
| **Royal Aimbot & Custom** | Hipfire, shotgun, scoped normal gun, aur sniper/DMR ke separate profiles. |
| **Support & Graphics** | View/FPS, Wallhack V2 custom colours, environment toggles, vehicle controls, aur weapon glow. |

## 3. AD ESP — Advanced Stable Tags & Tracers

| Menu control | Detail |
|---|---|
| **Enable AD ESP (Advanced Stable Tags & Tracers)** | AD ESP ka master toggle. Off karne par AD ESP widgets clean hote hain. |
| **Show Stable Tags & HP Bar** | Enemy/bot ke stable label aur HP bar ko show karta hai. |
| **Show Top-Origin Tracers** | Top counter origin se target tak tracer line draw karta hai. |
| **Show Top Enemy Counter** | Player count, bot count, aur nearest distance ka top counter. Enemy nahi ho to `ZONE CLEAR` state. |
| **Show Enemy Weapon Icon** | AD ESP target information ke saath weapon detail show karta hai. |

### AD ESP tracer / Snap Line stability

| Fix | Result |
|---|---|
| **2px geometry threshold** | Target endpoint start/end 2 pixel se kam move kare to line ka position, size aur angle redraw nahi hota. Isse small shake/jitter kam hota hai. |
| **Cached endpoints** | Previous start/end positions save rehte hain; meaningful movement par hi line geometry update hoti hai. |
| **Cached colour** | Bot/player type same rahe to same brush colour dobara apply nahi hota. |
| **Cached visibility** | Line already visible ho to har refresh mein same visibility command nahi jaati. Yeh Snap Line se merge ki gayi compatible stability optimization hai. |
| **Cached vectors** | Position aur size vector reuse hote hain, har update mein unnecessary UI object creation kam hota hai. |
| **Small-batch cleanup** | Missing/inactive line widgets limited batch mein remove hoti hain; lobby/match transition ka heavy cleanup spike kam hota hai. |

## 4. ESP tab — all controls

| Menu control | Kaam |
|---|---|
| **ESP Type 1** | HP aur name information. |
| **ESP Type 2** | Distance meters mein show karta hai. |
| **ESP Type 3** | Vertical HP + name mode. Iske andar **Show Player Names** aur **Show Vertical HP Bar**. |
| **ESP Type 4** | 360 radar mode. |
| **ESP Type 5** | Box-style ESP. |
| **ESP Type 6** | Skeleton-style ESP. |
| **ESP Type 7 (Detailed Info)** | Detailed info master. Iske andar **Show Enemy Count**, **Show Enemy Weapon**, aur **Show Enemy State**—standing/crouch/prone. |
| **ESP Type 8** | Head HP-bar mode. |
| **ESP Item** | Yeh overlay toggle nahi hai; current code mein configured Telegram link open karta hai. |
| **Grenade Warnings & Location** | Master group: **Locate Grenades on the Ground** aur **Warn when Enemy Holds or Throws**. |
| **Vehicle ESP (Detailed)** | Vehicle group: Dacia, UAZ/Jeep, Buggy, Coupe RB, Mirado, Motor/Scooter, aur Other (Boat/BRDM) ke separate toggles. |
| **ESP Antenna** | Antenna display toggle. |
| **ESP Enemy Outline (BRIGHT with HDR)** | Bright outline master toggle. **Outline Thickness** range `1–20`. |

## 5. Aimbot tab — all controls

| Menu control | Kaam |
|---|---|
| **Custom Long-Range Aimbot** | Master toggle; **Long-Range Aimbot Speed** `1–100`; **Compensate Recoil** `0–50`. |
| **Custom Close-Range Aimbot** | Master toggle; **Close-Range Aimbot Speed** `1–100`. |
| **Reduce Horizontal Recoil** | Master aur value slider `0–100`; UI value internal recoil setting mein map hoti hai. |
| **Reduce Vertical Recoil** | Master aur value slider `0–100`; UI value internal recoil setting mein map hoti hai. |
| **Reduce Scope Shake** | Scope-shake toggle. Menu note: off ke baad gun drop/pickup ki zarurat ho sakti hai. |
| **Perfect Accuracy** | Straight-bullets / accuracy toggle. |
| **Small Crosshair** | Small crosshair toggle. Menu note: off ke baad gun drop/pickup ki zarurat ho sakti hai. |
| **Aimbot Head** | Head-target toggle. Menu note: off effect next match tak lag sakta hai. |
| **Rapid Fire** | Extremely-fast rapid-fire toggle. Menu note: off ke baad gun drop/pickup ki zarurat ho sakti hai. |
| **Mortar Auto Aim** | Standalone mortar auto-aim toggle. |

## 6. Royal Aimbot & Custom

**Enable Royal Aimbot & Custom** master toggle ke andar chaar independent profile hain. Har profile ka apna config save hota hai.[1]

| Profile | Controls |
|---|---|
| **Hipfire Aimbot** | Ignore Knocked, Ignore Bots, VisCheck, Priority, Target Bone, Condition, Smoothness/Speed, FOV Radius, Max Distance. |
| **Shotgun Aimbot** | Auto Fire, Ignore Knocked, Ignore Bots, VisCheck, Priority, Target Bone, Condition, Smoothness/Speed, FOV Radius, Max Distance. |
| **Scoped Aimbot** | Ignore Knocked, Ignore Bots, VisCheck, Priority, Target Bone, Condition, Smoothness/Speed, FOV Radius, Max Distance, Moving Target Prediction, Auto Recoil Compensation. |
| **Scoped Aimbot (Snipers/DMRs)** | Ignore Knocked, Ignore Bots, VisCheck, Priority, Target Bone, Condition, Smoothness/Speed, FOV Radius, Max Distance, Moving Target Prediction. |

| Common slider | Values / meaning |
|---|---|
| **Priority** | `1` Crosshair, `2` Distance, `3` HP, `4` HP%. |
| **Target Bone** | `1` Head, `2` Chest, `3` Stomach, `4` Waist. |
| **Condition** | `1` Aim on Fire, `2` Always Aim. |
| **Smoothness / Speed** | `1–100`. |
| **FOV Radius** | `1–100`. |
| **Hipfire / Scoped / Sniper Max Distance** | UI `1–100`, code mapping ke baad `5–500m`. |
| **Shotgun Max Distance** | Direct `1–100m`. |
| **Prediction** | Scoped aur Sniper profile mein `0–100`. |
| **Scoped Auto Recoil** | `0–50`. |

## 7. Support & Graphics tab — all controls

| Menu control | Kaam |
|---|---|
| **Fake HWID** | Menu-labeled device-ID behavior toggle. |
| **iPad View** | Master toggle + **FOV Slider**. UI `1–100`; script configured FOV ko `91–190` ke range mein store karta hai. |
| **TPP View** | Force third-person view toggle. |
| **Unlock 165 FPS** | FPS unlock toggle. Menu note: off effect next match tak lag sakta hai. |
| **Wallhack V2 (Custom Glowing Colors)** | Wallhack V2 ka master toggle. |
| **Use Custom Colors** | On = custom colour sliders; off = normal colour mode. |
| **Enable for Players / Bots** | Player aur bot custom colour apply karne ke separate toggles. |
| **Vehicle Wallhack** | Vehicle see-through-walls toggle. |
| **Black Sky** | Environment visual toggle. |
| **Remove Fog** | Fog removal toggle; menu note next match effect possible. |
| **Remove Grass** | Grass removal toggle; menu note next match effect possible. |
| **Remove Trees** | Trees removal toggle; menu note next match effect possible. |
| **Wall Climb** | Wall-climb toggle. Code disable par cached movement values restore karta hai. |
| **Fast Flying Car** | Valid vehicle mein existing movement/velocity path use karta hai. |
| **Weapon Glow (HDR Brightness)** | Weapon glow master, colour selector, aur thickness. |

### Wallhack V2 custom colour sliders

Player aur Bot dono ke paas **Open** aur **Behind** state hoti hai. Har state mein same 5 sliders hain.[1]

| Slider | Range | Meaning |
|---|---:|---|
| **R / G / B** | `0–300` | Red, Green, Blue intensity. Menu `255` se upar values bhi accept karta hai for brighter output. |
| **A** | `0–255` | Alpha / opacity. |
| **L** | `0–50` | Flow / glow intensity. |

| Target | States |
|---|---|
| **Player** | Player Open `R/G/B/A/L`; Player Behind `R/G/B/A/L`. |
| **Bot** | Bot Open `R/G/B/A/L`; Bot Behind `R/G/B/A/L`. |

### Weapon Glow sliders

| Control | Values |
|---|---|
| **Weapon Color** | `1` Red, `2` Green, `3` Blue, `4` Yellow, `5` Rainbow. |
| **Weapon Glow Thickness** | `1–15`. |

## 8. Non-menu runtime systems — active behavior

Yeh systems menu rows nahi hain, lekin latest Lua mein active startup/main-loop paths se connected hain.[1]

| Runtime system | Kab / kya hota hai |
|---|---|
| **Delayed startup** | Script short delay ke baad login check run karta hai; timer na mile to direct fallback. |
| **Authorization state machine** | `validating`, `authorized`, `disabled` state use karta hai. Disabled state menu aur protected features ko block karti hai. |
| **Key popup retry** | Cancel, invalid key, server failure, ya UI failure par controlled retry schedule karta hai. |
| **Settings load** | Valid login ke baad `nexaconfig.json` load karta hai; legacy `NEXA_settings.txt` import compatibility ke liye read hoti hai. |
| **Settings autosave** | Authorized state mein changed config data ko approximately 3-second lightweight loop mein save karta hai. |
| **Menu lifecycle** | Valid login par menu inject; authorization loss par hide/unregister/remove ka attempt. |
| **Authorized bootstrap** | Valid authorization ke baad existing system setup, auto-head hook initialization, mortar initialization aur TPP switch setup call karta hai. |
| **AD ESP lifecycle** | Master on ho to start; off/unauthorized ho to widgets clear aur running state reset. |
| **Native ESP preparation** | Current native screen-mark config ko existing ESP renderer ke liye prepare karta hai. |
| **Vehicle/item runtime** | Vehicle Wallhack enabled ho to existing optimized item/vehicle ESP path call hota hai. |
| **ESP Type 7 weapon cache** | Type 7 weapon toggle on ho to enemy weapon aur bot/player classification data refresh hota hai. |
| **Weapon Glow cadence** | Weapon glow renderer ko roughly `0.5s` cadence par call karta hai, har loop pass par nahi. |
| **iPad View restore** | iPad View on par configured FOV, off par `90` restore karne ka path. |
| **Graphics restore** | Grass, trees, fog aur black-sky state track karke toggle off par restore commands attempt karta hai. |
| **Lobby/death cleanup** | Valid local player na ho to tracked marks, enemy materials aur temporary graphics state clear karta hai. |

## 9. Legacy / compatibility / excluded paths

Is list ke paths source mein milte hain, lekin current user feature list mein include nahi kiye gaye.[1] [2]

| Path type | Kyun exclude hai |
|---|---|
| **Manual `LoginWithKey` helper** | Ye fail-closed helper hai; server validation bypass karke access nahi deta. |
| **Offline expiry branch** | Local time-limit branch explicitly bypassed hai; current decision canonical server response se hota hai. |
| **Old key/config formats** | Ye sirf read compatibility hai, new independent feature nahi. |
| **Kill-message / kill-counter helper globals** | Five visible menu stacks ya audited startup/main-loop call chain mein current user control ke roop mein invoke nahi hote. Isliye feature tab mein show nahi kiye gaye. |
| **Maintenance / announcement / Telegram ticket routes** | Delivered gameplay Lua mein in panel presentation routes ka active invocation nahi mila. Active Telegram-related visible row sirf **ESP Item** external link hai. |
| **Commented / bypassed code** | Comment, disabled branch, aur legacy declaration ko usable current feature nahi maana gaya. |
| **Declaration-only lexical candidates** | Full scan mein sirf declaration par milne wale helper candidates generated appendix mein listed hain. External game callback prove na ho to unko user-facing feature claim nahi kiya gaya. |

## 10. Full-source audit proof

Complete delivered Lua source ka lexical scan kiya gaya. Yeh scan menu section tak limited nahi tha.[2]

| Audit metric | Result |
|---|---:|
| **Lua source lines scanned** | `8,595` |
| **Function declarations found** | `348` |
| **Exported `_G` helper declarations** | `23` |
| **Registered menu stacks** | `5` |
| **Referenced `LexusConfig` flags** | `80` |
| **Active-internal declarations** | `166` |
| **External-callback declarations** | `176` |
| **Compatibility-only declarations** | `3` |
| **Legacy/unreachable declarations** | `3` |

### Static classification ka matlab

| Static class | Hinglish meaning |
|---|---|
| **active-internal** | Declaration ke source mein definition ke alawa lexical reference bhi mila. Yeh current internal flow ka evidence hai. |
| **external-callback** | Global export, object method, ya declaration-only callback candidate. Engine/UI callback ho sakta hai, lekin Android runtime trace ke bina isko separate user feature claim nahi kiya gaya. |
| **compatibility-only** | Existing compatibility helper—jaise old config/key support ya fail-closed helper—not a new visible feature. |
| **legacy/unreachable** | Declaration bypassed/removed runtime context ke paas mila; current active control ke roop mein count nahi kiya gaya. |

**Generated appendix:** `LATEST_LUA_RUNTIME_INVENTORY.md` mein **all 348 declarations** line number, source-occurrence count, static class, aur evidence ke saath listed hain. Isse visible controls, active internal paths, callback candidates, compatibility helpers, aur legacy exclusions ko repeatably cross-check kiya ja sakta hai.[2]

> **Reality check:** Source audit confirm karta hai ki kaunse systems/code paths current Lua mein registered aur referenced hain. Actual Android/game behavior final mein device, game build, match scene, aur runtime support par depend karega. Tracer smoothness aur second-match lifecycle ko real match mein test karna zaroori hai.

## References

[1]: file:///home/ubuntu/BRPlayerCharacterBase-panel-updated.lua "Latest delivered Lua — login, menu, AD ESP, main-loop, persistence, and startup source"
[2]: ./LATEST_LUA_RUNTIME_INVENTORY.md "Generated full-source runtime inventory for the latest delivered Lua"
