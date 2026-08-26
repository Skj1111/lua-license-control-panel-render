import fs from 'node:fs';

const filePath = process.argv[2];
if (!filePath) {
  console.error('Usage: node scripts/verify-backup-lua-esp-type7-colors.mjs <lua-file>');
  process.exit(2);
}

const source = fs.readFileSync(filePath, 'utf8');
const required = [
  'local C_ESP7_BOT_TEXT = {R=0, G=255, B=255, A=255}',
  'local C_ESP7_PLAYER_TEXT = {R=255, G=220, B=0, A=255}',
  'local textColor = isBot and C_ESP7_BOT_TEXT or C_ESP7_PLAYER_TEXT',
  'MyHUD:AddDebugText(stateText, enemy',
];

const missing = required.filter((marker) => !source.includes(marker));
if (missing.length) {
  console.error(`FAIL: missing Type 7 color markers:\n${missing.join('\n')}`);
  process.exit(1);
}

if (source.includes('local textColor = isBot and C_CYAN or C_YELLOW')) {
  console.error('FAIL: Type 7 renderer still uses the legacy transparent shared colors');
  process.exit(1);
}

console.log('PASS: ESP Type 7 uses opaque dedicated Bot and Player text colors');
