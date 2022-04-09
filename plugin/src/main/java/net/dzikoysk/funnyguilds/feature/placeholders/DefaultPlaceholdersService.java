package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import org.bukkit.ChatColor;

public class DefaultPlaceholdersService extends AbstractPlaceholdersService<Object, SimplePlaceholders<Object>> {

    public static final SimplePlaceholders<String> ONLINE = new SimplePlaceholders<String>()
            .property("<online>", () -> ChatColor.GREEN)
            .property("</online>", end -> end);

    public static SimplePlaceholders<Object> createSimplePlaceholders(FunnyGuilds plugin) {
        return new SimplePlaceholders<>()
                .property("tps", MinecraftServerUtils::getFormattedTPS)
                .property("users", plugin.getUserManager()::countUsers)
                .property("guilds", plugin.getGuildManager()::countGuilds);
    }

}
