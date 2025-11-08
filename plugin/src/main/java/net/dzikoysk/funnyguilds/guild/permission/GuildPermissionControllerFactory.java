package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.FunnyGuilds;

public final class GuildPermissionControllerFactory {
    
    private GuildPermissionControllerFactory() {
    }

    public static GuildPermissionController create(FunnyGuilds plugin, GuildPermissionControllerType type) {
        switch (type) {
            case STATIC:
                return createStaticController(plugin);
            case EVENT:
                return createEventController(plugin);
            case MIXED:
                return new MixedGuildPermissionController(
                        createStaticController(plugin),
                        createEventController(plugin)
                );
            default:
                throw new IllegalArgumentException("Unknown GuildPermissionControllerType: " + type);
        }
    }
    
    private static StaticGuildPermissionController createStaticController(FunnyGuilds plugin) {
        return new StaticGuildPermissionController(
                plugin.getPluginConfiguration(),
                plugin.getMessageService()
        );
    }
    
    private static EventGuildPermissionController createEventController(FunnyGuilds plugin) {
        return new EventGuildPermissionController();
    }
}
