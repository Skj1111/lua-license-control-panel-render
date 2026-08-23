// @vitest-environment jsdom
import { cleanup, render, screen } from "@testing-library/react";
import { createElement } from "react";
import { afterEach, describe, expect, it, vi } from "vitest";

vi.mock("@/components/TelegramExtensionCard", () => ({ TelegramExtensionCard: () => createElement("div", null, "TELEGRAM EXTENSION" ) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

vi.mock("@/lib/trpc", () => {
  const ownerIdentity = { role: "super_owner" as const, accessStatus: "active" as const };
  const query = (data: unknown) => () => ({ data, isLoading: false, error: null, refetch: vi.fn() });
  const mutation = () => () => ({ mutate: vi.fn(), isPending: false });
  return {
    trpc: {
      useUtils: () => ({
        superOwner: { seasonCounts: { invalidate: vi.fn() }, allKeysOverview: { invalidate: vi.fn() }, referralOverview: { invalidate: vi.fn() } },
        dashboard: { homeKeys: { invalidate: vi.fn() }, overview: { invalidate: vi.fn() }, audit: { invalidate: vi.fn() } },
        referrals: { list: { invalidate: vi.fn() } },
      }),
      dashboard: { identity: { useQuery: query(ownerIdentity) } },
      superOwner: {
        maintenance: { useQuery: query({ enabled: false, title: "SERVER MAINTENANCE", message: "", startAt: null, endAt: null }) },
        updateMaintenance: { useMutation: mutation() },
        announcement: { useQuery: query({ enabled: true, title: "SYSTEM ANNOUNCEMENT", message: "Authorised presentation message", startAt: null, endAt: null, displayMode: "every_login", textSize: "normal" }) },
        updateAnnouncement: { useMutation: mutation() },
        seasonCounts: { useQuery: query({ keys: 0, referrals: 0 }) },
        referralOverview: { useQuery: query([]) },
        allKeysOverview: { useQuery: query([]) },
        deleteAllKeys: { useMutation: mutation() },
        deleteAllReferrals: { useMutation: mutation() },
      },
    },
  };
});

import AnnouncementControl from "../client/src/pages/AnnouncementControl";
import SuperOwnerOverview from "../client/src/pages/SuperOwnerOverview";
import SystemControl from "../client/src/pages/SystemControl";

afterEach(cleanup);

describe("authenticated Super Owner direct routes", () => {
  it("renders the maintenance control only after an active Super Owner identity is available", () => {
    render(createElement(SystemControl));
    expect(screen.getByText("SYSTEM CONTROL")).toBeTruthy();
    expect(screen.getByText("Server maintenance")).toBeTruthy();
    expect(screen.getByRole("button", { name: /save maintenance/i })).toBeTruthy();
  });

  it("renders announcement and isolated Telegram controls for the active Super Owner", () => {
    render(createElement(AnnouncementControl));
    expect(screen.getByText("ANNOUNCEMENT CONTROL")).toBeTruthy();
    expect(screen.getByText("System announcement")).toBeTruthy();
    expect(screen.getByText("TELEGRAM EXTENSION")).toBeTruthy();
    expect(screen.getByRole("button", { name: /save announcement/i })).toBeTruthy();
  });

  it("renders the global overview for the active Super Owner", () => {
    render(createElement(SuperOwnerOverview));
    expect(screen.getByText("GLOBAL OVERVIEW")).toBeTruthy();
    expect(screen.getByText(/season visibility/i)).toBeTruthy();
    expect(screen.getByRole("button", { name: /delete all keys/i })).toBeTruthy();
  });
});
