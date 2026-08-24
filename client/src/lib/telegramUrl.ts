export function validateTelegramHttpsUrl(value: string): string | null {
  const raw = value.trim();
  if (!raw) return "Enter a Telegram HTTPS channel URL";
  if (raw.length > 256) return "Telegram channel URL must be 256 characters or fewer";

  let url: URL;
  try {
    url = new URL(raw);
  } catch {
    return "Use a complete HTTPS link, for example https://t.me/channel";
  }

  if (url.protocol !== "https:" || !["t.me", "telegram.me"].includes(url.hostname.toLowerCase())) {
    return "Only HTTPS Telegram links from t.me or telegram.me are allowed";
  }
  if (url.search || url.hash) return "Telegram link must not contain a query or fragment";
  if (!url.pathname || url.pathname === "/") return "Telegram link must include a channel or invite path";
  return null;
}
