package net.dzikoysk.funnyguilds.guild.permission.member;

import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

/**
 * Enum representing all permission types that can be assigned to guild members.
 * Each permission controls a specific action within the guild region.
 */
public enum GuildMemberPermissionType implements GuildPermission<Boolean> {
    
    // Block operations
    BLOCK_PLACE("member.block.place", "Stawianie bloków"),
    BLOCK_BREAK("member.block.break", "Niszczenie bloków"),
    
    // Special blocks
    OBSIDIAN_PLACE("member.obsidian.place", "Stawianie obsydianu"),
    OBSIDIAN_BREAK("member.obsidian.break", "Niszczenie obsydianu"),
    TNT_PLACE("member.tnt.place", "Stawianie TNT"),
    TNT_IGNITE("member.tnt.ignite", "Zapalanie TNT"),
    
    // Liquids
    BUCKET_FILL("member.bucket.fill", "Napełnianie wiader"),
    BUCKET_EMPTY("member.bucket.empty", "Opróżnianie wiader"),
    
    // Containers
    CHEST_OPEN("member.chest.open", "Otwieranie skrzyń"),
    CHEST_MODIFY("member.chest.modify", "Modyfikacja skrzyń"),
    ENDER_CHEST_OPEN("member.enderchest.open", "Otwieranie ender skrzyń"),
    
    // Interactions
    DOOR_USE("member.door.use", "Używanie drzwi"),
    BUTTON_USE("member.button.use", "Używanie przycisków"),
    LEVER_USE("member.lever.use", "Używanie dźwigni"),
    PRESSURE_PLATE_USE("member.pressure_plate.use", "Używanie płyt naciskowych"),
    
    // Entity operations
    ENTITY_PLACE("member.entity.place", "Stawianie bytów"),
    ENTITY_DAMAGE("member.entity.damage", "Atakowanie bytów"),
    ENTITY_INTERACT("member.entity.interact", "Interakcja z bytami"),
    
    // Hanging items
    HANGING_PLACE("member.hanging.place", "Wieszanie obrazów/ramek"),
    HANGING_BREAK("member.hanging.break", "Zdejmowanie obrazów/ramek"),
    
    // Redstone
    REDSTONE_USE("member.redstone.use", "Używanie mechanizmów redstone"),
    
    // Special guild actions
    HOME_TELEPORT("member.home.teleport", "Teleportacja do bazy"),
    INVITE_MEMBERS("member.invite", "Zapraszanie graczy"),
    KICK_MEMBERS("member.kick", "Wyrzucanie graczy");

    private final Key key;
    private final String displayName;

    GuildMemberPermissionType(@KeyPattern String key, String displayName) {
        this.key = Key.key(GuildPermission.PLUGIN_NAMESPACE, key);
        this.displayName = displayName;
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }
    
    /**
     * @return the display name of this permission for GUI display
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * @return the key string without namespace
     */
    public String getKeyString() {
        return this.key.value();
    }
    
    /**
     * Find permission type by key string.
     * @param keyString the key string (without namespace)
     * @return the permission type or null if not found
     */
    public static GuildMemberPermissionType fromKeyString(String keyString) {
        for (GuildMemberPermissionType type : values()) {
            if (type.getKeyString().equals(keyString)) {
                return type;
            }
        }
        return null;
    }
}
