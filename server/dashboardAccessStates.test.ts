import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

const root = process.cwd();
const readClient = (relativePath: string) => readFileSync(resolve(root, "client/src", relativePath), "utf8");

describe("dashboard access-state UI", () => {
  it("keeps management controls behind loading, failure, pending, and inactive access gates", () => {
    const home = readClient("pages/Home.tsx");
    expect(home).toContain("PERMISSION BUS // SYNCHRONIZING");
    expect(home).toContain("ACCESS CHECK FAILED");
    expect(home).toContain("REFERRAL GATE // PENDING");
    expect(home).toContain("MANAGEMENT ACCESS SUSPENDED");
    expect(home).toContain("if (!isActive) return");
  });

  it("renders a non-secret API diagnostics area for authorized owners", () => {
    const home = readClient("pages/Home.tsx");
    expect(home).toContain("API DIAGNOSTICS // READ ONLY");
    expect(home).toContain("POST {apiOrigin}/api/license/validate");
    expect(home).toContain("GET/POST {apiOrigin}/v1/transport-ping");
    expect(home).toContain("No API secret, license key, raw device identifier");
  });

  it("retains explicit denied-access surfaces for the privileged control pages", () => {
    for (const page of ["pages/SystemControl.tsx", "pages/AnnouncementControl.tsx", "pages/AllPanelKeys.tsx"]) {
      const source = readClient(page);
      expect(source).toContain("SUPER OWNER ONLY");
    }
  });

  it("renders an authorization-loading state before a Super Owner direct route can be denied", () => {
    for (const page of ["pages/SystemControl.tsx", "pages/AnnouncementControl.tsx", "pages/SuperOwnerOverview.tsx"]) {
      const source = readClient(page);
      expect(source).toContain("if (identity.isLoading)");
      expect(source).toContain("VERIFYING SUPER OWNER ACCESS");
      expect(source).toContain("ACCESS CHECK FAILED");
    }
  });

  it("provides visible client-side feedback before invalid license changes reach the server", () => {
    const home = readClient("pages/Home.tsx");
    const validators = readClient("lib/licenseFormValidation.ts");
    expect(home).toContain("FORM VALIDATION // ACTION REQUIRED");
    expect(home).toContain("if (!validation.isValid) return setGenerationError(validation.error);");
    expect(home).toContain("if (!validation.isValid) return setManageError(validation.error);");
    expect(validators).toContain("Enter a whole activation duration between 1 and 5,256,000.");
    expect(validators).toContain("Enter a whole device limit between 1 and 100.");
    expect(validators).toContain("Enter a valid expiry date and time.");
    expect(home).toContain("No license or device change has been sent to the server.");
  });
});
