package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcBreak implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User lp = User.get(p);

        if (!lp.hasGuild()) {
            p.sendMessage(m.breakHasNotGuild);
            return;
        }

        if (!lp.isOwner()) {
            p.sendMessage(m.breakIsNotOwner);
            return;
        }

        Guild guild = lp.getGuild();

        if (guild.getAllies() == null || guild.getAllies().isEmpty()) {
            p.sendMessage(m.breakHasNotAllies);
            return;
        }

        if (args.length < 1) {
            List<String> list = m.breakAlliesList;
            String[] msgs = list.toArray(new String[list.size()]);
            String iss = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), true);
            for (String msg : msgs) {
                p.sendMessage(msg.replace("{GUILDS}", iss));
            }
            return;
        }

        String tag = args[0];

        if (!GuildUtils.tagExists(tag)) {
            p.sendMessage(m.breakGuildExists.replace("{TAG}", tag));
            return;
        }

        Guild tb = GuildUtils.byTag(tag);

        if (!guild.getAllies().contains(tb)) {
            p.sendMessage(m.breakAllyExists.replace("{GUILD}", tb.getName()).replace("{TAG}", tag));
        }

        guild.removeAlly(tb);
        tb.removeAlly(guild);

        for (User u : guild.getMembers()) {
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, tb);
        }
        for (User u : tb.getMembers()) {
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
        }

        p.sendMessage(m.breakDone.replace("{GUILD}", tb.getName()).replace("{TAG}", tb.getTag()));

        OfflineUser of = tb.getOwner().getOfflineUser();
        if (of.isOnline()) {
            of.getPlayer().sendMessage(m.allyIDone.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }
    }

}
