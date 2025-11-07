package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;

/**
 * Generic guild permissions used in various features.
 * 
 * @see GuildCommandPermission
 * @see GuildProtectionPermission
 */
public final class GenericGuildPermissions {

    /**
     * Specifies how the user position is displayer in messages.
     */
    public static final GuildPermission<String> USER_POSITION = GuildPermission.permission("user.position", String.class);

    /**
     * Allow to use and see guild chat.
     */
    public static final GuildPermission<Boolean> GUILD_CHAT_USE = GuildPermission.booleanPermission("guild.chat.use");
    /**
     * Allow to see guild chat.
     */
    public static final GuildPermission<Boolean> GUILD_CHAT_SEE = GuildPermission.booleanPermission("guild.chat.see");
    /**
     * Allow to use and see ally chat.
     */
    public static final GuildPermission<Boolean> ALLY_CHAT_USE = GuildPermission.booleanPermission("ally.chat.use");
    /**
     * Allow to see ally chat.
     */
    public static final GuildPermission<Boolean> ALLY_CHAT_SEE = GuildPermission.booleanPermission("ally.chat.see");
    /**
     * Allow to use and see global chat.
     */
    public static final GuildPermission<Boolean> GLOBAL_CHAT_USE = GuildPermission.booleanPermission("global.chat.use");
    /**
     * Allow to see global chat.
     */
    public static final GuildPermission<Boolean> GLOBAL_CHAT_SEE = GuildPermission.booleanPermission("global.chat.see");
    
    private GenericGuildPermissions() {
    }
}
