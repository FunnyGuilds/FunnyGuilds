package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LeaderCommand {

    @FunnyCommand(
        name = "${user.leader.name}",
        description = "${user.leader.description}",
        aliases = "${user.leader.aliases}",
        permission = "funnyguilds.leader",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.generalNoNickGiven);
            return;
        }

        User leaderUser = User.get(args[0]);

        if (leaderUser == null) {
            player.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        if (owner.equals(leaderUser)) {
            player.sendMessage(messages.leaderMustBeDifferent);
            return;
        }

        Guild guild = owner.getGuild();

        if (!guild.getMembers().contains(leaderUser)) {
            player.sendMessage(messages.generalIsNotMember);
            return;
        }

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
