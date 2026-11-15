import { hashPassword } from "./server/passwordAuth.ts";
import { getDb } from "./server/db.ts";
import { users } from "./drizzle/schema.ts";
import crypto from "node:crypto";

async function setupOwner() {
  const db = await getDb();
  if (!db) {
    console.error("Database unavailable");
    process.exit(1);
  }

  const username = "NEXA";
  const password = "NEXA@2026";
  const passwordHash = hashPassword(password);
  
  const existing = await db.select().from(users).where((await import('drizzle-orm')).eq(users.username, username)).limit(1);
  if (existing.length > 0) {
    console.log("User already exists, updating...");
    await db.update(users).set({
      passwordHash,
      role: 'owner',
      accessStatus: 'active'
    }).where((await import('drizzle-orm')).eq(users.username, username));
  } else {
    console.log("Creating owner...");
    await db.insert(users).values({
      openId: `local_${crypto.randomUUID()}`,
      name: username,
      username: username,
      passwordHash: passwordHash,
      loginMethod: "password",
      role: "owner",
      accessStatus: "active",
      lastSignedIn: new Date(),
    });
  }
  console.log("Owner setup complete.");
}

setupOwner().catch(console.error);
