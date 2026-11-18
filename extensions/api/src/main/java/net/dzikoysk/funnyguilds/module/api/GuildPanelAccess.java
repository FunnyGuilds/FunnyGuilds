package net.dzikoysk.funnyguilds.module.api;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionDecision;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;

/** Access to the current guild for a single panel session. */
public interface GuildPanelAccess {

    boolean isActive();
    String tag();
    Map<String, String> overview();
    boolean pvp();
    boolean allyPvpAllowed();
    boolean teleport();
    PanelAudit audit();
    List<Relation> relations();
    List<Relation> allies();
    Optional<Relation> relation(UUID target);
    boolean togglePvp();
    boolean changeRelation(RelationAction action, UUID target);
    Instant validity();
    String date(Instant date);
    String duration(Duration duration);
    List<Duration> periods();
    Renewal renewal(Duration period);
    Expansion expansion(int level);
    boolean owner();
    boolean canManageRoles();
    List<Member> members();
    List<Member> inviteTargets(boolean pending);
    boolean canInvite();
    boolean canKick();
    boolean changeInvitation(UUID target, boolean revoke);
    boolean kick(UUID target);
    void nameInputTimedOut();
    GuildRolePolicy roles();
    List<Permission> permissions();
    UUID selectedRole(UUID member);
    GuildPermissionDecision roleDecision(UUID role, Permission permission);
    GuildPermissionDecision memberDecision(UUID member, Permission permission);
    boolean createRole(UUID role, String name);
    boolean deleteRole(UUID role);
    boolean assignRole(UUID member, UUID role);
    boolean setRolePermission(UUID role, Permission permission, Boolean value);
    boolean setOverride(UUID member, Permission permission, Boolean value);
    boolean setDelegation(Permission permission, boolean delegated);

    record Member(UUID id, String name, boolean owner, boolean deputy) {
    }

    record Permission(String key, String policyKey, boolean delegable) {
    }

    record Relation(UUID id, String name, String tag, boolean ally, boolean incomingInvite, boolean outgoingInvite,
                    boolean incomingWar, boolean outgoingWar, boolean ourPvp, boolean theirPvp) {
    }

    enum RelationAction {
        ALLY_PVP, BREAK, ACCEPT, REVOKE, INVITE, DECLARE, WITHDRAW
    }

    enum Block {
        NONE, WAIT, PERIOD, PRICE, REGIONS, LIMIT, PURCHASED, CURRENT, PREVIOUS, NEAR
    }

    record Payment(String cost, String missing, boolean affordable, boolean economyUnavailable, BooleanSupplier confirm) {
    }

    record Renewal(Instant expiry, Duration waitTime, Block block, Payment payment) {
    }

    record Expansion(int current, int size, int maximum, Block block, Payment payment) {
    }
}
