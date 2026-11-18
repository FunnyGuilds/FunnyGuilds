package net.dzikoysk.funnyguilds.guild.permission;

public record GuildPermissionDecision(boolean allowed, Source source) {

    public enum Source {
        OWNER, PLAYER, CUSTOM_ROLE, BUILT_IN_ROLE, DEFAULT
    }
}
