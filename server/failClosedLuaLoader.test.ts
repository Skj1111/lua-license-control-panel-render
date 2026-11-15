import { execFileSync } from "node:child_process";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";

const projectRoot = resolve(import.meta.dirname, "..");
const verifyScript = resolve(projectRoot, "scripts/verify-fail-closed-license-loader.mjs");

describe("fail-closed Lua authorization loader", () => {
  for (const filename of ["BRPlayerCharacterBase-Merged.lua", "BRPlayerCharacterBase-Reusable.lua"]) {
    it(`keeps ${filename} behind canonical validation`, () => {
      const target = resolve(projectRoot, filename);
      const result = execFileSync(process.execPath, [verifyScript, target], { cwd: projectRoot, encoding: "utf8" });
      expect(result).toContain("FAIL_CLOSED_LOADER_CHECK=PASS");
      execFileSync("luac", ["-p", target], { cwd: projectRoot, stdio: "pipe" });
    });
  }
});
