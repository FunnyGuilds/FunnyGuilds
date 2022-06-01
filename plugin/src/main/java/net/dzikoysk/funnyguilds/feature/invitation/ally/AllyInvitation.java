package net.dzikoysk.funnyguilds.feature.invitation.ally;

import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.guild.Guild;

public class AllyInvitation extends Invitation<Guild, Guild> {

    AllyInvitation(Guild from, Guild to) {
        super(from, to);
    }

    @Override
    public UUID getFromUUID() {
        return this.from.getUUID();
    }

    @Override
    public UUID getToUUID() {
        return this.to.getUUID();
    }

}
