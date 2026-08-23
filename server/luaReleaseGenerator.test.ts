import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

describe("independent Lua release generator", () => {
  it("requires an HTTPS panel origin and replaces the inherited reference origin only in generated release copies", () => {
    const generator = readFileSync(resolve(process.cwd(), "scripts/render-lua-release.mjs"), "utf8");
    expect(generator).toContain('const inheritedOrigin = "https://luapanel-8355ybgt.manus.space"');
    expect(generator).toContain('process.env.PANEL_ORIGIN ?? "https://YOUR-PANEL-DOMAIN"');
    expect(generator).toContain('PANEL_ORIGIN must be an HTTPS origin without a path');
    expect(generator).toContain('const releaseDirectory = resolve(projectRoot, "release")');
    expect(generator).toContain('for (const filename of ["BRPlayerCharacterBase-Merged.lua", "BRPlayerCharacterBase-Reusable.lua"])');
    expect(generator).toContain('const releasePath = resolve(releaseDirectory, filename)');
  });

  it("documents that release wrappers must use the new panel's own stable HTTPS domain", () => {
    const guide = readFileSync(resolve(process.cwd(), "docs/LUA_RELEASE.md"), "utf8");
    expect(guide).toContain("Do not use its inherited endpoint in an independent deployment.");
    expect(guide).toContain("PANEL_ORIGIN=https://panel.example.com node scripts/render-lua-release.mjs");
  });
});
