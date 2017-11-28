package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
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

        User iu = User.get(args[0]);
        Player ip = iu.getPlayer();

        if (InvitationList.hasInvitationFrom(iu, guild)) {
            InvitationList.expireInvitation(guild, iu);
            p.sendMessage(m.inviteCancelled);
            
            if (ip != null) {
                ip.sendMessage(m.inviteCancelledToInvited.replace("{OWNER}", p.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
            }
            
            return;
        }

        if (ip == null) {
            p.sendMessage(m.invitePlayerExists);
            return;
        }

        if (iu.hasGuild()) {
            p.sendMessage(m.inviteHasGuild);
            return;
        }

        InvitationList.createInvitation(guild, ip);
        p.sendMessage(m.inviteToOwner.replace("{PLAYER}", ip.getName()));
        ip.sendMessage(m.inviteToInvited.replace("{OWNER}", p.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}
