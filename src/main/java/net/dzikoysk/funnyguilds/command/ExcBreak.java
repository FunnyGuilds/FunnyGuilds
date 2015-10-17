package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.BasicUtils;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcBreak implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) s;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("breakHasNotGuild"));
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.getMessage("breakIsNotOwner"));
            return;
        }

        Guild guild = user.getGuild();

        if (guild.getAllies() == null || guild.getAllies().isEmpty()) {
            player.sendMessage(messages.getMessage("breakHasNotAllies"));
            return;
        }

        if (args.length < 1) {
            List<String> list = messages.getList("breakAlliesList");
            String[] msgs = list.toArray(new String[list.size()]);
            String iss = StringUtils.toString(BasicUtils.getNames(guild.getAllies()), true);
            for (int i = 0; i < msgs.length; i++)
                player.sendMessage(msgs[i]
                        .replace("{GUILDS}", iss));
            return;
        }

        String tag = args[0];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(messages.getMessage("breakGuildExists")
                    .replace("{TAG}", tag));
            return;
        }

        Guild tb = GuildUtils.byTag(tag);

        if (!guild.getAllies().contains(tb)) {
            player.sendMessage(messages.getMessage("breakAllyExists")
                    .replace("{GUILD}", tb.getName())
                    .replace("{TAG}", tag));
            return;
        }

        guild.removeAlly(tb);
        tb.removeAlly(guild);

        for (User u : guild.getMembers())
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, tb);
        for (User u : tb.getMembers())
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);

        player.sendMessage(messages.getMessage("breakDone")
                .replace("{GUILD}", tb.getName())
                .replace("{TAG}", tb.getTag()));

        OfflineUser offline = tb.getOwner().getOfflineUser();
        if (offline.isOnline())
            offline.getPlayer().sendMessage(messages.getMessage("allyIDone")
                    .replace("{GUILD}", guild.getName())
                    .replace("{TAG}", guild.getTag()));
    }
}
