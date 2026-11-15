import crypto from "node:crypto";

export const LOCAL_USERNAME_PATTERN = /^[A-Za-z0-9](?:[A-Za-z0-9_.-]{1,30}[A-Za-z0-9])?$/;
export const LOCAL_PASSWORD_MIN_LENGTH = 8;

const SCRYPT_KEY_LENGTH = 64;
const SCRYPT_COST = 16_384;
const SCRYPT_BLOCK_SIZE = 8;
const SCRYPT_PARALLELIZATION = 1;
const failedAttempts = new Map<string, { count: number; resetAt: number }>();

export function normalizeLocalUsername(value: string) {
  return value.trim().toLowerCase();
}

export function hashPassword(password: string) {
  const salt = crypto.randomBytes(16).toString("hex");
  const derivedKey = crypto.scryptSync(password, salt, SCRYPT_KEY_LENGTH, {
    N: SCRYPT_COST,
    r: SCRYPT_BLOCK_SIZE,
    p: SCRYPT_PARALLELIZATION,
    maxmem: 32 * 1024 * 1024,
  });
  return `scrypt$${salt}$${derivedKey.toString("hex")}`;
}

export function verifyPassword(password: string, encodedHash: string | null | undefined) {
  if (!encodedHash) return false;
  const [algorithm, salt, digest] = encodedHash.split("$");
  if (algorithm !== "scrypt" || !salt || !digest || !/^[a-f0-9]+$/i.test(digest)) return false;

  try {
    const expected = Buffer.from(digest, "hex");
    const actual = crypto.scryptSync(password, salt, expected.length, {
      N: SCRYPT_COST,
      r: SCRYPT_BLOCK_SIZE,
      p: SCRYPT_PARALLELIZATION,
      maxmem: 32 * 1024 * 1024,
    });
    return expected.length === actual.length && crypto.timingSafeEqual(expected, actual);
  } catch {
    return false;
  }
}

export function getLoginRateLimitKey(username: string, ip: string | undefined) {
  return `${normalizeLocalUsername(username)}|${ip || "unknown"}`;
}

export function isLoginRateLimited(key: string, now = Date.now()) {
  const state = failedAttempts.get(key);
  if (!state) return false;
  if (state.resetAt <= now) {
    failedAttempts.delete(key);
    return false;
  }
  return state.count >= 5;
}

export function recordFailedLogin(key: string, now = Date.now()) {
  const current = failedAttempts.get(key);
  if (!current || current.resetAt <= now) {
    failedAttempts.set(key, { count: 1, resetAt: now + 15 * 60 * 1000 });
    return;
  }
  current.count += 1;
}

export function clearFailedLogins(key: string) {
  failedAttempts.delete(key);
}
