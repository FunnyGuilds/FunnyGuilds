package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.nametag.NameTagGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BreakCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.break.name}",
            description = "${user.break.description}",
            aliases = "${user.break.aliases}",
            permission = "funnyguilds.break",
            completer = "allies:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(!guild.hasAllies(), this.messages.breakHasNotAllies);

        if (args.length < 1) {
            FunnyFormatter formatter = FunnyFormatter.of("{GUILDS}", FunnyStringUtils.join(Entity.names(guild.getAllies()), true));
            this.messages.breakAlliesList.forEach(line -> owner.sendMessage(formatter.format(line)));
            return;
        }

        Guild oppositeGuild = GuildValidation.requireGuildByTag(args[0]);
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", oppositeGuild.getName())
                .register("{TAG}", guild.getTag());

        when(!guild.isAlly(oppositeGuild), () -> formatter.format(this.messages.breakAllyExists));

        if (!SimpleEventHandler.handle(new GuildBreakAllyEvent(EventCause.USER, owner, guild, oppositeGuild))) {
            return;
        }

        FunnyFormatter breakFormatter = new FunnyFormatter()
                .register("{GUILD}", oppositeGuild.getName())
                .register("{TAG}", oppositeGuild.getTag());

        FunnyFormatter breakIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        guild.removeAlly(oppositeGuild);
        oppositeGuild.removeAlly(guild);

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        guild.getMembers().forEach(member -> {
            taskBuilder.delegate(new NameTagGlobalUpdateUserRequest(this.plugin, member));
        });

        oppositeGuild.getMembers().forEach(member -> {
            taskBuilder.delegate(new NameTagGlobalUpdateUserRequest(this.plugin, member));
        });

        ConcurrencyTask task = taskBuilder.build();
        this.concurrencyManager.postTask(task);

        owner.sendMessage(breakFormatter.format(this.messages.breakDone));
        oppositeGuild.getOwner().sendMessage(breakIFormatter.format(this.messages.breakIDone));
    }

}
