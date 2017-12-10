package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
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
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        Guild guild = user.getGuild();

        if (guild.getAllies() == null || guild.getAllies().isEmpty()) {
            player.sendMessage(messages.breakHasNotAllies);
            return;
        }

        if (args.length < 1) {
            List<String> list = messages.breakAlliesList;
            String iss = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), true);
            
            for (String msg : list) {
                player.sendMessage(msg.replace("{GUILDS}", iss));
            }
            
            return;
        }

        String tag = args[0];
        Guild oppositeGuild = GuildUtils.byTag(tag);

        if (oppositeGuild == null) {
            player.sendMessage(messages.generalGuildNotExists.replace("{TAG}", tag));
            return;
        }

        if (!guild.getAllies().contains(oppositeGuild)) {
            player.sendMessage(messages.breakAllyExists.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", tag));
        }

        guild.removeAlly(oppositeGuild);
        oppositeGuild.removeAlly(guild);

        for (User u : guild.getMembers()) {
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, oppositeGuild);
        }
        
        for (User u : oppositeGuild.getMembers()) {
            IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
        }

        Player owner = oppositeGuild.getOwner().getPlayer();

        if (owner !=null) {
            owner.sendMessage(messages.allyIDone.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }

        player.sendMessage(messages.breakDone.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", oppositeGuild.getTag()));
    }

}
