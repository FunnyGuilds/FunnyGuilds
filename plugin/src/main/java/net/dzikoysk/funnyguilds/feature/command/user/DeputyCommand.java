package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class DeputyCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.deputy.name}",
            description = "${user.deputy.description}",
            aliases = "${user.deputy.aliases}",
            permission = "funnyguilds.deputy",
            completer = "members:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(args.length < 1, config -> config.generalNoNickGiven);

        User deputyUser = UserValidation.requireUserByName(args[0]);
        when(owner.equals(deputyUser), config -> config.deputyMustBeDifferent);
        when(!guild.isMember(deputyUser), config -> config.generalIsNotMember);

        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(EventCause.USER, owner, guild, deputyUser))) {
            return;
        }

        if (deputyUser.isDeputy()) {
            guild.removeDeputy(deputyUser);
            this.messageService.getMessage(config -> config.deputyRemove)
                    .receiver(owner)
                    .send();
            this.messageService.getMessage(config -> config.deputyMember)
                    .receiver(deputyUser)
                    .send();
            return;
        }

        guild.addDeputy(deputyUser);
        this.messageService.getMessage(config -> config.deputySet)
                .receiver(owner)
                .send();
        this.messageService.getMessage(config -> config.deputyOwner)
                .receiver(deputyUser)
                .send();
    }

}
