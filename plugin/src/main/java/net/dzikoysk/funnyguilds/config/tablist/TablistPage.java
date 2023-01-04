package net.dzikoysk.funnyguilds.config.tablist;

import java.util.Map;

public class TablistPage {

    public final int cycles;
    public final Map<Integer, String> cells;
    public final String header;
    public final String footer;

    public TablistPage(int cycles, Map<Integer, String> cells, String header, String footer) {
        this.cycles = cycles;
        this.cells = cells;
        this.header = header;
        this.footer = footer;
    }

}
