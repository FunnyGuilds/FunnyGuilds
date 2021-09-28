package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BreakCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.break.name}",
        description = "${user.break.description}",
        aliases = "${user.break.aliases}",
        permission = "funnyguilds.break",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild, String[] args) {
        when (!guild.hasAllies(), this.messageConfig.breakHasNotAllies);

        if (args.length < 1) {
            List<String> list = this.messageConfig.breakAlliesList;
            String iss = ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), true);
            
            for (String msg : list) {
                player.sendMessage(msg.replace("{GUILDS}", iss));
            }
            
            return;
        }

        Guild oppositeGuild = GuildValidation.requireGuildByTag(args[0]);
        when (!guild.getAllies().contains(oppositeGuild), () -> this.messageConfig.breakAllyExists.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", guild.getTag()));

        if (!SimpleEventHandler.handle(new GuildBreakAllyEvent(EventCause.USER, user, guild, oppositeGuild))) {
            return;
        }

        Player owner = oppositeGuild.getOwner().getPlayer();

        if (owner != null) {
            owner.sendMessage(this.messageConfig.breakIDone.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }

        guild.removeAlly(oppositeGuild);
        oppositeGuild.removeAlly(guild);
        
        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        for (User member : guild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, oppositeGuild));
        }
        
        for (User member : oppositeGuild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
        }

        ConcurrencyTask task = taskBuilder.build();
        this.concurrencyManager.postTask(task);

        player.sendMessage(this.messageConfig.breakDone.replace("{GUILD}", oppositeGuild.getName()).replace("{TAG}", oppositeGuild.getTag()));
    }

}
