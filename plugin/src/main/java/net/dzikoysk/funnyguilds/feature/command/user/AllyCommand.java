package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Set;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.panda_lang.utilities.inject.annotations.Inject;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class AllyCommand extends AbstractFunnyCommand {

    @Inject
    public AllyInvitationList allyInvitationList;

    @FunnyCommand(
            name = "${user.ally.name}",
            description = "${user.ally.description}",
            aliases = "${user.ally.aliases}",
            permission = "funnyguilds.ally",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@HasGuildPermission(GuildCommandPermission.ALLY) User owner, Guild guild, String[] args) {
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when(invitations.isEmpty(), config -> config.allyHasNotInvitation);
            String guildNames = FunnyStringUtils.join(this.allyInvitationList.getInvitationGuildNames(guild), true);

            this.messageService.getMessage(config -> config.allyInvitationList)
                    .receiver(owner)
                    .with("{GUILDS}", guildNames)
                    .send();

            return;
        }

        this.plugin.getGuildDiplomacyService().handleAllyCommand(owner, guild, GuildValidation.requireGuildByTag(args[0]));
    }

}
