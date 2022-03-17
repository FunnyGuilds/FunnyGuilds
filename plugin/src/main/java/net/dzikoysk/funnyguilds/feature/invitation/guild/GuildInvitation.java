package net.dzikoysk.funnyguilds.feature.invitation.guild;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import panda.std.Option;

public class GuildInvitation extends Invitation<Guild, User> {

    GuildInvitation(UUID from, UUID to) {
        super(from, to);
    }

    public Option<Guild> wrapFrom(GuildManager guildManager) {
        return guildManager.findByUuid(from);
    }

    public Option<User> wrapTo(UserManager userManager) {
        return userManager.findByUuid(to);
    }

}
