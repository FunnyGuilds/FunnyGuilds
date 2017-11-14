package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User lp = User.get(p);

        if (!lp.hasGuild()) {
            p.sendMessage(m.inviteHasNotGuild);
            return;
        }

        if (!lp.isOwner() && !lp.isDeputy()) {
            p.sendMessage(m.inviteIsNotOwner);
            return;
        }

        if (args.length < 1) {
            p.sendMessage(m.invitePlayer);
            return;
        }
        Guild guild = lp.getGuild();

        if (guild.getMembers().size() >= Settings.getConfig().inviteMembers) {
            p.sendMessage(m.inviteAmount.replace("{AMOUNT}", Integer.toString(Settings.getConfig().inviteMembers)));
            return;
        }

        if (!UserUtils.playedBefore(args[0])) {
            p.sendMessage(StringUtils.colored("&cTen gracz nie byl nigdy na serwerze!"));
            return;
        }

        OfflinePlayer oi = Bukkit.getOfflinePlayer(args[0]);
        User ip = User.get(args[0]);

        if (InvitationList.hasInvitationFrom(ip, guild)) {
            InvitationList.expireInvitation(guild, ip);
            p.sendMessage(m.inviteCancelled);
            if (oi == null || !oi.isOnline()) {
                Player inp = oi.getPlayer();
                inp.sendMessage(m.inviteCancelledToInvited.replace("{OWNER}", p.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
            }
            return;
        }

        if (oi == null || !oi.isOnline()) {
            p.sendMessage(m.invitePlayerExists);
            return;
        }

        Player invited = oi.getPlayer();

        if (ip.hasGuild()) {
            p.sendMessage(m.inviteHasGuild);
            return;
        }

        InvitationList.createInvitation(guild, invited);

        p.sendMessage(m.inviteToOwner.replace("{PLAYER}", invited.getName()));

        invited.sendMessage(m.inviteToInvited.replace("{OWNER}", p.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }

}
