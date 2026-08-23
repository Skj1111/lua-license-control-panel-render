CREATE TABLE `audit_logs` (
	`id` int AUTO_INCREMENT NOT NULL,
	`actorUserId` int,
	`action` varchar(64) NOT NULL,
	`entityType` varchar(32) NOT NULL,
	`entityId` int,
	`keyPreview` varchar(64),
	`details` text,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	CONSTRAINT `audit_logs_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `device_bindings` (
	`id` int AUTO_INCREMENT NOT NULL,
	`licenseKeyId` int NOT NULL,
	`deviceHash` varchar(128) NOT NULL,
	`firstSeenAt` timestamp NOT NULL DEFAULT (now()),
	`lastSeenAt` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `device_bindings_id` PRIMARY KEY(`id`),
	CONSTRAINT `device_bindings_deviceHash_unique` UNIQUE(`deviceHash`)
);
--> statement-breakpoint
CREATE TABLE `license_keys` (
	`id` int AUTO_INCREMENT NOT NULL,
	`keyValue` varchar(128) NOT NULL,
	`status` enum('active','banned') NOT NULL DEFAULT 'active',
	`expiresAt` timestamp NOT NULL,
	`maxDevices` int NOT NULL DEFAULT 1,
	`createdByUserId` int,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	CONSTRAINT `license_keys_id` PRIMARY KEY(`id`),
	CONSTRAINT `license_keys_keyValue_unique` UNIQUE(`keyValue`)
);
--> statement-breakpoint
CREATE TABLE `licenses` (
	`id` int AUTO_INCREMENT NOT NULL,
	`license_key` varchar(128) NOT NULL,
	`status` enum('active','expired','revoked','unused') NOT NULL DEFAULT 'unused',
	`expires_at` timestamp,
	`activated_at` timestamp,
	`duration_minutes` int,
	`max_devices` int NOT NULL DEFAULT 1,
	`bound_device_id` varchar(128),
	`created_by_user_id` int,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `licenses_id` PRIMARY KEY(`id`),
	CONSTRAINT `licenses_license_key_unique` UNIQUE(`license_key`)
);
--> statement-breakpoint
CREATE TABLE `referrals` (
	`id` int AUTO_INCREMENT NOT NULL,
	`code` varchar(96) NOT NULL,
	`role` enum('owner','admin') NOT NULL,
	`createdByUserId` int NOT NULL,
	`redeemedByUserId` int,
	`status` enum('active','redeemed','revoked') NOT NULL DEFAULT 'active',
	`expiresAt` timestamp NOT NULL,
	`createdAt` timestamp NOT NULL DEFAULT (now()),
	`redeemedAt` timestamp,
	CONSTRAINT `referrals_id` PRIMARY KEY(`id`),
	CONSTRAINT `referrals_code_unique` UNIQUE(`code`)
);
--> statement-breakpoint
CREATE TABLE `server_maintenance` (
	`id` int NOT NULL,
	`version` int NOT NULL DEFAULT 1,
	`enabled` boolean NOT NULL DEFAULT false,
	`title` varchar(80) NOT NULL DEFAULT 'SERVER MAINTENANCE',
	`message` varchar(300) NOT NULL DEFAULT '',
	`start_at` timestamp,
	`end_at` timestamp,
	`updated_by_user_id` int,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `server_maintenance_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `system_announcements` (
	`id` int NOT NULL,
	`version` int NOT NULL DEFAULT 1,
	`enabled` boolean NOT NULL DEFAULT false,
	`title` varchar(80) NOT NULL DEFAULT 'SYSTEM ANNOUNCEMENT',
	`message` varchar(300) NOT NULL DEFAULT '',
	`start_at` timestamp,
	`end_at` timestamp,
	`display_mode` enum('every_login','once_per_day') NOT NULL DEFAULT 'every_login',
	`text_size` enum('small','normal','large','festival_large') NOT NULL DEFAULT 'normal',
	`updated_by_user_id` int,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `system_announcements_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `telegram_account_scopes` (
	`id` int AUTO_INCREMENT NOT NULL,
	`owner_user_id` int NOT NULL,
	`account_user_id` int NOT NULL,
	`source_referral_id` int,
	`status` enum('active','review') NOT NULL DEFAULT 'active',
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `telegram_account_scopes_id` PRIMARY KEY(`id`),
	CONSTRAINT `telegram_account_scopes_account_user_id_unique` UNIQUE(`account_user_id`)
);
--> statement-breakpoint
CREATE TABLE `telegram_channels` (
	`id` int AUTO_INCREMENT NOT NULL,
	`user_id` int NOT NULL,
	`channel_url` varchar(256) NOT NULL,
	`enabled` boolean NOT NULL DEFAULT true,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `telegram_channels_id` PRIMARY KEY(`id`),
	CONSTRAINT `telegram_channels_user_id_unique` UNIQUE(`user_id`)
);
--> statement-breakpoint
CREATE TABLE `telegram_key_mappings` (
	`id` int AUTO_INCREMENT NOT NULL,
	`license_id` int NOT NULL,
	`account_user_id` int NOT NULL,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `telegram_key_mappings_id` PRIMARY KEY(`id`),
	CONSTRAINT `telegram_key_mappings_license_id_unique` UNIQUE(`license_id`)
);
--> statement-breakpoint
CREATE TABLE `telegram_settings` (
	`id` int NOT NULL,
	`enabled` boolean NOT NULL DEFAULT false,
	`updated_by_user_id` int,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	`updated_at` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `telegram_settings_id` PRIMARY KEY(`id`)
);
--> statement-breakpoint
CREATE TABLE `telegram_tickets` (
	`id` int AUTO_INCREMENT NOT NULL,
	`token_hash` varchar(128) NOT NULL,
	`license_id` int NOT NULL,
	`device_hash` varchar(128) NOT NULL,
	`expires_at` timestamp NOT NULL,
	`redeemed_at` timestamp,
	`created_at` timestamp NOT NULL DEFAULT (now()),
	CONSTRAINT `telegram_tickets_id` PRIMARY KEY(`id`),
	CONSTRAINT `telegram_tickets_token_hash_unique` UNIQUE(`token_hash`)
);
--> statement-breakpoint
ALTER TABLE `users` ADD COLUMN `username` varchar(32);
--> statement-breakpoint
ALTER TABLE `users` ADD COLUMN `usernameChangedAt` timestamp;
--> statement-breakpoint
ALTER TABLE `users` MODIFY COLUMN `role` enum('super_owner','owner','admin') NOT NULL DEFAULT 'admin';
--> statement-breakpoint
ALTER TABLE `users` ADD COLUMN `accessStatus` enum('pending','active','revoked') NOT NULL DEFAULT 'pending';
--> statement-breakpoint
ALTER TABLE `users` ADD COLUMN `accessExpiresAt` timestamp;
--> statement-breakpoint
ALTER TABLE `users` ADD CONSTRAINT `users_username_unique` UNIQUE(`username`);
