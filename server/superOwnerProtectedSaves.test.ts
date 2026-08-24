import { beforeEach, describe, expect, it, vi } from "vitest";
import { appRouter } from "./routers";
import * as license from "./license";
import * as maintenance from "./maintenance";
import * as telegram from "./telegram";

const activeSuperOwnerContext = {
  user: {
    id: 42,
    openId: "active-super-owner-with-stale-configured-id",
    name: "Owner",
    email: null,
    username: "owner",
    usernameChangedAt: null,
    loginMethod: "oauth",
    role: "super_owner" as const,
    accessStatus: "active" as const,
    accessExpiresAt: null,
    createdAt: new Date(),
    updatedAt: new Date(),
    lastSignedIn: new Date(),
  },
  req: {} as never,
  res: { clearCookie: vi.fn() } as never,
};

describe("Super Owner protected saves", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it("permits active Super Owner maintenance, announcement, and Telegram save mutations without a forbidden error", async () => {
    const announcementSave = vi.spyOn(license, "updateSystemAnnouncement").mockResolvedValue({} as never);
    const maintenanceSave = vi.spyOn(maintenance, "updateServerMaintenance").mockResolvedValue({} as never);
    const telegramSave = vi.spyOn(telegram, "updateTelegramSettings").mockResolvedValue({} as never);
    const caller = appRouter.createCaller(activeSuperOwnerContext as never);

    await expect(caller.superOwner.updateAnnouncement({
      enabled: true,
      title: "Notice",
      message: "Ready",
      startAt: null,
      endAt: null,
      displayMode: "every_login",
      textSize: "normal",
    })).resolves.toEqual({});

    await expect(caller.superOwner.updateMaintenance({
      enabled: true,
      title: "Maintenance",
      message: "Please wait",
      startAt: null,
      endAt: null,
    })).resolves.toEqual({});

    await expect(caller.telegram.updateSettings({ enabled: true })).resolves.toEqual({});

    expect(announcementSave).toHaveBeenCalledWith(expect.objectContaining({ title: "Notice" }), 42);
    expect(maintenanceSave).toHaveBeenCalledWith(expect.objectContaining({ title: "Maintenance" }), 42);
    expect(telegramSave).toHaveBeenCalledWith({ enabled: true }, 42);
  });
});
