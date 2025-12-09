package net.dzikoysk.funnyguilds.feature.protection;

import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissionType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

public enum GuildProtectionPermission implements GuildPermission<Boolean> {
    BLOCK_BREAK("protection.block-break", GuildMemberPermissionType.BLOCK_BREAK),
    BLOCK_PLACE("protection.block-place", GuildMemberPermissionType.BLOCK_PLACE),
    BLOCK_IGNITE("protection.block-ignite", GuildMemberPermissionType.TNT_IGNITE),
    BUCKET_FILL("protection.bucket-fill", GuildMemberPermissionType.BUCKET_FILL),
    BUCKET_EMPTY("protection.bucket-empty", GuildMemberPermissionType.BUCKET_EMPTY),
    HANGING_PLACE("protection.hanging-place", GuildMemberPermissionType.HANGING_PLACE),
    HANGING_BREAK("protection.hanging-break", GuildMemberPermissionType.HANGING_BREAK),
    ENTITY_PLACE("protection.entity-place", GuildMemberPermissionType.ENTITY_PLACE),
    TNT_PLACE("protection.tnt-place", GuildMemberPermissionType.TNT_PLACE),
    OBSIDIAN_PLACE("protection.obsidian-place", GuildMemberPermissionType.OBSIDIAN_PLACE),
    OBSIDIAN_BREAK("protection.obsidian-break", GuildMemberPermissionType.OBSIDIAN_BREAK);

    private final Key key;
    private final GuildMemberPermissionType memberPermissionType;

    GuildProtectionPermission(@KeyPattern String key, GuildMemberPermissionType memberPermissionType) {
        this.key = Key.key(
                GuildPermission.PLUGIN_NAMESPACE,
                key
        );
        this.memberPermissionType = memberPermissionType;
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public final Class<Boolean> getValueType() {
        return Boolean.class;
    }

    /**
     * Get the corresponding member permission type for this protection permission.
     * @return the member permission type that controls this protection action
     */
    public GuildMemberPermissionType getMemberPermissionType() {
        return this.memberPermissionType;
    }
}
