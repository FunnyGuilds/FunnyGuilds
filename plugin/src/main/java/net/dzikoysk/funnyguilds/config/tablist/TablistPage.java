package net.dzikoysk.funnyguilds.config.tablist;

import java.util.Collections;
import java.util.Map;
import net.kyori.adventure.text.Component;

public record TablistPage(int cycles, Map<Integer, Component> cells, Component header, Component footer) {

    public TablistPage(
            int cycles,
            Map<Integer, Component> cells,
            Component header,
            Component footer
    ) {
        this.cycles = cycles;
        this.cells = Collections.unmodifiableMap(cells);
        this.header = header;
        this.footer = footer;
    }

}
