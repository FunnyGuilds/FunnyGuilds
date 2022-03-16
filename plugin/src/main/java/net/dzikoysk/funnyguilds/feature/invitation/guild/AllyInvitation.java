package net.dzikoysk.funnyguilds.feature.invitation.guild;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import panda.std.Option;

public class AllyInvitation extends Invitation<Guild, Guild> {

    AllyInvitation(UUID from, UUID to) {
        super(from, to);
    }

    public Option<Guild> wrapFrom(GuildManager guildManager) {
        return guildManager.findByUuid(from);
    }

    public Option<Guild> wrapTo(GuildManager guildManager) {
        return guildManager.findByUuid(to);
    }

}
