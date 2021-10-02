package net.dzikoysk.funnyguilds.data.util;

import com.google.common.collect.ImmutableList;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class InvitationList {

    private static final Set<Invitation> INVITATION_LIST = new HashSet<>();

    public static void createInvitation(Guild from, Player to) {
        Invitation invitation = new Invitation(from, to);
        INVITATION_LIST.add(invitation);
    }

    public static void createInvitation(Guild from, Guild to) {
        Invitation invitation = new Invitation(from, to);
        INVITATION_LIST.add(invitation);
    }

    public static void createInvitation(Guild from, UUID player) {
        Invitation invitation = new Invitation(from, player);
        INVITATION_LIST.add(invitation);
    }


    public static void expireInvitation(Guild from, Player to) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(to.getUniqueId())) {
                INVITATION_LIST.remove(invitation);
                break;
            }
        }
    }
    public static void expireInvitation(Guild from, User to) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(to.getUUID())) {
                INVITATION_LIST.remove(invitation);
                break;
            }
        }
    }

    public static void expireInvitation(Guild from, Guild to) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToAlly() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(to.getUUID())) {
                INVITATION_LIST.remove(invitation);
                break;
            }
        }
    }

    public static boolean hasInvitation(Player player) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFor().equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasInvitationFrom(Player player, Guild from) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasInvitationFrom(User player, Guild from) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasInvitationFrom(Guild guild, Guild from) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToAlly() && invitation.getFrom().equals(from.getUUID()) && invitation.getFor().equals(guild.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasInvitation(Guild guild) {
        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToAlly() && invitation.getFor().equals(guild.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public static List<Invitation> getInvitations() {
        return ImmutableList.copyOf(INVITATION_LIST);
    }

    public static List<Invitation> getInvitationsFor(Player player) {
        return INVITATION_LIST
                .stream()
                .filter(inv -> inv.isToGuild() && inv.getFor().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public static List<Invitation> getInvitationsFrom(Guild guild) {
        return INVITATION_LIST
                .stream()
                .filter(inv -> inv.getFrom().equals(guild.getUUID()))
                .collect(Collectors.toList());
    }

    public static List<Invitation> getInvitationsFor(Guild guild) {
        return INVITATION_LIST
                .stream()
                .filter(inv -> inv.isToAlly() && inv.getFor().equals(guild.getUUID()))
                .collect(Collectors.toList());
    }

    public static List<String> getInvitationGuildNames(Player player) {
        List<String> guildNames = new ArrayList<>();

        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToGuild() && invitation.getFor().equals(player.getUniqueId())) {
                guildNames.add(invitation.wrapFrom().getName());
            }
        }

        return guildNames;
    }

    public static List<String> getInvitationGuildNames(Guild guild) {
        List<String> guildNames = new ArrayList<>();

        for (Invitation invitation : INVITATION_LIST) {
            if (invitation.isToAlly() && invitation.getFor().equals(guild.getUUID())) {
                guildNames.add(invitation.wrapFrom().getName());
            }
        }

        return guildNames;
    }

    public static final class Invitation {

        private final UUID from;
        private final UUID to;
        private final InvitationType type;

        private Invitation(Guild from, UUID player) {
            this.from = from.getUUID();
            this.to = player;
            this.type = InvitationType.TO_GUILD;
        }

        private Invitation(Guild from, Player to) {
            this.from = from.getUUID();
            this.to = to.getUniqueId();
            this.type = InvitationType.TO_GUILD;
        }

        private Invitation(Guild from, Guild to) {
            this.from = from.getUUID();
            this.to = to.getUUID();
            this.type = InvitationType.TO_ALLY;
        }

        public boolean isToAlly() {
            return type == InvitationType.TO_ALLY;
        }

        public boolean isToGuild() {
            return type == InvitationType.TO_GUILD;
        }

        public UUID getFrom() {
            return from;
        }

        public UUID getFor() {
            return to;
        }

        @Nullable
        private Guild wrapFrom() {
            return FunnyGuilds.getInstance().getGuildManager().findByUuid(from).getOrNull();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Invitation that = (Invitation) o;
            return Objects.equals(from, that.from) && Objects.equals(to, that.to) && type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, type);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("from", from).append("to", to).append("type", type).toString();
        }
    }

    private enum InvitationType {
        TO_GUILD,
        TO_ALLY
    }
}
