package net.dzikoysk.funnyguilds.feature.invitation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface InvitationList<T extends Invitation<?, ?>> {

    Set<T> getInvitations();

    default Set<T> getInvitationsFrom(UUID from) {
        return this.getInvitations().stream()
                .filter(invitation -> invitation.getFromUUID().equals(from))
                .collect(Collectors.toUnmodifiableSet());
    }

    default Set<T> getInvitationsFor(UUID to) {
        return this.getInvitations().stream()
                .filter(invitation -> invitation.getToUUID().equals(to))
                .collect(Collectors.toUnmodifiableSet());
    }

    default boolean hasInvitation(UUID from, UUID to) {
        return this.getInvitationsFrom(from).stream()
                .anyMatch(invitation -> invitation.getToUUID().equals(to));
    }

    default boolean hasInvitationFor(UUID to) {
        return !this.getInvitationsFor(to).isEmpty();
    }

    void createInvitation(UUID from, UUID to);

    void expireInvitation(UUID from, UUID to);

}
