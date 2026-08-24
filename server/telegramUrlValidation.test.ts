import { describe, expect, it } from "vitest";
import { validateTelegramHttpsUrl } from "../client/src/lib/telegramUrl";
import { appRouter } from "./routers";

const activeAdminContext = {
  user: {
    id: 7,
    openId: "admin-test",
    name: "Admin",
    email: null,
    username: "admin",
    usernameChangedAt: null,
    loginMethod: "oauth",
    role: "admin" as const,
    accessStatus: "active" as const,
    accessExpiresAt: null,
    createdAt: new Date(),
    updatedAt: new Date(),
    lastSignedIn: new Date(),
  },
  req: {} as never,
  res: { clearCookie: () => undefined } as never,
};

describe("Telegram URL validation", () => {
  it("blocks shorthand links in the client and accepts complete HTTPS Telegram links", () => {
    expect(validateTelegramHttpsUrl("t.me/NEXALORD")).toContain("complete HTTPS link");
    expect(validateTelegramHttpsUrl("https://t.me/NEXALORD")).toBeNull();
  });

  it("returns a typed input error before malformed URLs reach the channel save service", async () => {
    const caller = appRouter.createCaller(activeAdminContext as never);
    await expect(caller.telegram.saveChannel({ channelUrl: "t.me/NEXALORD", enabled: true })).rejects.toMatchObject({ code: "BAD_REQUEST" });
  });
});
