import { initTRPC, TRPCError } from "@trpc/server";
import superjson from "superjson";
import type { TrpcContext } from "./context";
import { ENV } from "./env";

const t = initTRPC.context<TrpcContext>().create({ transformer: superjson });
export const router = t.router;
export const publicProcedure = t.procedure;

const requireUser = t.middleware(async opts => {
  if (!opts.ctx.user) throw new TRPCError({ code: "UNAUTHORIZED", message: "Authentication required" });
  return opts.next({ ctx: { ...opts.ctx, user: opts.ctx.user } });
});

export const protectedProcedure = t.procedure.use(requireUser);

type AuthorizationUser = Pick<NonNullable<TrpcContext["user"]>, "role" | "accessStatus">;
type ActiveSuperOwner = NonNullable<TrpcContext["user"]> & {
  role: "super_owner";
  accessStatus: "active";
};

/**
 * A Super Owner role is assigned only through the server-controlled user table.
 * Do not additionally require a potentially stale deployment owner-open-ID value:
 * that extra comparison can deny the active Super Owner after project ownership
 * or OAuth identity metadata changes.
 */
export function isActiveSuperOwner(user: TrpcContext["user"]): user is ActiveSuperOwner;
export function isActiveSuperOwner(user: AuthorizationUser | null | undefined): boolean;
export function isActiveSuperOwner(user: AuthorizationUser | null | undefined) {
  return Boolean(user && user.accessStatus === "active" && user.role === "super_owner");
}

export const adminProcedure = t.procedure.use(t.middleware(async opts => {
  const user = opts.ctx.user;
  const isOwner = Boolean(user && user.accessStatus === "active" && (user.role === "super_owner" || user.role === "owner" || user.openId === ENV.ownerOpenId));
  if (!user || user.accessStatus !== "active" || (!isOwner && user.role !== "admin")) {
    throw new TRPCError({ code: "FORBIDDEN", message: "Owner or admin access required" });
  }
  return opts.next({ ctx: { ...opts.ctx, user, isOwner } });
}));

export const ownerProcedure = t.procedure.use(t.middleware(async opts => {
  const user = opts.ctx.user;
  if (!user || user.accessStatus !== "active" || (user.role !== "super_owner" && user.role !== "owner" && user.openId !== ENV.ownerOpenId)) {
    throw new TRPCError({ code: "FORBIDDEN", message: "Owner access required" });
  }
  return opts.next({ ctx: { ...opts.ctx, user, isOwner: true } });
}));

export const superOwnerProcedure = t.procedure.use(t.middleware(async opts => {
  const user = opts.ctx.user;
  if (!isActiveSuperOwner(user)) {
    throw new TRPCError({ code: "FORBIDDEN", message: "Super Owner access required" });
  }
  return opts.next({ ctx: { ...opts.ctx, user, isOwner: true, isSuperOwner: true } });
}));
