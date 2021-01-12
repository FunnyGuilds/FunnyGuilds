package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.bukkit.entity.Player;

import java.util.List;

public final class BreakCommand {

    @FunnyCommand(
        name = "${user.break.name}",
        description = "${user.break.description}",
        aliases = "${user.break.aliases}",
        permission = "funnyguilds.break",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, Player player, User user, String[] args) {
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
            String iss = ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), true);
            
            for (String msg : list) {
                player.sendMessage(msg.replace("{GUILDS}", iss));
            }
            
            return;
        }

        String tag = args[0];
        Guild oppositeGuild = GuildUtils.getByTag(tag);

        if (oppositeGuild == null) {
            player.sendMessage(messages.generalGuildNotExists.replace("{TAG}", tag));
            return;
        }

        if (!guild.getAllies().contains(oppositeGuild)) {
            player.sendMessage(messages.breakAllyExists.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", tag));
        }

        if (!SimpleEventHandler.handle(new GuildBreakAllyEvent(EventCause.USER, user, guild, oppositeGuild))) {
            return;
        }

        Player owner = oppositeGuild.getOwner().getPlayer();

        if (owner != null) {
            owner.sendMessage(messages.breakIDone.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }

        guild.removeAlly(oppositeGuild);
        oppositeGuild.removeAlly(guild);

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        for (User member : guild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, oppositeGuild));
        }
        
        for (User member : oppositeGuild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
        }

        ConcurrencyTask task = taskBuilder.build();
        concurrencyManager.postTask(task);

        player.sendMessage(messages.breakDone.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", oppositeGuild.getTag()));
    }

}
