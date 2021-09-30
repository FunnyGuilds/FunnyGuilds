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
import org.bukkit.entity.Player;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class DeputyCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.deputy.name}",
        description = "${user.deputy.description}",
        aliases = "${user.deputy.aliases}",
        permission = "funnyguilds.deputy",
        completer = "online-players:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, @IsOwner User owner, Guild guild, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);

        User deputyUser = UserValidation.requireUserByName(args[0]);
        when (owner.equals(deputyUser), messages.deputyMustBeDifferent);
        when (!guild.getMembers().contains(deputyUser), messages.generalIsNotMember);

        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(EventCause.USER, owner, guild, deputyUser))) {
            return;
        }

        Option<Player> deputyPlayer = Option.of(deputyUser.getPlayer());

        if (deputyUser.isDeputy()) {
            guild.removeDeputy(deputyUser);
            player.sendMessage(messages.deputyRemove);
            deputyPlayer.peek(value -> value.sendMessage(messages.deputyMember));
            return;
        }

        guild.addDeputy(deputyUser);
        player.sendMessage(messages.deputySet);
        deputyPlayer.peek(value -> value.sendMessage(messages.deputyOwner));
    }

}
