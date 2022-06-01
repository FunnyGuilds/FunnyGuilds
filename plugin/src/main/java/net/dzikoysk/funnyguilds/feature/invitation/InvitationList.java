package net.dzikoysk.funnyguilds.feature.invitation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import panda.std.stream.PandaStream;

public interface InvitationList<T extends Invitation<?, ?>> {

    Set<T> getInvitations();

    default Set<T> getInvitationsFrom(UUID from) {
        try (PandaStream<T> invitations = PandaStream.of(this.getInvitations())) {
            return invitations.filter(invitation -> invitation.getFromUUID().equals(from)).collect(Collectors.toSet());
        }
    }

    default Set<T> getInvitationsFor(UUID to) {
        try (PandaStream<T> invitations = PandaStream.of(this.getInvitations())) {
            return invitations.filter(invitation -> invitation.getToUUID().equals(to)).collect(Collectors.toSet());
        }
    }

    default boolean hasInvitation(UUID from, UUID to) {
        try (PandaStream<T> invitations = PandaStream.of(this.getInvitationsFrom(from))) {
            return invitations.find(invitation -> invitation.getToUUID().equals(to)).isPresent();
        }
    }

    default boolean hasInvitationFor(UUID to) {
        return !this.getInvitationsFor(to).isEmpty();
    }

    void createInvitation(UUID from, UUID to);

    void expireInvitation(UUID from, UUID to);

}
