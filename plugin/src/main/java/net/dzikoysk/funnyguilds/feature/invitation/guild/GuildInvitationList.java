package net.dzikoysk.funnyguilds.feature.invitation.guild;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.feature.invitation.InvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

public class GuildInvitationList implements InvitationList<GuildInvitation> {

    private final Set<GuildInvitation> invitations = new HashSet<>();
    private final UserManager userManager;
    private final GuildManager guildManager;

    public GuildInvitationList(UserManager userManager, GuildManager guildManager) {
        this.userManager = userManager;
        this.guildManager = guildManager;
    }

    @Override
    public Set<GuildInvitation> getInvitations() {
        return ImmutableSet.copyOf(this.invitations);
    }

    public Set<GuildInvitation> getInvitationsFrom(Guild from) {
        return this.getInvitationsFrom(from.getUUID());
    }

    public Set<GuildInvitation> getInvitationsFor(User to) {
        return this.getInvitationsFor(to.getUUID());
    }

    public boolean hasInvitation(Guild from, User to) {
        return this.hasInvitation(from.getUUID(), to.getUUID());
    }

    public Set<String> getInvitationGuildNames(UUID to) {
        return this.getInvitationsFor(to).stream()
                .map(GuildInvitation::getFrom)
                .map(Guild::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getInvitationGuildNames(User to) {
        return this.getInvitationGuildNames(to.getUUID());
    }

    public Set<String> getInvitationGuildTags(UUID to) {
        return this.getInvitationsFor(to).stream()
                .map(GuildInvitation::getFrom)
                .map(Guild::getTag)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getInvitationGuildTags(User to) {
        return this.getInvitationGuildTags(to.getUUID());
    }

    @Override
    public void createInvitation(UUID from, UUID to) {
        Optional<Guild> fromOption = this.guildManager.findByUuid(from);
        Optional<User> toOption = this.userManager.findByUuid(to);

        if (fromOption.isEmpty() || toOption.isEmpty()) {
            return;
        }

        this.createInvitation(fromOption.orElseThrow(), toOption.orElseThrow());
    }

    public void createInvitation(Guild from, User to) {
        this.invitations.add(new GuildInvitation(from, to));
    }

    @Override
    public void expireInvitation(UUID from, UUID to) {
        this.getInvitationsFor(to).stream()
                .filter(invitation -> invitation.getToUUID().equals(to))
                .forEach(this.invitations::remove);
    }

    public void expireInvitation(Guild from, User to) {
        this.expireInvitation(from.getUUID(), to.getUUID());
    }

}
