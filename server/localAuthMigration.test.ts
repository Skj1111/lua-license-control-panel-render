import { describe, expect, it } from "vitest";
import { readFileSync, existsSync } from "node:fs";
import { resolve } from "node:path";

const projectRoot = resolve(import.meta.dirname, "..");
const read = (relativePath: string) => readFileSync(resolve(projectRoot, relativePath), "utf8");

describe("local authentication migration", () => {
  it("keeps the frontend on local credentials without OAuth redirects or preview tokens", () => {
    const main = read("client/src/main.tsx");
    const auth = read("client/src/_core/hooks/useAuth.ts");
    const layout = read("client/src/components/DashboardLayout.tsx");

    for (const source of [main, auth, layout]) {
      expect(source).not.toContain("startLogin");
      expect(source).not.toContain("oauth/start");
      expect(source).not.toContain("manus-cookie");
    }
    expect(main).toContain('credentials: "include"');
    expect(auth).toContain("trpc.auth.login.useMutation");
    expect(layout).toContain("RETURN TO LOGIN");
  });

  it("has no runtime OAuth implementation and no deleted login helper", () => {
    expect(existsSync(resolve(projectRoot, "client/src/const.ts"))).toBe(false);
    expect(read("server/_core/oauth.ts")).toContain("local authentication is now primary");
    expect(read("server/_core/sdk.ts")).not.toContain("OAuthService");
    expect(read("server/_core/sdk.ts")).not.toContain("OAUTH_SERVER_URL");
    expect(read("server/_core/env.ts")).not.toContain("OAUTH_SERVER_URL");
  });
});
