package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;

public class BasicPlaceholdersService extends StaticPlaceholdersService<Object, BasicPlaceholders<Object>> {

    public static final BasicPlaceholders<String> ONLINE = new BasicPlaceholders<String>()
            .property("<online>", () -> "§a")
            .property("</online>", () -> "§7"); // reset do szarego


    public static BasicPlaceholders<Object> createSimplePlaceholders(FunnyGuilds plugin) {
        return new BasicPlaceholders<>()
                .property("tps", NmsUtils::getFormattedTPS)
                .property("users", plugin.getUserManager()::countUsers)
                .property("guilds", plugin.getGuildManager()::countGuilds);
    }

}
