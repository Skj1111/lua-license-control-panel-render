import { useEffect, useState } from "react";
import { Link } from "wouter";
import { ShieldAlert } from "lucide-react";
import { toast } from "sonner";
import { trpc } from "@/lib/trpc";
import { TelegramExtensionCard } from "@/components/TelegramExtensionCard";
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

export default function AnnouncementControl() {
  const identity = trpc.dashboard.identity.useQuery();
  const isSuperOwner = identity.data?.role === "super_owner" && identity.data.accessStatus === "active";
  const announcement = trpc.superOwner.announcement.useQuery(undefined, { enabled: isSuperOwner });
  const [enabled, setEnabled] = useState(false);
  const [title, setTitle] = useState("SYSTEM ANNOUNCEMENT");
  const [message, setMessage] = useState("");
  const [startAt, setStartAt] = useState("");
  const [endAt, setEndAt] = useState("");
  const [displayMode, setDisplayMode] = useState<"every_login" | "once_per_day">("every_login");
  const [textSize, setTextSize] = useState<"small" | "normal" | "large" | "festival_large">("normal");
  const save = trpc.superOwner.updateAnnouncement.useMutation({ onSuccess: () => { toast.success("System announcement saved"); void announcement.refetch(); }, onError: error => toast.error(error.message) });

  useEffect(() => {
    const current = announcement.data;
    if (!current) return;
    setEnabled(current.enabled);
    setTitle(current.title);
    setMessage(current.message);
    setStartAt(current.startAt ? toDateTimeLocal(current.startAt) : "");
    setEndAt(current.endAt ? toDateTimeLocal(current.endAt) : "");
    setDisplayMode(current.displayMode);
    setTextSize(current.textSize);
  }, [announcement.data]);

  if (!isSuperOwner) return <Card className="hud-panel mx-auto mt-10 max-w-xl"><CardContent className="p-8 text-center"><ShieldAlert className="mx-auto h-8 w-8 text-pink-300" /><p className="eyebrow mt-4">RESTRICTED NODE</p><h1 className="neon-heading mt-2 text-3xl">SUPER OWNER ONLY</h1><p className="mt-4 text-sm text-slate-400">Announcement Control is available only to the active configured Super Owner account.</p><Link href="/" className="cyber-outline mt-6 inline-flex h-10 items-center px-4 text-sm">RETURN TO DASHBOARD</Link></CardContent></Card>;

  return <><header className="flex flex-col gap-5 border-b border-cyan-300/15 pb-7 sm:flex-row sm:items-end sm:justify-between"><div><p className="eyebrow">SUPER OWNER // PRESENTATION CONTROL</p><h1 className="neon-heading mt-2 text-4xl sm:text-5xl">ANNOUNCEMENT CONTROL</h1><p className="mt-3 max-w-2xl text-sm text-slate-400">Manage the existing System Announcement and isolated Telegram extension without changing their validation or authorization behavior.</p></div><Link href="/" className="cyber-outline inline-flex h-10 items-center justify-center px-4 text-sm">RETURN TO DASHBOARD</Link></header>
    <section className="mt-7"><Card className="hud-panel"><CardContent className="p-6"><div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"><div><p className="eyebrow">SUPER OWNER CONTROL</p><h2 className="section-title">System announcement</h2><p className="mt-2 text-xs leading-5 text-slate-500">Shown only after a successful license validation. Plain Unicode emoji are supported; validation and Lua authorization remain independent.</p></div><Switch checked={enabled} onCheckedChange={setEnabled} aria-label="Enable system announcement" /></div><div className="mt-5 grid gap-4 md:grid-cols-2"><label className="field-label">title<Input value={title} maxLength={80} onChange={event => setTitle(event.target.value)} className="cyber-input mt-2" /></label><label className="field-label">text size<select value={textSize} onChange={event => setTextSize(event.target.value as typeof textSize)} className="cyber-input mt-2 h-10 w-full px-3"><option value="small">Small</option><option value="normal">Normal</option><option value="large">Large</option><option value="festival_large">Festival / Large</option></select></label><label className="field-label md:col-span-2">message <span className="text-slate-500">({message.length}/300)</span><Textarea value={message} maxLength={300} onChange={event => setMessage(event.target.value)} className="cyber-input mt-2 min-h-24" placeholder="🇮🇳 Happy Independence Day to all users! 🎉" /></label><label className="field-label">start date/time<input type="datetime-local" value={startAt} onChange={event => setStartAt(event.target.value)} className="cyber-input mt-2 h-10 w-full px-3" /></label><label className="field-label">end date/time<input type="datetime-local" value={endAt} onChange={event => setEndAt(event.target.value)} className="cyber-input mt-2 h-10 w-full px-3" /></label><label className="field-label">display mode<select value={displayMode} onChange={event => setDisplayMode(event.target.value as typeof displayMode)} className="cyber-input mt-2 h-10 w-full px-3"><option value="every_login">Every successful login</option><option value="once_per_day">Once per day</option></select></label></div><div className="mt-5 flex items-center justify-between gap-3"><p className="text-xs text-slate-500">The message is optional presentation data and never blocks a valid Lua load.</p><Button className="cyber-button" disabled={save.isPending || !message.trim()} onClick={() => save.mutate({ enabled, title, message, startAt: startAt ? new Date(startAt) : null, endAt: endAt ? new Date(endAt) : null, displayMode, textSize })}>{save.isPending ? "SAVING…" : "SAVE ANNOUNCEMENT"}</Button></div></CardContent></Card></section>
    <TelegramExtensionCard isSuperOwner={isSuperOwner} isOwner={false} isAdmin={false} isActive />
  </>;
}
