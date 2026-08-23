import { describe, expect, it } from "vitest";
import { validateLicenseGenerationInput, validateLicenseManagementInput } from "../client/src/lib/licenseFormValidation";

describe("license management form validation", () => {
  it("blocks invalid generation data before a create mutation can receive a payload", () => {
    const invalidDuration = validateLicenseGenerationInput({ durationAmount: "0", maxDevices: "1", customEnabled: false, customPrefix: "" });
    const invalidDevices = validateLicenseGenerationInput({ durationAmount: "30", maxDevices: "101", customEnabled: false, customPrefix: "" });
    const missingCustomName = validateLicenseGenerationInput({ durationAmount: "30", maxDevices: "1", customEnabled: true, customPrefix: "  " });

    expect(invalidDuration).toEqual({ isValid: false, error: "Enter a whole activation duration between 1 and 5,256,000." });
    expect(invalidDevices).toEqual({ isValid: false, error: "Enter a whole device limit between 1 and 100." });
    expect(missingCustomName).toEqual({ isValid: false, error: "Enter a custom key name or disable custom key name." });
  });

  it("blocks invalid management updates and only returns a mutation-ready payload when valid", () => {
    const invalidLimit = validateLicenseManagementInput({ maxDevices: "0", requiresExpiry: false, expiry: "" });
    const invalidExpiry = validateLicenseManagementInput({ maxDevices: "2", requiresExpiry: true, expiry: "not-a-date" });
    const validFixedExpiry = validateLicenseManagementInput({ maxDevices: "2", requiresExpiry: true, expiry: "2030-01-15T10:30" });
    const validDurationBased = validateLicenseManagementInput({ maxDevices: "3", requiresExpiry: false, expiry: "" });

    expect(invalidLimit).toEqual({ isValid: false, error: "Enter a whole device limit between 1 and 100." });
    expect(invalidExpiry).toEqual({ isValid: false, error: "Enter a valid expiry date and time." });
    expect(validFixedExpiry).toMatchObject({ isValid: true, value: { maxDevices: 2, expiresAt: expect.any(Date) } });
    expect(validDurationBased).toEqual({ isValid: true, value: { maxDevices: 3 } });
  });
});
