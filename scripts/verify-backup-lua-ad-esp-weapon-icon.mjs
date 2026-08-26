import fs from 'node:fs';

const filePath = process.argv[2];
if (!filePath) {
  console.error('Usage: node scripts/verify-backup-lua-ad-esp-weapon-icon.mjs <lua-file>');
  process.exit(2);
}

const source = fs.readFileSync(filePath, 'utf8');
const required = [
  'weaponIconSlot:SetSize(FVector2D(50, 30))',
  'entry.weaponIconSlot:SetPosition(FVector2D((width - 50) * 0.5, -23))',
  'entry.weaponIcon:SetWidgetVisibility(UEnums.ESlateVisibility.SelfHitTestInvisible)',
];
const missing = required.filter((marker) => !source.includes(marker));
if (missing.length) {
  console.error(`FAIL: missing AD ESP weapon icon markers:\n${missing.join('\n')}`);
  process.exit(1);
}
if (source.includes('weaponIconSlot:SetSize(FVector2D(30, 20))')) {
  console.error('FAIL: legacy 30×20 weapon icon size remains');
  process.exit(1);
}
console.log('PASS: backup AD ESP weapon icon is 50×30 and remains centered');
