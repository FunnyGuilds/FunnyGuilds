package net.dzikoysk.funnyguilds.feature.invitation.guild;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.feature.invitation.InvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import panda.std.Option;
import panda.std.stream.PandaStream;

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
        return PandaStream.of(this.getInvitationsFor(to))
                .map(GuildInvitation::getFrom)
                .map(Guild::getName)
                .collect(Collectors.toSet());

    }

    public Set<String> getInvitationGuildNames(User to) {
        return this.getInvitationGuildNames(to.getUUID());
    }

    public Set<String> getInvitationGuildTags(UUID to) {
        return PandaStream.of(this.getInvitationsFor(to))
                .map(GuildInvitation::getFrom)
                .map(Guild::getTag)
                .collect(Collectors.toSet());
    }

    public Set<String> getInvitationGuildTags(User to) {
        return this.getInvitationGuildTags(to.getUUID());
    }

    @Override
    public void createInvitation(UUID from, UUID to) {
        Option<Guild> fromOption = guildManager.findByUuid(from);
        Option<User> toOption = userManager.findByUuid(to);
        if (fromOption.isEmpty() || toOption.isEmpty()) {
            return;
        }
        this.createInvitation(fromOption.get(), toOption.get());
    }

    public void createInvitation(Guild from, User to) {
        invitations.add(new GuildInvitation(from, to));
    }

    @Override
    public void expireInvitation(UUID from, UUID to) {
        PandaStream.of(this.getInvitationsFrom(from))
                .filter(invitation -> invitation.getToUUID().equals(to))
                .forEach(this.invitations::remove);
    }

    public void expireInvitation(Guild from, User to) {
        this.expireInvitation(from.getUUID(), to.getUUID());
    }

}
