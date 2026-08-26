import fs from 'node:fs';

const filePath = process.argv[2];
if (!filePath) {
  console.error('Usage: node scripts/verify-backup-lua-ad-esp-counter-distance.mjs <lua-file>');
  process.exit(2);
}

const source = fs.readFileSync(filePath, 'utf8');
const required = [
  'local ADESP_UPDATE_INTERVAL = 0.05',
  'math.floor(nearestDist / 10) / 10',
  'Dist: %.1fm',
  'local textChanged = newText ~= _G.ADESP_LastCounterText',
  'SetCounterText(widget, newText)',
];
const missing = required.filter((marker) => !source.includes(marker));
if (missing.length) {
  console.error(`FAIL: missing counter distance refresh markers:\n${missing.join('\n')}`);
  process.exit(1);
}
if (source.includes('math.floor(nearestDist / 100)')) {
  console.error('FAIL: legacy whole-metre-only counter distance conversion remains');
  process.exit(1);
}
console.log('PASS: backup AD ESP counter refreshes nearest distance at 0.1m precision');
