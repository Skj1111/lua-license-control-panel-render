import { boolean, integer, pgEnum, pgTable, text, timestamp, varchar, serial } from "drizzle-orm/pg-core";

const userRole = pgEnum("user_role", ["super_owner", "owner", "admin"]);
const accessStatus = pgEnum("access_status", ["pending", "active", "revoked"]);
const licenseStatus = pgEnum("license_status", ["active", "expired", "revoked", "unused"]);
const legacyLicenseStatus = pgEnum("legacy_license_status", ["active", "banned"]);
const referralRole = pgEnum("referral_role", ["owner", "admin"]);
const referralStatus = pgEnum("referral_status", ["active", "redeemed", "revoked"]);
const announcementDisplayMode = pgEnum("announcement_display_mode", ["every_login", "once_per_day"]);
const announcementTextSize = pgEnum("announcement_text_size", ["small", "normal", "large", "festival_large"]);
const telegramScopeStatus = pgEnum("telegram_scope_status", ["active", "review"]);

export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  openId: varchar("openId", { length: 64 }).notNull().unique(),
  name: text("name"),
  email: varchar("email", { length: 320 }),
  username: varchar("username", { length: 32 }).unique(),
  passwordHash: varchar("passwordHash", { length: 256 }),
  usernameChangedAt: timestamp("usernameChangedAt"),
  loginMethod: varchar("loginMethod", { length: 64 }),
  role: userRole("role").default("admin").notNull(),
  accessStatus: accessStatus("accessStatus").default("pending").notNull(),
  accessExpiresAt: timestamp("accessExpiresAt"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().notNull(),
  lastSignedIn: timestamp("lastSignedIn").defaultNow().notNull(),
});

/**
 * Canonical server-authoritative license registry.
 * `boundDeviceId` stores a keyed SHA-256 hash, never the raw client device ID.
 */
export const licenses = pgTable("licenses", {
  id: serial("id").primaryKey(),
  keyValue: varchar("license_key", { length: 128 }).notNull().unique(),
  status: licenseStatus("status").default("unused").notNull(),
  // Existing keys retain their stored expiry. Newly created activation-duration keys
  // receive an expiry only after their first successful device binding.
  expiresAt: timestamp("expires_at"),
  activatedAt: timestamp("activated_at"),
  durationMinutes: integer("duration_minutes"),
  maxDevices: integer("max_devices").notNull().default(1),
  boundDeviceId: varchar("bound_device_id", { length: 128 }),
  createdByUserId: integer("created_by_user_id"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

/**
 * Legacy registry retained only for a non-destructive, one-time data migration.
 * Application code reads and writes exclusively through `licenses`.
 */
export const legacyLicenseKeys = pgTable("license_keys", {
  id: serial("id").primaryKey(),
  keyValue: varchar("keyValue", { length: 128 }).notNull().unique(),
  status: legacyLicenseStatus("status").default("active").notNull(),
  expiresAt: timestamp("expiresAt").notNull(),
  maxDevices: integer("maxDevices").notNull().default(1),
  createdByUserId: integer("createdByUserId"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export const referrals = pgTable("referrals", {
  id: serial("id").primaryKey(),
  code: varchar("code", { length: 96 }).notNull().unique(),
  role: referralRole("role").notNull(),
  createdByUserId: integer("createdByUserId").notNull(),
  redeemedByUserId: integer("redeemedByUserId"),
  status: referralStatus("status").default("active").notNull(),
  expiresAt: timestamp("expiresAt").notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  redeemedAt: timestamp("redeemedAt"),
});

export const auditLogs = pgTable("audit_logs", {
  id: serial("id").primaryKey(),
  actorUserId: integer("actorUserId"),
  action: varchar("action", { length: 64 }).notNull(),
  entityType: varchar("entityType", { length: 32 }).notNull(),
  entityId: integer("entityId"),
  keyPreview: varchar("keyPreview", { length: 64 }),
  details: text("details"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

/**
 * Server-authoritative list of distinct hashed device bindings. `boundDeviceId` remains
 * the first bound device for backwards compatibility; this table enforces maxDevices.
 */
export const deviceBindings = pgTable("device_bindings", {
  id: serial("id").primaryKey(),
  licenseKeyId: integer("licenseKeyId").notNull(),
  deviceHash: varchar("deviceHash", { length: 128 }).notNull().unique(),
  firstSeenAt: timestamp("firstSeenAt").defaultNow().notNull(),
  lastSeenAt: timestamp("lastSeenAt").defaultNow().notNull(),
});

export type User = typeof users.$inferSelect;
export type InsertUser = typeof users.$inferInsert;
export type License = typeof licenses.$inferSelect;
export type DeviceBinding = typeof deviceBindings.$inferSelect;

/**
 * Singleton, presentation-only announcement controlled by the Super Owner.
 * `id = 1` is the only supported row; no license decision reads this table.
 */
export const systemAnnouncements = pgTable("system_announcements", {
  id: integer("id").primaryKey(),
  version: integer("version").notNull().default(1),
  enabled: boolean("enabled").notNull().default(false),
  title: varchar("title", { length: 80 }).notNull().default("SYSTEM ANNOUNCEMENT"),
  message: varchar("message", { length: 300 }).notNull().default(""),
  startAt: timestamp("start_at"),
  endAt: timestamp("end_at"),
  displayMode: announcementDisplayMode("display_mode").notNull().default("every_login"),
  textSize: announcementTextSize("text_size").notNull().default("normal"),
  updatedByUserId: integer("updated_by_user_id"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export type SystemAnnouncement = typeof systemAnnouncements.$inferSelect;

/**
 * Singleton operational hold controlled only by the existing Super Owner gate.
 * This state is intentionally separate from System Announcement and never participates
 * in license validation or device-binding decisions.
 */
export const serverMaintenance = pgTable("server_maintenance", {
  id: integer("id").primaryKey(),
  version: integer("version").notNull().default(1),
  enabled: boolean("enabled").notNull().default(false),
  title: varchar("title", { length: 80 }).notNull().default("SERVER MAINTENANCE"),
  message: varchar("message", { length: 300 }).notNull().default(""),
  startAt: timestamp("start_at"),
  endAt: timestamp("end_at"),
  updatedByUserId: integer("updated_by_user_id"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export type ServerMaintenance = typeof serverMaintenance.$inferSelect;

/**
 * Fully isolated, removable Telegram extension tables.
 * These tables are presentation/navigation metadata only and are never read by license validation.
 */
export const telegramSettings = pgTable("telegram_settings", {
  id: integer("id").primaryKey(),
  enabled: boolean("enabled").notNull().default(false),
  updatedByUserId: integer("updated_by_user_id"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const telegramChannels = pgTable("telegram_channels", {
  id: serial("id").primaryKey(),
  userId: integer("user_id").notNull().unique(),
  channelUrl: varchar("channel_url", { length: 256 }).notNull(),
  enabled: boolean("enabled").notNull().default(true),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const telegramAccountScopes = pgTable("telegram_account_scopes", {
  id: serial("id").primaryKey(),
  ownerUserId: integer("owner_user_id").notNull(),
  accountUserId: integer("account_user_id").notNull().unique(),
  sourceReferralId: integer("source_referral_id"),
  status: telegramScopeStatus("status").notNull().default("active"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const telegramKeyMappings = pgTable("telegram_key_mappings", {
  id: serial("id").primaryKey(),
  licenseId: integer("license_id").notNull().unique(),
  accountUserId: integer("account_user_id").notNull(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const telegramTickets = pgTable("telegram_tickets", {
  id: serial("id").primaryKey(),
  tokenHash: varchar("token_hash", { length: 128 }).notNull().unique(),
  licenseId: integer("license_id").notNull(),
  deviceHash: varchar("device_hash", { length: 128 }).notNull(),
  expiresAt: timestamp("expires_at").notNull(),
  redeemedAt: timestamp("redeemed_at"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export type TelegramChannel = typeof telegramChannels.$inferSelect;
export type TelegramAccountScope = typeof telegramAccountScopes.$inferSelect;
export type TelegramKeyMapping = typeof telegramKeyMappings.$inferSelect;
export type TelegramSettings = typeof telegramSettings.$inferSelect;
export type TelegramTicket = typeof telegramTickets.$inferSelect;
