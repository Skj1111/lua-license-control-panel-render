// @vitest-environment jsdom
import { cleanup, fireEvent, render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createElement } from "react";
import { afterEach, describe, expect, it, vi } from "vitest";

const mutationSpies = vi.hoisted(() => ({ create: vi.fn(), update: vi.fn() }));

vi.mock("@/_core/hooks/useAuth", () => ({
  useAuth: () => ({ user: { email: "owner@example.test", openId: "owner-open-id" }, logout: vi.fn() }),
}));

vi.mock("@/components/TelegramExtensionCard", () => ({ TelegramExtensionCard: () => null }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn(), info: vi.fn() } }));

vi.mock("@/lib/trpc", () => {
  const key = {
    id: 41,
    keyValue: "NEXA-TEST-KEY",
    status: "active",
    durationMinutes: 1440,
    activatedAt: null,
    expiresAt: null,
    maxDevices: 1,
    usedDevices: 0,
    boundDeviceId: null,
    createdAt: new Date("2026-08-23T00:00:00.000Z"),
  };
  const query = (data: unknown) => () => ({ data, isLoading: false, error: null, refetch: vi.fn() });
  const mutation = (mutate: ReturnType<typeof vi.fn> = vi.fn()) => () => ({ mutate, isPending: false });

  return {
    trpc: {
      useUtils: () => ({
        dashboard: { homeKeys: { invalidate: vi.fn() }, overview: { invalidate: vi.fn() }, bindings: { invalidate: vi.fn() }, users: { invalidate: vi.fn() }, audit: { invalidate: vi.fn() }, identity: { invalidate: vi.fn() } },
        referrals: { list: { invalidate: vi.fn() } },
      }),
      dashboard: {
        identity: { useQuery: query({ role: "super_owner", accessStatus: "active", username: "Nexa", usernameChangeAvailable: true, usernameChangeAvailableAt: null, email: "owner@example.test" }) },
        overview: { useQuery: query({ totalKeys: 1, activeKeys: 1, revokedKeys: 0, totalDeviceBindings: 0 }) },
        homeKeys: { useQuery: query([key]) },
        users: { useQuery: query([]) },
        audit: { useQuery: query([]) },
        bindings: { useQuery: query([]) },
      },
      referrals: { list: { useQuery: query([]) }, create: { useMutation: mutation() }, revoke: { useMutation: mutation() }, delete: { useMutation: mutation() }, redeem: { useMutation: mutation() } },
      license: {
        create: { useMutation: mutation(mutationSpies.create) },
        setStatus: { useMutation: mutation() },
        update: { useMutation: mutation(mutationSpies.update) },
        delete: { useMutation: mutation() },
        unbind: { useMutation: mutation() },
      },
      users: { setRole: { useMutation: mutation() }, delete: { useMutation: mutation() }, removeOwner: { useMutation: mutation() }, changeUsername: { useMutation: mutation() } },
    },
  };
});

import Home from "../client/src/pages/Home";

afterEach(() => {
  cleanup();
  mutationSpies.create.mockReset();
  mutationSpies.update.mockReset();
});

describe("Home license form submission guards", () => {
  it("shows a validation alert and blocks the create mutation for an invalid device limit", async () => {
    const user = userEvent.setup();
    render(createElement(Home));

    const deviceLimit = screen.getAllByRole("spinbutton")[1];
    await user.clear(deviceLimit);
    await user.type(deviceLimit, "0");
    await user.click(screen.getByRole("button", { name: /generate license/i }));

    expect(screen.getByText("FORM VALIDATION // ACTION REQUIRED")).toBeTruthy();
    expect(screen.getByText("Enter a whole device limit between 1 and 100.")).toBeTruthy();
    expect(mutationSpies.create).not.toHaveBeenCalled();
  });

  it("shows a validation alert and blocks the update mutation for an invalid managed device limit", async () => {
    const user = userEvent.setup();
    render(createElement(Home));

    await user.click(screen.getByLabelText("Manage NEXA-TEST-KEY"));
    const dialog = screen.getByRole("dialog");
    const deviceLimit = within(dialog).getByRole("spinbutton");
    fireEvent.change(deviceLimit, { target: { value: "0" } });
    await user.click(within(dialog).getByRole("button", { name: /save license/i }));

    expect(screen.getByText("FORM VALIDATION // ACTION REQUIRED")).toBeTruthy();
    expect(screen.getByText("Enter a whole device limit between 1 and 100.")).toBeTruthy();
    expect(mutationSpies.update).not.toHaveBeenCalled();
  });
});
