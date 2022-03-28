package net.dzikoysk.funnyguilds.config.tablist;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class TablistPageSerializer implements ObjectSerializer<TablistPage> {

    @Override
    public boolean supports(@NotNull Class<? super TablistPage> type) {
        return TablistPage.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(TablistPage page, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("cycles", page.cycles);

        if (page.playerList != null) {
            data.addAsMap("player-list", page.playerList, Integer.class, String.class);
        }

        if (page.playerListHeader != null) {
            data.add("player-list-header", page.playerListHeader);
        }

        if (page.playerListFooter != null) {
            data.add("player-list-footer", page.playerListFooter);
        }
    }

    @Override
    public TablistPage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        int cycles = data.get("cycles", Integer.class);

        Map<Integer, String> playerList = data.containsKey("player-list")
                ? data.getAsMap("player-list", Integer.class, String.class)
                : null;

        String playerListHeader = data.containsKey("player-list-header")
                ? data.get("player-list-header", String.class)
                : null;

        String playerListFooter = data.containsKey("player-list-footer")
                ? data.get("player-list-footer", String.class)
                : null;

        return new TablistPage(cycles, playerList, playerListHeader, playerListFooter);
    }

}
