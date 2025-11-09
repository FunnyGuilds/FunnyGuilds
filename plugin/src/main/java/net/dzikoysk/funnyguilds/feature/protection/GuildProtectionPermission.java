package net.dzikoysk.funnyguilds.feature.protection;

import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

public enum GuildProtectionPermission implements GuildPermission<Boolean> {
    BLOCK_BREAK("protection.block-break"),
    BLOCK_PLACE("protection.block-place"),
    BLOCK_IGNITE("protection.block-ignite"),
    BUCKET_FILL("protection.bucket-fill"),
    BUCKET_EMPTY("protection.bucket-empty"),
    HANGING_PLACE("protection.hanging-place"),
    HANGING_BREAK("protection.hanging-break"),
    ENTITY_PLACE("protection.entity-place");

    private final Key key;

    GuildProtectionPermission(@KeyPattern String key) {
        this.key = Key.key(
                GuildPermission.PLUGIN_NAMESPACE,
                key
        );
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public final Class<Boolean> getValueType() {
        return Boolean.class;
    }
}
