CREATE TYPE "public"."access_status" AS ENUM('pending', 'active', 'revoked');--> statement-breakpoint
CREATE TYPE "public"."announcement_display_mode" AS ENUM('every_login', 'once_per_day');--> statement-breakpoint
CREATE TYPE "public"."announcement_text_size" AS ENUM('small', 'normal', 'large', 'festival_large');--> statement-breakpoint
CREATE TYPE "public"."legacy_license_status" AS ENUM('active', 'banned');--> statement-breakpoint
CREATE TYPE "public"."license_status" AS ENUM('active', 'expired', 'revoked', 'unused');--> statement-breakpoint
CREATE TYPE "public"."referral_role" AS ENUM('owner', 'admin');--> statement-breakpoint
CREATE TYPE "public"."referral_status" AS ENUM('active', 'redeemed', 'revoked');--> statement-breakpoint
CREATE TYPE "public"."telegram_scope_status" AS ENUM('active', 'review');--> statement-breakpoint
CREATE TYPE "public"."user_role" AS ENUM('super_owner', 'owner', 'admin');--> statement-breakpoint
CREATE TABLE "audit_logs" (
	"id" serial PRIMARY KEY NOT NULL,
	"actorUserId" integer,
	"action" varchar(64) NOT NULL,
	"entityType" varchar(32) NOT NULL,
	"entityId" integer,
	"keyPreview" varchar(64),
	"details" text,
	"createdAt" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "device_bindings" (
	"id" serial PRIMARY KEY NOT NULL,
	"licenseKeyId" integer NOT NULL,
	"deviceHash" varchar(128) NOT NULL,
	"firstSeenAt" timestamp DEFAULT now() NOT NULL,
	"lastSeenAt" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "device_bindings_deviceHash_unique" UNIQUE("deviceHash")
);
--> statement-breakpoint
CREATE TABLE "license_keys" (
	"id" serial PRIMARY KEY NOT NULL,
	"keyValue" varchar(128) NOT NULL,
	"status" "legacy_license_status" DEFAULT 'active' NOT NULL,
	"expiresAt" timestamp NOT NULL,
	"maxDevices" integer DEFAULT 1 NOT NULL,
	"createdByUserId" integer,
	"createdAt" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "license_keys_keyValue_unique" UNIQUE("keyValue")
);
--> statement-breakpoint
CREATE TABLE "licenses" (
	"id" serial PRIMARY KEY NOT NULL,
	"license_key" varchar(128) NOT NULL,
	"status" "license_status" DEFAULT 'unused' NOT NULL,
	"expires_at" timestamp,
	"activated_at" timestamp,
	"duration_minutes" integer,
	"max_devices" integer DEFAULT 1 NOT NULL,
	"bound_device_id" varchar(128),
	"created_by_user_id" integer,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "licenses_license_key_unique" UNIQUE("license_key")
);
--> statement-breakpoint
CREATE TABLE "referrals" (
	"id" serial PRIMARY KEY NOT NULL,
	"code" varchar(96) NOT NULL,
	"role" "referral_role" NOT NULL,
	"createdByUserId" integer NOT NULL,
	"redeemedByUserId" integer,
	"status" "referral_status" DEFAULT 'active' NOT NULL,
	"expiresAt" timestamp NOT NULL,
	"createdAt" timestamp DEFAULT now() NOT NULL,
	"redeemedAt" timestamp,
	CONSTRAINT "referrals_code_unique" UNIQUE("code")
);
--> statement-breakpoint
CREATE TABLE "server_maintenance" (
	"id" integer PRIMARY KEY NOT NULL,
	"version" integer DEFAULT 1 NOT NULL,
	"enabled" boolean DEFAULT false NOT NULL,
	"title" varchar(80) DEFAULT 'SERVER MAINTENANCE' NOT NULL,
	"message" varchar(300) DEFAULT '' NOT NULL,
	"start_at" timestamp,
	"end_at" timestamp,
	"updated_by_user_id" integer,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "system_announcements" (
	"id" integer PRIMARY KEY NOT NULL,
	"version" integer DEFAULT 1 NOT NULL,
	"enabled" boolean DEFAULT false NOT NULL,
	"title" varchar(80) DEFAULT 'SYSTEM ANNOUNCEMENT' NOT NULL,
	"message" varchar(300) DEFAULT '' NOT NULL,
	"start_at" timestamp,
	"end_at" timestamp,
	"display_mode" "announcement_display_mode" DEFAULT 'every_login' NOT NULL,
	"text_size" "announcement_text_size" DEFAULT 'normal' NOT NULL,
	"updated_by_user_id" integer,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "telegram_account_scopes" (
	"id" serial PRIMARY KEY NOT NULL,
	"owner_user_id" integer NOT NULL,
	"account_user_id" integer NOT NULL,
	"source_referral_id" integer,
	"status" "telegram_scope_status" DEFAULT 'active' NOT NULL,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "telegram_account_scopes_account_user_id_unique" UNIQUE("account_user_id")
);
--> statement-breakpoint
CREATE TABLE "telegram_channels" (
	"id" serial PRIMARY KEY NOT NULL,
	"user_id" integer NOT NULL,
	"channel_url" varchar(256) NOT NULL,
	"enabled" boolean DEFAULT true NOT NULL,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "telegram_channels_user_id_unique" UNIQUE("user_id")
);
--> statement-breakpoint
CREATE TABLE "telegram_key_mappings" (
	"id" serial PRIMARY KEY NOT NULL,
	"license_id" integer NOT NULL,
	"account_user_id" integer NOT NULL,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "telegram_key_mappings_license_id_unique" UNIQUE("license_id")
);
--> statement-breakpoint
CREATE TABLE "telegram_settings" (
	"id" integer PRIMARY KEY NOT NULL,
	"enabled" boolean DEFAULT false NOT NULL,
	"updated_by_user_id" integer,
	"created_at" timestamp DEFAULT now() NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "telegram_tickets" (
	"id" serial PRIMARY KEY NOT NULL,
	"token_hash" varchar(128) NOT NULL,
	"license_id" integer NOT NULL,
	"device_hash" varchar(128) NOT NULL,
	"expires_at" timestamp NOT NULL,
	"redeemed_at" timestamp,
	"created_at" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "telegram_tickets_token_hash_unique" UNIQUE("token_hash")
);
--> statement-breakpoint
CREATE TABLE "users" (
	"id" serial PRIMARY KEY NOT NULL,
	"openId" varchar(64) NOT NULL,
	"name" text,
	"email" varchar(320),
	"username" varchar(32),
	"passwordHash" varchar(256),
	"usernameChangedAt" timestamp,
	"loginMethod" varchar(64),
	"role" "user_role" DEFAULT 'admin' NOT NULL,
	"accessStatus" "access_status" DEFAULT 'pending' NOT NULL,
	"accessExpiresAt" timestamp,
	"createdAt" timestamp DEFAULT now() NOT NULL,
	"updatedAt" timestamp DEFAULT now() NOT NULL,
	"lastSignedIn" timestamp DEFAULT now() NOT NULL,
	CONSTRAINT "users_openId_unique" UNIQUE("openId"),
	CONSTRAINT "users_username_unique" UNIQUE("username")
);
