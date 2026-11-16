import { useAuth } from "@/_core/hooks/useAuth";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Link, useLocation } from "wouter";
import { Bell, FileClock, KeyRound, LayoutDashboard, LoaderCircle, LogOut, Megaphone, Menu, Radio, ShieldCheck, SlidersHorizontal, Users, X } from "lucide-react";
import { useState } from "react";
import { trpc } from "@/lib/trpc";

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { user, loading, logout } = useAuth();
  const [location] = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const identity = trpc.dashboard.identity.useQuery(undefined, { enabled: Boolean(user) });

  if (loading) return <div className="cyber-grid min-h-screen bg-[#05070d] px-5 text-slate-100"><div className="mx-auto grid min-h-screen max-w-md place-items-center"><div className="hud-panel w-full overflow-hidden p-0"><div className="h-1 bg-gradient-to-r from-cyan-300 via-fuchsia-400 to-emerald-300" /><div className="space-y-6 p-7 sm:p-9"><div className="flex items-center justify-between"><div className="brand-mark">NX</div><p className="eyebrow">AUTHENTICATION BUS // ONLINE</p></div><div><p className="eyebrow">COMMAND OS INITIALIZATION</p><h1 className="neon-heading mt-3 text-3xl">VERIFYING SESSION</h1><p className="mt-3 text-sm leading-6 text-slate-400">Loading permissions, license registry, and protected control surfaces.</p></div><div className="h-1.5 overflow-hidden rounded-full bg-cyan-300/10"><div className="h-full w-2/3 animate-pulse bg-gradient-to-r from-cyan-300 to-fuchsia-400" /></div></div></div></div></div>;
  if (!user && location === "/") return <>{children}</>;
  if (!user) return <div className="min-h-screen bg-[#05070d] grid place-items-center p-6"><div className="hud-panel max-w-md w-full p-8 text-center"><p className="eyebrow">AUTH GATE // 01</p><h1 className="neon-heading text-3xl mt-3">ACCESS REQUIRED</h1><p className="mt-4 text-slate-400">Return to the panel login screen and sign in with your local username and password.</p><Link href="/" className="cyber-button mt-7 inline-flex w-full items-center justify-center">RETURN TO LOGIN</Link></div></div>;

  const isOwner = identity.data?.role === "super_owner" || identity.data?.role === "owner" || user.role === "owner" || user.openId === import.meta.env.VITE_OWNER_OPEN_ID;
  const isSuperOwner = identity.data?.role === "super_owner";
  const roleLabel = isSuperOwner ? "super owner" : isOwner ? "owner" : identity.data?.role ?? user.role;
  const displayName = identity.data?.username || user.name || user.email;
  const displayEmail = identity.data?.email || user.email;
  const switchAccount = async () => { await logout(); window.location.href = "/"; };
  const navItems = [
    { href: "/", label: "Command center", icon: LayoutDashboard, active: location === "/" },
    { href: "/#key-registry", label: "License manager", icon: KeyRound, active: false },
    { href: "/#user-management", label: "User management", icon: Users, active: false, ownerOnly: true },
    { href: "/#audit-log", label: "Security logs", icon: FileClock, active: false, ownerOnly: true },
    { href: "/system-control", label: "Server maintenance", icon: SlidersHorizontal, active: location === "/system-control", superOnly: true },
    { href: "/announcement-control", label: "Announcements", icon: Megaphone, active: location === "/announcement-control", superOnly: true },
    { href: "/super-owner", label: "Global overview", icon: ShieldCheck, active: location === "/super-owner", superOnly: true },
  ].filter(item => (!item.ownerOnly || isOwner) && (!item.superOnly || isSuperOwner));
  const Nav = ({ mobile = false }: { mobile?: boolean }) => <nav className={mobile ? "grid gap-1" : "space-y-1"} aria-label="Primary navigation">{navItems.map(({ href, label, icon: Icon, active }) => <Link key={href} href={href} onClick={() => mobile && setMobileOpen(false)} className={active ? "nav-active" : "nav-item"}><Icon className="h-4 w-4 shrink-0" /><span>{label}</span>{active && <span className="ml-auto nav-live-dot" />}</Link>)}</nav>;

  return <div className="panel-shell min-h-screen text-slate-100 cyber-grid">
    <aside className="command-sidebar fixed inset-y-0 left-0 z-30 hidden w-[276px] border-r border-cyan-200/15 p-5 lg:block"><div className="flex items-center gap-3 border-b border-cyan-200/10 pb-6"><div className="brand-mark">NX</div><div><p className="font-display text-lg tracking-[.2em] text-cyan-100">NEXA//OS</p><p className="text-[10px] tracking-[.24em] text-fuchsia-200">LICENSE CONTROL</p></div></div><div className="mt-7 mb-3 flex items-center justify-between"><p className="eyebrow">NAVIGATION</p><span className="status-pulse"><Radio className="h-3 w-3" /> LIVE</span></div><Nav /><div className="absolute bottom-5 left-5 right-5 hud-divider pt-4"><div className="mb-4 flex items-center gap-2 text-[10px] uppercase tracking-[.16em] text-emerald-300"><span className="live-dot" /> API network online</div><div className="flex items-center gap-3"><Avatar className="h-9 w-9 border border-cyan-200/25"><AvatarFallback className="bg-cyan-300/10 text-cyan-100">{displayName?.[0] ?? "U"}</AvatarFallback></Avatar><div className="min-w-0"><p className="truncate text-sm text-cyan-50">{displayName}</p><p className="truncate text-[10px] text-slate-500">{displayEmail}</p><p className="text-[10px] uppercase tracking-[.18em] text-violet-200">{roleLabel}</p></div><button className="ml-auto text-slate-500 hover:text-cyan-100" onClick={logout} aria-label="Sign out"><LogOut className="h-4 w-4" /></button></div><button className="mt-3 w-full text-left text-[10px] uppercase tracking-[.16em] text-cyan-300 hover:text-white" onClick={switchAccount}>Sign in with another account</button></div></aside>
    {mobileOpen && <div className="fixed inset-0 z-40 bg-black/70 backdrop-blur-sm lg:hidden" onClick={() => setMobileOpen(false)}><aside className="mobile-drawer h-full w-[min(86vw,340px)] border-r border-cyan-200/20 bg-[#080d18] p-5" onClick={event => event.stopPropagation()}><div className="flex items-center justify-between border-b border-cyan-200/10 pb-6"><div className="flex items-center gap-3"><div className="brand-mark">NX</div><div><p className="font-display text-lg tracking-[.2em] text-cyan-100">NEXA//OS</p><p className="text-[10px] tracking-[.24em] text-fuchsia-200">LICENSE CONTROL</p></div></div><button onClick={() => setMobileOpen(false)} aria-label="Close navigation"><X className="h-5 w-5 text-slate-400" /></button></div><p className="eyebrow mt-7 mb-3">NAVIGATION</p><Nav mobile /></aside></div>}
    <main className="min-h-screen lg:pl-[276px]"><div className="mx-auto max-w-[1680px] p-4 sm:p-6 lg:p-8"><header className="command-topbar mb-7 flex items-center justify-between gap-4 rounded-2xl border border-cyan-200/15 bg-slate-950/50 px-4 py-3 backdrop-blur-xl sm:px-5"><div className="flex items-center gap-3"><button className="icon-button lg:hidden" onClick={() => setMobileOpen(true)} aria-label="Open navigation"><Menu className="h-5 w-5" /></button><div><p className="eyebrow">NEXA COMMAND OS / SECURE CHANNEL</p><p className="hidden text-xs text-slate-400 sm:block">License infrastructure telemetry and access operations</p></div></div><div className="flex items-center gap-3"><span className="hidden items-center gap-2 text-[10px] uppercase tracking-[.16em] text-emerald-300 sm:flex"><span className="live-dot" /> System online</span><button className="icon-button" aria-label="Notifications"><Bell className="h-4 w-4" /></button><div className="hidden h-6 w-px bg-cyan-200/15 sm:block" /><span className="text-xs text-cyan-100">{displayName}</span></div></header>{children}</div></main>
  </div>;
}
