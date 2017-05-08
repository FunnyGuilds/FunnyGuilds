package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {

        Messages m = Messages.getInstance();
        Player p = (Player) s;
        User lp = User.get(p);

        if (!lp.hasGuild()) {
            p.sendMessage(m.getMessage("inviteHasNotGuild"));
            return;
        }

        if (!lp.isOwner() && !lp.isDeputy()) {
            p.sendMessage(m.getMessage("inviteIsNotOwner"));
            return;
        }

        if (args.length < 1) {
            p.sendMessage(m.getMessage("invitePlayer"));
            return;
        }
        Guild guild = lp.getGuild();

        if (guild.getMembers().size() >= Settings.getConfig().inviteMembers) {
            p.sendMessage(m.getMessage("inviteAmount")
                    .replace("{AMOUNT}", Integer.toString(Settings.getConfig().inviteMembers)));
            return;
        }

        if (!UserUtils.playedBefore(args[0])) {
            p.sendMessage(StringUtils.colored("&cTen gracz nie byl nigdy na serwerze!"));
            return;
        }

        OfflinePlayer oi = Bukkit.getOfflinePlayer(args[0]);
        User ip = User.get(args[0]);

        if (InvitationsList.get(ip, 0).contains(guild.getTag())) {
            InvitationsList.get(ip, 0).remove(guild.getTag());
            p.sendMessage(m.getMessage("inviteCancelled"));
            if (oi == null || !oi.isOnline()) {
                Player inp = oi.getPlayer();
                inp.sendMessage(
                        m.getMessage("inviteCancelledToInvited")
                                .replace("{OWNER}", p.getName())
                                .replace("{GUILD}", guild.getName())
                                .replace("{TAG}", guild.getTag())
                );
            }
            return;
        }

        if (oi == null || !oi.isOnline()) {
            p.sendMessage(m.getMessage("invitePlayerExists"));
            return;
        }

        Player invited = oi.getPlayer();

        if (ip.hasGuild()) {
            p.sendMessage(m.getMessage("inviteHasGuild"));
            return;
        }

        InvitationsList.get(ip, 0).add(guild.getTag());

        p.sendMessage(
                m.getMessage("inviteToOwner")
                        .replace("{PLAYER}", invited.getName())
        );

        invited.sendMessage(
                m.getMessage("inviteToInvited")
                        .replace("{OWNER}", p.getName())
                        .replace("{GUILD}", guild.getName())
                        .replace("{TAG}", guild.getTag())
        );
    }
}
