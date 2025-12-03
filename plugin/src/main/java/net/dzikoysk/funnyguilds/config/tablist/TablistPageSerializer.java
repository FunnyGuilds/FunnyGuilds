package net.dzikoysk.funnyguilds.config.tablist;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TablistPageSerializer implements ObjectSerializer<TablistPage> {

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return TablistPage.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(TablistPage page, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.set("cycles", page.cycles());

        data.setMap(
                "cells",
                page.cells(),
                Integer.class,
                Component.class
        );

        if (page.header() != null) {
            data.set("header", page.header());
        }

        if (page.footer() != null) {
            data.set("footer", page.footer());
        }
    }

    @Override
    public TablistPage deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        int cycles = data.get("cycles", Integer.class);

        Map<Integer, Component> cells = data.containsKey("cells")
                ? data.getAsMap("cells", Integer.class, Component.class)
                : null;

        Component header = data.containsKey("header")
                ? data.get("header", Component.class)
                : null;

        Component footer = data.containsKey("footer")
                ? data.get("footer", Component.class)
                : null;

        return new TablistPage(cycles, cells, header, footer);
    }

}
