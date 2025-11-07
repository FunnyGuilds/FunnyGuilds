package net.dzikoysk.funnyguilds.guild.permission;

/**
 * Generic guild permissions used in various features
 */
public final class GuildPermissions {
    
    public static final GuildPermission<String> USER_POSITION = GuildPermission.permission("user.position", String.class);
    
    private GuildPermissions() {
    }
}
