import React, { useEffect, useState } from "react";
import { Link } from "wouter";
import { ShieldAlert } from "lucide-react";
import { toast } from "sonner";
import { trpc } from "@/lib/trpc";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";

const toDateTimeLocal = (value: Date | string) => {
  const date = new Date(value);
  const pad = (unit: number) => String(unit).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

export default function SystemControl() {
  const identity = trpc.dashboard.identity.useQuery();
  const isSuperOwner = identity.data?.role === "super_owner" && identity.data.accessStatus === "active";
  const maintenance = trpc.superOwner.maintenance.useQuery(undefined, { enabled: isSuperOwner });
  const [enabled, setEnabled] = useState(false);
  const [title, setTitle] = useState("SERVER MAINTENANCE");
  const [message, setMessage] = useState("");
  const [startAt, setStartAt] = useState("");
  const [endAt, setEndAt] = useState("");
  const save = trpc.superOwner.updateMaintenance.useMutation({ onSuccess: () => { toast.success("Server maintenance settings saved"); void maintenance.refetch(); }, onError: error => toast.error(error.message) });

  useEffect(() => {
    const current = maintenance.data;
    if (!current) return;
    setEnabled(current.enabled);
    setTitle(current.title);
    setMessage(current.message);
    setStartAt(current.startAt ? toDateTimeLocal(current.startAt) : "");
    setEndAt(current.endAt ? toDateTimeLocal(current.endAt) : "");
  }, [maintenance.data]);

  if (identity.isLoading) return <Card className="hud-panel mx-auto mt-10 max-w-xl"><CardContent className="p-8 text-center"><p className="eyebrow">AUTHORIZATION BUS // SYNCHRONIZING</p><h1 className="neon-heading mt-2 text-3xl">VERIFYING SUPER OWNER ACCESS</h1><p className="mt-4 text-sm text-slate-400">Loading the server-enforced control scope before maintenance settings are exposed.</p></CardContent></Card>;
  if (identity.error) return <Card className="hud-panel mx-auto mt-10 max-w-xl"><CardContent className="p-8 text-center"><ShieldAlert className="mx-auto h-8 w-8 text-pink-300" /><p className="eyebrow mt-4 text-pink-300">AUTHORIZATION BUS // UNAVAILABLE</p><h1 className="neon-heading mt-2 text-3xl">ACCESS CHECK FAILED</h1><p className="mt-4 text-sm text-slate-400">Maintenance controls remain disabled until the panel can verify your role. Refresh or sign in again.</p></CardContent></Card>;
  if (!isSuperOwner) return <Card className="hud-panel mx-auto mt-10 max-w-xl"><CardContent className="p-8 text-center"><ShieldAlert className="mx-auto h-8 w-8 text-pink-300" /><p className="eyebrow mt-4">RESTRICTED NODE</p><h1 className="neon-heading mt-2 text-3xl">SUPER OWNER ONLY</h1><p className="mt-4 text-sm text-slate-400">System Control is available only to the active configured Super Owner account.</p><Link href="/" className="cyber-outline mt-6 inline-flex h-10 items-center px-4 text-sm">RETURN TO DASHBOARD</Link></CardContent></Card>;

  return <><header className="flex flex-col gap-5 border-b border-cyan-300/15 pb-7 sm:flex-row sm:items-end sm:justify-between"><div><p className="eyebrow text-amber-200">SUPER OWNER // OPERATIONAL HOLD</p><h1 className="neon-heading mt-2 text-4xl sm:text-5xl">SYSTEM CONTROL</h1><p className="mt-3 max-w-2xl text-sm text-slate-400">Configure the separate Server Maintenance state. It preserves valid license status while holding Main Lua during an active window.</p></div><Link href="/" className="cyber-outline inline-flex h-10 items-center justify-center px-4 text-sm">RETURN TO DASHBOARD</Link></header>
    <section className="mt-7"><Card className="hud-panel border-amber-300/25"><CardContent className="p-6"><div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"><div><p className="eyebrow text-amber-200">SUPER OWNER // OPERATIONAL HOLD</p><h2 className="section-title">Server maintenance</h2><p className="mt-2 text-xs leading-5 text-slate-500">Separate from System Announcement. An active window preserves a valid license, shows this exact popup, and holds Main Lua before later presentation extensions run.</p></div><Switch checked={enabled} onCheckedChange={setEnabled} aria-label="Enable server maintenance" /></div><div className="mt-5 grid gap-4 md:grid-cols-2"><label className="field-label">popup title <span className="text-slate-500">({title.length}/80)</span><Input value={title} maxLength={80} onChange={event => setTitle(event.target.value)} className="cyber-input mt-2" placeholder="SERVER MAINTENANCE" /></label><label className="field-label">schedule <span className="text-slate-500">(local input; saved and evaluated as UTC)</span><div className="mt-2 grid grid-cols-2 gap-2"><input type="datetime-local" value={startAt} onChange={event => setStartAt(event.target.value)} className="cyber-input h-10 w-full px-3" aria-label="Maintenance start date and time" /><input type="datetime-local" value={endAt} onChange={event => setEndAt(event.target.value)} className="cyber-input h-10 w-full px-3" aria-label="Maintenance end date and time" /></div></label><label className="field-label md:col-span-2">popup message <span className="text-slate-500">({message.length}/300)</span><Textarea value={message} maxLength={300} onChange={event => setMessage(event.target.value)} className="cyber-input mt-2 min-h-28" placeholder={"🔧 Server Under Maintenance\n\nPlease wait... ⏳\n\n📢 For more updates, contact Admin."} /></label></div><div className="mt-5 flex items-center justify-between gap-3"><p className="text-xs text-slate-500">Unicode and emoji are supported. Enabled maintenance requires a message; blank schedule fields create an immediate open-ended hold.</p><Button className="cyber-button border-amber-300/40 text-amber-100" disabled={save.isPending || (enabled && !message.trim())} onClick={() => save.mutate({ enabled, title, message, startAt: startAt ? new Date(startAt) : null, endAt: endAt ? new Date(endAt) : null })}>{save.isPending ? "SAVING…" : "SAVE MAINTENANCE"}</Button></div></CardContent></Card></section>
  </>;
}
