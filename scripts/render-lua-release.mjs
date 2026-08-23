import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { resolve } from "node:path";

const projectRoot = resolve(import.meta.dirname, "..");
const inheritedOrigin = "https://luapanel-8355ybgt.manus.space";
const configuredOrigin = (process.env.PANEL_ORIGIN ?? "https://YOUR-PANEL-DOMAIN")
  .trim()
  .replace(/\/+$/, "");

if (!/^https:\/\/[A-Za-z0-9.-]+(?::\d+)?$/.test(configuredOrigin)) {
  throw new Error("PANEL_ORIGIN must be an HTTPS origin without a path, for example https://panel.example.com");
}

const releaseDirectory = resolve(projectRoot, "release");
mkdirSync(releaseDirectory, { recursive: true });

for (const filename of ["BRPlayerCharacterBase-Merged.lua", "BRPlayerCharacterBase-Reusable.lua"]) {
  const sourcePath = resolve(projectRoot, filename);
  const releasePath = resolve(releaseDirectory, filename);
  const source = readFileSync(sourcePath, "utf8");
  const rendered = source.split(inheritedOrigin).join(configuredOrigin);

  if (rendered.includes(inheritedOrigin)) {
    throw new Error(`Inherited endpoint remained in ${filename}`);
  }

  writeFileSync(releasePath, rendered, "utf8");
  console.log(`Rendered ${filename} for ${configuredOrigin}`);
}
