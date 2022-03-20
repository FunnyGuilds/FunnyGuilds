package net.dzikoysk.funnyguilds.feature.invitation.guild;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class GuildInvitation extends Invitation<Guild, User> {

    GuildInvitation(Guild from, User to) {
        super(from, to);
    }

    @Override
    public UUID getFromUUID() {
        return from.getUUID();
    }

    @Override
    public UUID getToUUID() {
        return to.getUUID();
    }

}
