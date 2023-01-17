package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;
import panda.std.stream.PandaStream;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand extends AbstractFunnyCommand {

    @Inject
    public GuildInvitationList guildInvitationList;

    @FunnyCommand(
            name = "${user.invite.name}",
            description = "${user.invite.description}",
            aliases = "${user.invite.aliases}",
            permission = "funnyguilds.invite",
            completer = "invite-players:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@CanManage User deputy, Player sender, Guild guild, String[] args) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{AMOUNT}", this.config.maxMembersInGuild)
                .register("{OWNER}", deputy.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        when(args.length < 1, config -> config.generalNoNickGiven);
        when(guild.getMembers().size() >= this.config.maxMembersInGuild, config -> config.inviteAmount, formatter);

        boolean checkArgument = this.config.inviteCommandAllArgumentIgnoreCase
                ? args[0].equalsIgnoreCase(this.config.inviteCommandAllArgument)
                : args[0].equals(this.config.inviteCommandAllArgument);

        if (checkArgument) {
            double range = args.length >= 2
                    ? Option.attempt(NumberFormatException.class, () -> Double.parseDouble(args[1]))
                        .orThrow(() -> new InternalValidationException(config -> config.inviteAllArgumentIsNotNumber))
                    : this.config.inviteCommandAllDefaultRange;

            when(range > this.config.inviteCommandAllMaxRange, config -> config.inviteRangeToBig, FunnyFormatter.of("{MAX_RANGE}", this.config.inviteCommandAllMaxRange));

            List<Player> nearbyPlayers = PandaStream.of(Bukkit.getServer().getOnlinePlayers())
                    .filter(player -> range >= player.getLocation().distance(sender.getLocation()))
                    .filterNot(player -> player.equals(sender))
                    .collect(Collectors.toList());

            when(guild.getMembers().size() + nearbyPlayers.size() > this.config.maxMembersInGuild, config -> config.inviteAmount, formatter);
            when(nearbyPlayers.isEmpty(), config -> config.inviteNoOneIsNearby);

            this.messageService.getMessage(config -> config.inviteAllCommand)
                    .with(FunnyFormatter.of("{RANGE}", range))
                    .receiver(sender)
                    .send();

            PandaStream.of(nearbyPlayers)
                    .mapOpt(this.userManager::findByPlayer)
                    .filterNot(User::isVanished)
                    .forEach(it -> this.inviteUserToGuild(deputy, guild, it, formatter));

            return;
        }

        this.inviteUserToGuild(deputy, guild, UserValidation.requireUserByName(args[0]), formatter);
    }

    private void inviteUserToGuild(User deputy, Guild guild, User invitedUser, FunnyFormatter formatter) {
        if (this.guildInvitationList.hasInvitation(guild, invitedUser)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, deputy, guild, invitedUser))) {
                return;
            }

            this.messageService.getMessage(config -> config.inviteCancelled)
                    .with("{PLAYER}", invitedUser.getName())
                    .receiver(deputy)
                    .send();
            this.messageService.getMessage(config -> config.inviteCancelledToInvited)
                    .with(formatter)
                    .receiver(invitedUser)
                    .send();

            this.guildInvitationList.expireInvitation(guild, invitedUser);
            return;
        }

        when(invitedUser.hasGuild(), config -> config.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, deputy, guild, invitedUser))) {
            return;
        }

        this.guildInvitationList.createInvitation(guild, invitedUser);

        this.messageService.getMessage(config -> config.inviteToOwner)
                .with(FunnyFormatter.of("{PLAYER}", invitedUser.getName()))
                .receiver(deputy)
                .send();
        this.messageService.getMessage(config -> config.inviteToInvited)
                .with(formatter)
                .receiver(invitedUser)
                .send();
    }

}
