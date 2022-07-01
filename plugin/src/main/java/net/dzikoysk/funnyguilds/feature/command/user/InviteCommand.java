package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
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

        when(args.length < 1, this.messages.generalNoNickGiven);
        when(guild.getMembers().size() >= this.config.maxMembersInGuild, formatter.format(this.messages.inviteAmount));

        if (args[0].equals(this.config.inviteCommandAllArgument)) {
            double range = args.length >= 2
                ? Option.attempt(NumberFormatException.class, () -> Double.parseDouble(args[1]))
                        .orThrow(() -> new ValidationException(this.messages.inviteAllArgumentIsNotNumber))
                : this.config.inviteCommandAllDefaultRange;

            when(range > this.config.inviteCommandAllMaxRange, FunnyFormatter.format(this.messages.inviteRangeToBig, "{MAX_RANGE}", this.config.inviteCommandAllMaxRange));

            List<Player> nearbyPlayers = Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(player -> range >= player.getLocation().distance(sender.getLocation()))
                    .filter(player -> player != sender)
                    .collect(Collectors.toList());

            when(guild.getMembers().size() + nearbyPlayers.size() > this.config.maxMembersInGuild, formatter.format(this.messages.inviteAmount));
            when(nearbyPlayers.isEmpty(), this.messages.inviteNoOneIsNearby);

            sender.sendMessage(FunnyFormatter.format(this.messages.inviteAllCommand, "{RANGE}", range));

            PandaStream.of(nearbyPlayers)
                    .mapOpt(this.userManager::findByPlayer)
                    .filter(User::isVanished)
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

            deputy.sendMessage(FunnyFormatter.format(this.messages.inviteCancelled, "{PLAYER}", invitedUser.getName()));
            invitedUser.sendMessage(formatter.format(this.messages.inviteCancelledToInvited));

            this.guildInvitationList.expireInvitation(guild, invitedUser);
            return;
        }

        when(invitedUser.hasGuild(), this.messages.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, deputy, guild, invitedUser))) {
            return;
        }

        this.guildInvitationList.createInvitation(guild, invitedUser);

        deputy.sendMessage(FunnyFormatter.format(this.messages.inviteToOwner, "{PLAYER}", invitedUser.getName()));
        invitedUser.sendMessage(formatter.format(this.messages.inviteToInvited));
    }
}
