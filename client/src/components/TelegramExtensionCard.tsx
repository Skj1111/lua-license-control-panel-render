import { useEffect, useState } from "react";
import { trpc } from "@/lib/trpc";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { toast } from "sonner";

type Props = { isSuperOwner: boolean; isOwner: boolean; isAdmin: boolean; isActive: boolean };

export function TelegramExtensionCard({ isSuperOwner, isOwner, isAdmin, isActive }: Props) {
  const settings = trpc.telegram.settings.useQuery(undefined, { enabled: isActive });
  const channel = trpc.telegram.myChannel.useQuery(undefined, { enabled: isActive });
  const setupState = trpc.telegram.setupState.useQuery(undefined, { enabled: isActive });
  const channels = trpc.telegram.channels.useQuery(undefined, { enabled: (isSuperOwner || isOwner) && isActive });
  const scopeCandidates = trpc.telegram.scopeCandidates.useQuery(undefined, { enabled: isSuperOwner && isActive });
  const [url, setUrl] = useState("");
  const [enabled, setEnabled] = useState(true);
  const [setupDialogOpen, setSetupDialogOpen] = useState(false);
  const [editingUserId, setEditingUserId] = useState<number | null>(null);
  const [editingUrl, setEditingUrl] = useState("");
  const save = trpc.telegram.saveChannel.useMutation({ onSuccess: () => { toast.success("Telegram channel saved"); setSetupDialogOpen(false); setEditingUserId(null); setEditingUrl(""); void channel.refetch(); void setupState.refetch(); void channels.refetch(); }, onError: e => toast.error(e.message) });
  const update = trpc.telegram.updateSettings.useMutation({ onSuccess: () => { toast.success("Telegram extension updated"); void settings.refetch(); }, onError: e => toast.error(e.message) });
  const setScope = trpc.telegram.setScope.useMutation({ onSuccess: () => { toast.success("Telegram account scope saved"); void scopeCandidates.refetch(); void channels.refetch(); }, onError: e => toast.error(e.message) });

  useEffect(() => {
    setUrl(channel.data?.channelUrl ?? "");
  }, [channel.data?.channelUrl]);
  useEffect(() => {
    if (settings.data) setEnabled(settings.data.enabled);
  }, [settings.data?.enabled]);
  useEffect(() => {
    if (enabled && setupState.data?.needsSetup) setSetupDialogOpen(true);
  }, [enabled, setupState.data?.needsSetup]);

  if (!isActive || (!isOwner && !isSuperOwner && !isAdmin)) return null;
  const canEditOwnChannel = isSuperOwner || isOwner || (isAdmin && !channel.data);
  const canEditManagedAccounts = isSuperOwner || isOwner;
  const saveFromDialog = () => {
    const value = url.trim();
    if (!value) return;
    save.mutate({ channelUrl: value, enabled: true });
  };
  return <>
    <Dialog open={setupDialogOpen} onOpenChange={setSetupDialogOpen}>
      <DialogContent className="hud-panel border-cyan-300/25 bg-[#090910] text-slate-100 sm:max-w-lg">
        <DialogHeader>
          <DialogTitle className="neon-heading text-2xl">ADD TELEGRAM LINK</DialogTitle>
          <DialogDescription className="text-slate-400">Add a Public Channel Link or Private Invite Link. The link is validated and saved securely for this account.</DialogDescription>
        </DialogHeader>
        <div className="space-y-3 py-3">
          <label className="field-label">Telegram link<Input value={url} maxLength={256} onChange={e => setUrl(e.target.value)} className="cyber-input mt-2" placeholder="https://t.me/channel or https://t.me/+invite_hash" autoFocus /></label>
          <p className="text-xs leading-5 text-slate-500">Accepted examples: https://t.me/channel, https://t.me/+invite_hash, or https://t.me/joinchat/invite_hash.</p>
        </div>
        <DialogFooter><Button className="cyber-button w-full sm:w-auto" disabled={save.isPending || !url.trim()} onClick={saveFromDialog}>{save.isPending ? "SAVING…" : "SAVE TELEGRAM LINK"}</Button></DialogFooter>
      </DialogContent>
    </Dialog>
    <section className="mt-6"><Card className="hud-panel"><CardContent className="p-6">
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div><p className="eyebrow">OPTIONAL EXTENSION // TELEGRAM</p><h2 className="section-title">Channel redirect</h2><p className="mt-2 text-xs leading-5 text-slate-500">This removable extension is independent from license validation and System Announcement. Only a confirmed announcement OK action can open the mapped channel.</p></div>
      {isSuperOwner && <Switch checked={enabled} onCheckedChange={value => { setEnabled(value); update.mutate({ enabled: value }); }} aria-label="Enable Telegram extension" />}
    </div>
    {enabled && setupState.data?.needsSetup && <div className="mt-5 rounded border border-amber-300/30 bg-amber-300/5 p-3 text-xs leading-5 text-amber-100">Telegram setup is required for this account. Save a valid HTTPS Telegram link—Public Channel Link or Private Invite Link—to dismiss this prompt on future logins.</div>}
    {enabled && canEditOwnChannel && <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-end"><label className="field-label flex-1">your Telegram channel URL (Public Channel Link or Private Invite Link)<Input value={url} maxLength={256} onChange={e => setUrl(e.target.value)} className="cyber-input mt-2" placeholder="https://t.me/channel or https://t.me/+invite_hash" /></label><Button className="cyber-button" disabled={save.isPending || !url.trim()} onClick={() => save.mutate({ channelUrl: url.trim(), enabled: true })}>{save.isPending ? "SAVING…" : "SAVE CHANNEL"}</Button></div>}
    {enabled && isAdmin && channel.data && <p className="mt-5 rounded border border-cyan-300/10 bg-cyan-300/5 p-3 text-xs leading-5 text-slate-400">Your Telegram link is saved. Only the Owner who referred your Admin account, or the Super Owner, can edit it.</p>}
    {canEditManagedAccounts && enabled && <div className="mt-5 border-t border-cyan-300/10 pt-4"><p className="field-label">{isSuperOwner ? "mapped accounts" : "Admins you referred"}</p><div className="mt-2 space-y-3">{channels.data?.slice(0, 12).map(item => <div className="rounded border border-cyan-300/10 p-3 text-xs" key={item.channel.id}><div className="flex flex-wrap items-center justify-between gap-2"><span className="text-cyan-100">{item.username || `account ${item.channel.userId}`} <span className="text-slate-600">({item.role})</span></span><span className="text-slate-500">{item.channel.enabled ? "enabled" : "disabled"}</span></div>{editingUserId === item.channel.userId ? <div className="mt-2 flex flex-col gap-2 sm:flex-row"><Input value={editingUrl} maxLength={256} onChange={e => setEditingUrl(e.target.value)} className="cyber-input" placeholder="https://t.me/channel or https://t.me/+invite_hash" /><Button size="sm" className="cyber-button" disabled={save.isPending || !editingUrl.trim()} onClick={() => save.mutate({ userId: item.channel.userId, channelUrl: editingUrl.trim(), enabled: item.channel.enabled })}>{save.isPending ? "SAVING…" : "SAVE"}</Button><Button size="sm" variant="outline" onClick={() => { setEditingUserId(null); setEditingUrl(""); }}>CANCEL</Button></div> : <div className="mt-2 flex flex-wrap items-center justify-between gap-2"><span className="break-all text-slate-500">{item.channel.channelUrl}</span><Button size="sm" variant="outline" onClick={() => { setEditingUserId(item.channel.userId); setEditingUrl(item.channel.channelUrl); }}>EDIT LINK</Button></div>}</div>)}{!channels.data?.length && <p className="text-xs text-slate-500">{isSuperOwner ? "No Telegram channel mappings yet." : "No referred Admin Telegram links yet."}</p>}</div></div>}
    {isSuperOwner && enabled && <div className="mt-5 border-t border-cyan-300/10 pt-4"><p className="field-label">scope review</p><p className="mt-1 text-xs leading-5 text-slate-500">Only confirmed referral provenance can create an Owner → Admin Telegram scope. Uncertain accounts remain unmapped.</p><div className="mt-3 space-y-2">{scopeCandidates.data?.slice(0, 12).map(candidate => <div className="flex flex-wrap items-center justify-between gap-2 rounded border border-cyan-300/10 p-2 text-xs" key={candidate.referralId}><span className="text-cyan-100">{candidate.accountUsername || `account ${candidate.accountUserId ?? "pending"}`}</span><span className="text-slate-500">{candidate.status}{candidate.accountUserId ? ` · creator ${candidate.referralCreatorId}` : " · not redeemed"}</span>{candidate.accountUserId && <Button size="sm" variant="outline" disabled={setScope.isPending} onClick={() => setScope.mutate({ accountUserId: candidate.accountUserId!, ownerUserId: candidate.referralCreatorId, sourceReferralId: candidate.referralId, status: "active" })}>CONFIRM SCOPE</Button>}</div>)}{!scopeCandidates.data?.length && <p className="text-xs text-slate-500">No referral-based Admin scopes need review.</p>}</div></div>}
  </CardContent></Card></section>
  </>;
}
