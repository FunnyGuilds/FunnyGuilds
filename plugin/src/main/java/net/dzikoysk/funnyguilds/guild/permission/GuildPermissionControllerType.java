package net.dzikoysk.funnyguilds.guild.permission;

public enum GuildPermissionControllerType {
    /**
     * Static permission controller that uses predefined permission values, preserving default behavior.
     */
    STATIC, 
    /**
     * Event-based permission controller that triggers events to determine permission values, allowing dynamic modifications.
     * Without listeners, it prevents all actions by default.
     */
    EVENT,
    /**
     * Mixed permission controller that combines static and event-based approaches.
     * First check events for dynamic overrides, then fall back to static values if no events modify the outcome.
     */
    MIXED
}
