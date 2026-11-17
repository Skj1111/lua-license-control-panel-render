import type { Express, Request, Response } from "express";
import { SignJWT, jwtVerify } from "jose";

import { getUserByOpenId, getUserByUsername } from "./db";
import { getActiveServerMaintenance } from "./maintenance";
import {
  getActiveSystemAnnouncement,
  getBindings,
  getStats,
  listKeys,
  unbindDevice,
  validateCanonicalLicense,
} from "./license";
import { verifyPassword } from "./passwordAuth";
import { ENV } from "./_core/env";
import type { User } from "../drizzle/schema";

function publicUser(user: User) {
  return {
    id: user.id,
    openId: user.openId,
    username: user.username,
    name: user.name,
    email: user.email,
    role: user.role,
    accessStatus: user.accessStatus,
    accessExpiresAt: user.accessExpiresAt,
    lastSignedIn: user.lastSignedIn,
  };
}

function isActiveAdmin(user: User) {
  const owner = user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId;
  return user.accessStatus === "active" && (owner || user.role === "admin");
}

async function readBearerUser(req: Request) {
  const header = req.header("authorization") ?? "";
  const token = header.startsWith("Bearer ") ? header.slice("Bearer ".length).trim() : "";
  if (!token) return null;
  try {
    const { payload } = await jwtVerify(token, new TextEncoder().encode(ENV.cookieSecret));
    return payload.openId ? (await getUserByOpenId(String(payload.openId))) ?? null : null;
  } catch {
    return null;
  }
}

function requireUser(handler: (req: Request, res: Response, user: User) => Promise<unknown> | unknown) {
  return async (req: Request, res: Response) => {
    const user = await readBearerUser(req);
    if (!user) return res.status(401).json({ success: false, error: "authentication_required" });
    try {
      return await handler(req, res, user);
    } catch (error) {
      console.error("[mobile-api] request failed", error);
      return res.status(500).json({ success: false, error: "service_unavailable" });
    }
  };
}

export function registerMobileRoutes(app: Express) {
  app.get("/api/mobile/health", (_req, res) => res.json({ ok: true, service: "nexa-lord" }));

  app.post("/api/mobile/login", async (req, res) => {
    const username = typeof req.body?.username === "string" ? req.body.username.trim() : "";
    const password = typeof req.body?.password === "string" ? req.body.password : "";
    if (!username || !password) return res.status(400).json({ success: false, error: "username_and_password_required" });
    const user = await getUserByUsername(username);
    if (!user || !verifyPassword(password, user.passwordHash)) {
      return res.status(401).json({ success: false, error: "invalid_credentials" });
    }
    const token = await new SignJWT({ openId: user.openId, client: "nexa-lord-mobile" })
      .setProtectedHeader({ alg: "HS256" })
      .setExpirationTime("30d")
      .sign(new TextEncoder().encode(ENV.cookieSecret));
    return res.json({ success: true, token, user: publicUser(user) });
  });

  app.get("/api/mobile/me", requireUser(async (_req, res, user) => {
    return res.json({ success: true, user: publicUser(user) });
  }));

  app.get("/api/mobile/dashboard", requireUser(async (_req, res, user) => {
    const owner = user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId;
    const active = user.accessStatus === "active";
    const stats = active && isActiveAdmin(user) ? await getStats({ id: user.id, isOwner: owner }) : null;
    const keys = active && isActiveAdmin(user) ? await listKeys({ id: user.id, isOwner: owner }) : [];
    return res.json({
      success: true,
      user: publicUser(user),
      stats,
      keys,
      announcement: await getActiveSystemAnnouncement(),
      maintenance: await getActiveServerMaintenance(),
    });
  }));

  app.get("/api/mobile/keys", requireUser(async (_req, res, user) => {
    if (!isActiveAdmin(user)) return res.status(403).json({ success: false, error: "management_access_required" });
    const owner = user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId;
    return res.json({ success: true, keys: await listKeys({ id: user.id, isOwner: owner }) });
  }));

  app.get("/api/mobile/keys/:keyId/bindings", requireUser(async (req, res, user) => {
    if (!isActiveAdmin(user)) return res.status(403).json({ success: false, error: "management_access_required" });
    const keyId = Number(req.params.keyId);
    if (!Number.isInteger(keyId) || keyId < 1) return res.status(400).json({ success: false, error: "invalid_key_id" });
    const owner = user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId;
    return res.json({ success: true, bindings: await getBindings(keyId, { id: user.id, isOwner: owner }) });
  }));

  app.post("/api/mobile/keys/:keyId/unbind", requireUser(async (req, res, user) => {
    if (!isActiveAdmin(user)) return res.status(403).json({ success: false, error: "management_access_required" });
    const bindingId = Number(req.body?.bindingId);
    if (!Number.isInteger(bindingId) || bindingId < 1) return res.status(400).json({ success: false, error: "invalid_binding_id" });
    const owner = user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId;
    return res.json({ success: true, result: await unbindDevice(bindingId, { id: user.id, isOwner: owner }) });
  }));

  app.post("/api/mobile/license/validate", async (req, res) => {
    const key = typeof req.body?.key === "string" ? req.body.key.trim() : "";
    const deviceId = typeof req.body?.deviceId === "string" ? req.body.deviceId.trim() : "";
    if (!key || key.length > 128 || !deviceId || deviceId.length > 256) {
      return res.status(400).json({ success: false, status: "not_registered", message: "A valid key and device ID are required." });
    }
    try {
      return res.json(await validateCanonicalLicense(key, deviceId));
    } catch {
      return res.status(500).json({ success: false, status: "invalid", message: "Validation service is temporarily unavailable." });
    }
  });

  app.get("/api/mobile/announcement", async (_req, res) => {
    try {
      return res.json(await getActiveSystemAnnouncement());
    } catch {
      return res.json({ active: false });
    }
  });

  app.get("/api/mobile/maintenance", async (_req, res) => {
    try {
      return res.json(await getActiveServerMaintenance());
    } catch {
      return res.json({ active: false });
    }
  });
}
