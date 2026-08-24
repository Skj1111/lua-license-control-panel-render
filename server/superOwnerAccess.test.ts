import { describe, expect, it } from "vitest";
import { isActiveSuperOwner } from "./_core/trpc";

describe("active Super Owner authorization", () => {
  it("permits the database-authoritative active Super Owner role without a redundant configured-owner ID comparison", () => {
    expect(isActiveSuperOwner({ role: "super_owner", accessStatus: "active" })).toBe(true);
  });

  it("continues to deny pending, revoked, owner, and admin accounts", () => {
    expect(isActiveSuperOwner({ role: "super_owner", accessStatus: "pending" })).toBe(false);
    expect(isActiveSuperOwner({ role: "super_owner", accessStatus: "revoked" })).toBe(false);
    expect(isActiveSuperOwner({ role: "owner", accessStatus: "active" })).toBe(false);
    expect(isActiveSuperOwner({ role: "admin", accessStatus: "active" })).toBe(false);
  });
});
