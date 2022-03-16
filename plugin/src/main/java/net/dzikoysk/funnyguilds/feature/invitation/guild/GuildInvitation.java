package net.dzikoysk.funnyguilds.feature.invitation.guild;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

public class GuildInvitation extends Invitation<Guild, Player> {

    GuildInvitation(UUID from, UUID to) {
        super(from, to);
    }

    public Option<Guild> wrapFrom(GuildManager guildManager) {
        return guildManager.findByUuid(from);
    }

    public Option<Player> wrapTo(Server server) {
        return Option.of(server.getPlayer(to));
    }

}
