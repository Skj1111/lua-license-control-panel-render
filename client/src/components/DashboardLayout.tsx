import { useAuth } from "@/_core/hooks/useAuth";
import { startLogin } from "@/const";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { trpc } from "@/lib/trpc";
import { Link } from "wouter";
import { LayoutDashboard, LoaderCircle, LogOut, ShieldCheck } from "lucide-react";

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { user, loading, logout } = useAuth();
  const identity = trpc.dashboard.identity.useQuery(undefined, { enabled: Boolean(user) });
  if (loading) return (
    <div className="cyber-grid min-h-screen bg-[#07070b] px-5 text-slate-100">
      <div className="mx-auto grid min-h-screen max-w-md place-items-center">
        <div className="hud-panel w-full overflow-hidden p-0">
          <div className="h-1 bg-gradient-to-r from-cyan-300 via-pink-400 to-cyan-300" />
          <div className="space-y-6 p-7 sm:p-9">
            <div className="flex items-center justify-between"><div className="brand-mark">LX</div><p className="eyebrow">AUTHENTICATION BUS // ONLINE</p></div>
            <div><p className="eyebrow">CONTROL GRID INITIALIZATION</p><h1 className="neon-heading mt-3 text-3xl">VERIFYING SESSION</h1><p className="mt-3 max-w-sm text-sm leading-6 text-slate-400">Securely loading your account permissions, license registry, and management controls.</p></div>
            <div className="space-y-3">
              <div className="flex items-center justify-between text-xs uppercase tracking-[.16em] text-cyan-200"><span>Session handshake</span><span className="flex items-center gap-2"><LoaderCircle className="h-3.5 w-3.5 animate-spin" /> active</span></div>
              <div className="h-1.5 overflow-hidden rounded-full bg-cyan-300/10"><div className="h-full w-2/3 animate-pulse bg-gradient-to-r from-cyan-300 to-pink-400" /></div>
            </div>
            <div className="grid grid-cols-3 gap-2 text-[10px] uppercase tracking-[.14em] text-slate-500"><span>Roles</span><span>Bindings</span><span className="text-right">Policies</span></div>
          </div>
        </div>
      </div>
    </div>
  );
  if (!user) return <div className="min-h-screen bg-[#07070b] grid place-items-center p-6"><div className="hud-panel max-w-md w-full p-8 text-center"><p className="eyebrow">AUTH GATE // 01</p><h1 className="neon-heading text-3xl mt-3">ACCESS REQUIRED</h1><p className="mt-4 text-slate-400">Sign in with your own email account. New admins must sign in first, then redeem the referral code provided by the owner.</p><Button className="mt-7 w-full cyber-button" onClick={() => startLogin()}>SIGN IN</Button></div></div>;
  const isOwner = identity.data?.role === "super_owner" || identity.data?.role === "owner" || user.role === "owner" || user.openId === import.meta.env.VITE_OWNER_OPEN_ID;
  const isSuperOwner = identity.data?.role === "super_owner";
  const roleLabel = identity.data?.role === "super_owner" ? "super owner" : isOwner ? "owner" : identity.data?.role ?? user.role;
  const displayName = identity.data?.username || user.name || user.email;
  const displayEmail = identity.data?.email || user.email;
  const switchAccount = async () => {
    await logout();
    startLogin();
  };
  return <div className="min-h-screen bg-[#07070b] text-slate-100 cyber-grid"><aside className="fixed inset-y-0 left-0 hidden w-72 border-r border-cyan-400/20 bg-[#090910]/95 p-5 lg:block"><div className="flex items-center gap-3 mb-12"><div className="brand-mark">LX</div><div><p className="font-display text-lg tracking-[.2em] text-cyan-200">LICENSE//X</p><p className="text-[10px] tracking-[.24em] text-pink-400">CONTROL PANEL</p></div></div><nav className="space-y-2"><Link href="/" className="nav-active"><LayoutDashboard className="h-4 w-4" /> Overview</Link><Link href="/#key-registry" className="nav-item"><ShieldCheck className="h-4 w-4" /> Key registry</Link>{isSuperOwner && <Link href="/super-owner" className="nav-item"><ShieldCheck className="h-4 w-4" /> Super Owner overview</Link>}</nav><div className="absolute bottom-5 left-5 right-5 hud-divider pt-4"><div className="flex items-center gap-3"><Avatar className="h-9 w-9 border border-pink-400/40"><AvatarFallback className="bg-pink-500/10 text-pink-200">{displayName?.[0] ?? "U"}</AvatarFallback></Avatar><div className="min-w-0"><p className="truncate text-sm text-cyan-100">{displayName}</p><p className="truncate text-[10px] text-slate-500">{displayEmail}</p><p className="text-[10px] uppercase tracking-[.18em] text-pink-300">{roleLabel}</p></div><button className="ml-auto text-slate-500 hover:text-pink-300" onClick={logout} aria-label="Sign out" title="Sign out"><LogOut className="h-4 w-4" /></button></div><button className="mt-3 w-full text-left text-[10px] uppercase tracking-[.16em] text-cyan-300 hover:text-white" onClick={switchAccount}>Sign in with another account</button></div></aside><main className="min-h-screen lg:pl-72"><div className="mx-auto max-w-[1600px] p-5 sm:p-8"><div className="mb-5 flex items-center justify-between rounded border border-cyan-300/15 bg-cyan-300/5 px-4 py-3 lg:hidden"><div className="min-w-0"><p className="truncate text-xs text-cyan-100">{displayName}</p><p className="truncate text-[10px] text-slate-500">{displayEmail}</p><p className="text-[10px] uppercase tracking-[.18em] text-pink-300">ACTIVE SESSION // {roleLabel}</p></div><button className="text-slate-300 hover:text-pink-300" onClick={switchAccount} aria-label="Sign in with another account" title="Sign in with another account"><LogOut className="h-4 w-4" /></button></div>{children}</div></main></div>;
}
