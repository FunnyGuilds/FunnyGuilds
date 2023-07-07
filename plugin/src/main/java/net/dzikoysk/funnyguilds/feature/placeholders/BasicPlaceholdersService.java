package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;
import org.bukkit.ChatColor;

public class BasicPlaceholdersService extends StaticPlaceholdersService<Object, BasicPlaceholders<Object>> {

    public static final BasicPlaceholders<String> ONLINE = new BasicPlaceholders<String>()
            .property("<online>", () -> ChatColor.GREEN)
            .property("</online>", end -> end);

    public static BasicPlaceholders<Object> createSimplePlaceholders(FunnyGuilds plugin) {
        return new BasicPlaceholders<>()
                .property("tps", NmsUtils::getFormattedTPS)
                .property("users", plugin.getUserManager()::countUsers)
                .property("guilds", plugin.getGuildManager()::countGuilds);
    }

}
