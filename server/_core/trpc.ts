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
  if (!user || user.accessStatus !== "active" || user.role !== "super_owner" || user.openId !== ENV.ownerOpenId) {
    throw new TRPCError({ code: "FORBIDDEN", message: "Super Owner access required" });
  }
  return opts.next({ ctx: { ...opts.ctx, user, isOwner: true, isSuperOwner: true } });
}));
