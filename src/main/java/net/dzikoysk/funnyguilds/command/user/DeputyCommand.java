package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.IsOwner;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.function.Option;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class DeputyCommand {

    @FunnyCommand(
        name = "${user.deputy.name}",
        description = "${user.deputy.description}",
        aliases = "${user.deputy.aliases}",
        permission = "funnyguilds.deputy",
        completer = "online-players:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, Player player, @IsOwner User owner, Guild guild, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);

        User deputyUser = UserValidation.requireUserByName(args[0]);
        when (owner.equals(deputyUser), messages.deputyMustBeDifferent);
        when (!guild.getMembers().contains(deputyUser), messages.generalIsNotMember);
        when (guild.getDeputies().contains(deputyUser), messages.adminAlreadyDeputy);

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
