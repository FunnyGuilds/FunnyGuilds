package net.dzikoysk.funnyguilds.feature.invitation.ally;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.feature.invitation.InvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class AllyInvitationList implements InvitationList<AllyInvitation> {

    private final Set<AllyInvitation> invitations = new HashSet<>();
    private final GuildManager guildManager;

    public AllyInvitationList(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public Set<AllyInvitation> getInvitations() {
        return ImmutableSet.copyOf(this.invitations);
    }

    public Set<AllyInvitation> getInvitationsFrom(Guild from) {
        return this.getInvitationsFrom(from.getUUID());
    }

    public Set<AllyInvitation> getInvitationsFor(Guild to) {
        return this.getInvitationsFor(to.getUUID());
    }

    public boolean hasInvitation(Guild from, Guild to) {
        return this.hasInvitation(from.getUUID(), to.getUUID());
    }

    public Set<String> getInvitationGuildNames(UUID to) {
        try (PandaStream<AllyInvitation> invitations = PandaStream.of(this.getInvitationsFor(to))) {
            return invitations.map(AllyInvitation::getFrom).map(Guild::getName).collect(Collectors.toSet());
        }
    }

    public Set<String> getInvitationGuildNames(Guild to) {
        return this.getInvitationGuildNames(to.getUUID());
    }

    public Set<String> getInvitationGuildTags(UUID to) {
        try (PandaStream<AllyInvitation> invitations = PandaStream.of(this.getInvitationsFor(to))) {
            return invitations.map(AllyInvitation::getFrom).map(Guild::getTag).collect(Collectors.toSet());
        }
    }

    public Set<String> getInvitationGuildTags(Guild to) {
        return this.getInvitationGuildTags(to.getUUID());
    }

    @Override
    public void createInvitation(UUID from, UUID to) {
        Option<Guild> fromOption = this.guildManager.findByUuid(from);
        Option<Guild> toOption = this.guildManager.findByUuid(to);

        if (fromOption.isEmpty() || toOption.isEmpty()) {
            return;
        }

        this.invitations.add(new AllyInvitation(fromOption.get(), toOption.get()));
    }

    public void createInvitation(Guild from, Guild to) {
        this.invitations.add(new AllyInvitation(from, to));
    }

    @Override
    public void expireInvitation(UUID from, UUID to) {
        try (PandaStream<AllyInvitation> invitations = PandaStream.of(this.getInvitationsFrom(from))) {
            invitations.filter(invitation -> invitation.getToUUID().equals(to)).forEach(this.invitations::remove);
        }
    }

    public void expireInvitation(Guild from, Guild to) {
        this.expireInvitation(from.getUUID(), to.getUUID());
    }

}
