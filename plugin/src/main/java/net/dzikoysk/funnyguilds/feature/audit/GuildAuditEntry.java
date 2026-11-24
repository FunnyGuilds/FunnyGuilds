package net.dzikoysk.funnyguilds.feature.audit;

import java.util.Map;
import java.util.UUID;

public record GuildAuditEntry(long time, UUID actorId, String actorName, Type type, Map<String, String> details) {

    public GuildAuditEntry {
        details = Map.copyOf(details);
    }

    public enum Category {
        GUILD, MEMBERS, PERMISSIONS, PVP, RELATIONS, PURCHASES
    }

    public enum Type {
        GUILD_CREATED(Category.GUILD),
        INVITED(Category.MEMBERS),
        INVITATION_REVOKED(Category.MEMBERS),
        JOINED(Category.MEMBERS),
        LEFT(Category.MEMBERS),
        KICKED(Category.MEMBERS),
        LEADER_CHANGED(Category.MEMBERS),
        DEPUTY_ADDED(Category.MEMBERS),
        DEPUTY_REMOVED(Category.MEMBERS),
        PERMISSIONS_CHANGED(Category.PERMISSIONS),
        PVP_CHANGED(Category.PVP),
        ALLY_PVP_CHANGED(Category.PVP),
        ALLIANCE_INVITED(Category.RELATIONS),
        ALLIANCE_INVITATION_REVOKED(Category.RELATIONS),
        ALLIANCE_FORMED(Category.RELATIONS),
        ALLIANCE_BROKEN(Category.RELATIONS),
        WAR_DECLARED(Category.RELATIONS),
        WAR_WITHDRAWN(Category.RELATIONS),
        WAR_WON(Category.RELATIONS),
        TERRITORY_EXPANDED(Category.PURCHASES),
        VALIDITY_RENEWED(Category.PURCHASES);

        private final Category category;

        Type(Category category) {
            this.category = category;
        }

        public Category category() {
            return this.category;
        }
    }
}
