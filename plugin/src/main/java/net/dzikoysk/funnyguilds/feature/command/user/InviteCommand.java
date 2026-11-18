package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.Optionals;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.invite.name}",
            description = "${user.invite.description}",
            aliases = "${user.invite.aliases}",
            permission = "funnyguilds.invite",
            completer = "invite-players:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@HasGuildPermission(GuildCommandPermission.INVITE) User deputy, Player sender, Guild guild, String[] args) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{AMOUNT}", this.config.maxMembersInGuild)
                .register("{OWNER}", deputy.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        when(args.length < 1, config -> config.generalNoNickGiven);

        boolean checkArgument = this.config.inviteCommandAllArgumentIgnoreCase
                ? args[0].equalsIgnoreCase(this.config.inviteCommandAllArgument)
                : args[0].equals(this.config.inviteCommandAllArgument);

        if (checkArgument) {
            when(guild.getMembers().size() >= this.config.maxMembersInGuild, config -> config.inviteAmount, formatter);
            double range = args.length >= 2
                    ? Optionals.attempt(NumberFormatException.class, () -> Double.parseDouble(args[1]))
                        .orElseThrow(() -> new InternalValidationException(config -> config.inviteAllArgumentIsNotNumber))
                    : this.config.inviteCommandAllDefaultRange;

            when(range > this.config.inviteCommandAllMaxRange, config -> config.inviteRangeToBig, Replacement.string("{MAX_RANGE}", this.config.inviteCommandAllMaxRange));

            List<Player> nearbyPlayers = Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(player -> player.getWorld().equals(sender.getWorld()))
                    .filter(player -> range >= player.getLocation().distance(sender.getLocation()))
                    .filter(player -> !player.equals(sender))
                    .collect(Collectors.toList());

            when(guild.getMembers().size() + nearbyPlayers.size() > this.config.maxMembersInGuild, config -> config.inviteAmount, formatter);
            when(nearbyPlayers.isEmpty(), config -> config.inviteNoOneIsNearby);

            this.messageService.getMessage(config -> config.inviteAllCommand)
                    .receiver(sender)
                    .with("{RANGE}", range)
                    .send();

            nearbyPlayers.stream()
                    .flatMap(player -> this.userManager.findByPlayer(player).stream())
                    .filter(user -> !user.isVanished())
                    .forEach(it -> this.plugin.getGuildMembershipService().toggleInvitation(deputy, guild, it));

            return;
        }

        this.plugin.getGuildMembershipService().toggleInvitation(deputy, guild, UserValidation.requireUserByName(args[0]));
    }

}
