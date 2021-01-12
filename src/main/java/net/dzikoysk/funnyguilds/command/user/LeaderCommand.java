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
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.*;

@FunnyComponent
public final class LeaderCommand {

    @FunnyCommand(
        name = "${user.leader.name}",
        description = "${user.leader.description}",
        aliases = "${user.leader.aliases}",
        permission = "funnyguilds.leader",
        completer = "members:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, Player player, @IsOwner User owner, Guild guild, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);

        User leaderUser = UserValidation.requireUserByName(args[0]);
        when (owner.equals(leaderUser), messages.leaderMustBeDifferent);
        when (!guild.getMembers().contains(leaderUser), messages.generalIsNotMember);

        if (!SimpleEventHandler.handle(new GuildMemberLeaderEvent(EventCause.USER, owner, guild, leaderUser))) {
            return;
        }
        
        Player leaderPlayer = leaderUser.getPlayer();
        guild.setOwner(leaderUser);
        player.sendMessage(messages.leaderSet);

        if (leaderPlayer != null) {
            leaderPlayer.sendMessage(messages.leaderOwner);
        }
    }

}
