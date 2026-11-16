import { eq } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import crypto from "node:crypto";
import { InsertUser, users } from "../drizzle/schema";
import { ENV } from './_core/env';
import { hashPassword, LOCAL_PASSWORD_MIN_LENGTH } from "./passwordAuth";

let _db: ReturnType<typeof drizzle> | null = null;

// Lazily create the drizzle instance so local tooling can run without a DB.
export async function getDb() {
  if (!_db && process.env.DATABASE_URL) {
    try {
      _db = drizzle(process.env.DATABASE_URL);
    } catch (error) {
      console.warn("[Database] Failed to connect:", error);
      _db = null;
    }
  }
  return _db;
}

export async function upsertUser(user: InsertUser): Promise<void> {
  if (!user.openId) {
    throw new Error("User openId is required for upsert");
  }

  const db = await getDb();
  if (!db) {
    console.warn("[Database] Cannot upsert user: database not available");
    return;
  }

  try {
    const values: InsertUser = {
      openId: user.openId,
    };
    const updateSet: Record<string, unknown> = {};

    const textFields = ["name", "email", "loginMethod"] as const;
    type TextField = (typeof textFields)[number];

    const assignNullable = (field: TextField) => {
      const value = user[field];
      if (value === undefined) return;
      const normalized = value ?? null;
      values[field] = normalized;
      updateSet[field] = normalized;
    };

    textFields.forEach(assignNullable);

    if (user.lastSignedIn !== undefined) {
      values.lastSignedIn = user.lastSignedIn;
      updateSet.lastSignedIn = user.lastSignedIn;
    }
    const isConfiguredOwner = user.openId === ENV.ownerOpenId;
    if (isConfiguredOwner) {
      values.role = 'super_owner';
      updateSet.role = 'super_owner';
      values.accessStatus = 'active';
      updateSet.accessStatus = 'active';
    } else if (user.role !== undefined) {
      values.role = user.role;
      updateSet.role = user.role;
    }

    if (!values.lastSignedIn) {
      values.lastSignedIn = new Date();
    }

    if (Object.keys(updateSet).length === 0) {
      updateSet.lastSignedIn = new Date();
    }

    await db.insert(users).values(values).onDuplicateKeyUpdate({
      set: updateSet,
    });
  } catch (error) {
    console.error("[Database] Failed to upsert user:", error);
    throw error;
  }
}

export async function getUserByOpenId(openId: string) {
  const db = await getDb();
  if (!db) {
    console.warn("[Database] Cannot get user: database not available");
    return undefined;
  }

  const result = await db.select().from(users).where(eq(users.openId, openId)).limit(1);

  return result.length > 0 ? result[0] : undefined;
}

export async function getUserByUsername(username: string) {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(users).where(eq(users.username, username)).limit(1);
  return result.length > 0 ? result[0] : undefined;
}

export async function bootstrapLocalSuperOwner() {
  const username = process.env.BOOTSTRAP_OWNER_USERNAME?.trim();
  const password = process.env.BOOTSTRAP_OWNER_PASSWORD;
  if (!username && !password) return false;
  if (!username || !password) throw new Error("Both BOOTSTRAP_OWNER_USERNAME and BOOTSTRAP_OWNER_PASSWORD are required");
  if (password.length < LOCAL_PASSWORD_MIN_LENGTH) throw new Error("BOOTSTRAP_OWNER_PASSWORD is too short");
  const db = await getDb();
  if (!db) throw new Error("Database unavailable for Super Owner bootstrap");
  const passwordHash = hashPassword(password);
  const existing = await db.select({ id: users.id }).from(users).where(eq(users.username, username)).limit(1);
  if (existing.length > 0) {
    await db.update(users).set({ passwordHash, role: "super_owner", accessStatus: "active", loginMethod: "password", name: username, updatedAt: new Date() }).where(eq(users.id, existing[0].id));
  } else {
    await db.insert(users).values({ openId: `local_${crypto.randomUUID()}`, name: username, username, passwordHash, loginMethod: "password", role: "super_owner", accessStatus: "active", lastSignedIn: new Date() });
  }
  console.log(`[Database] Bootstrapped Super Owner account: ${username}`);
  return true;
}

export async function createLocalUser(username: string, passwordHash: string) {
  const db = await getDb();
  if (!db) throw new Error("Database unavailable");
  const now = new Date();
  const openId = `local_${crypto.randomUUID()}`;
  await db.insert(users).values({
    openId,
    name: username,
    username,
    passwordHash,
    loginMethod: "password",
    role: "admin",
    accessStatus: "pending",
    lastSignedIn: now,
  });
  const created = await getUserByOpenId(openId);
  if (!created) throw new Error("Local account could not be created");
  return created;
}

export async function setLocalCredentials(userId: number, username: string, passwordHash: string) {
  const db = await getDb();
  if (!db) throw new Error("Database unavailable");
  await db.update(users).set({
    name: username,
    username,
    passwordHash,
    loginMethod: "password",
    updatedAt: new Date(),
  }).where(eq(users.id, userId));
  const result = await db.select().from(users).where(eq(users.id, userId)).limit(1);
  return result[0];
}

export async function touchUserLastSignedIn(userId: number) {
  const db = await getDb();
  if (!db) return;
  await db.update(users).set({ lastSignedIn: new Date() }).where(eq(users.id, userId));
}

// TODO: add feature queries here as your schema grows.
