package net.dzikoysk.funnyguilds.config.tablist;

import java.util.Map;

public class TablistPage {

    public final int cycles;
    public final Map<Integer, String> playerList;
    public final String playerListHeader;
    public final String playerListFooter;

    public TablistPage(int cycles, Map<Integer, String> playerList, String playerListHeader, String playerListFooter) {
        this.cycles = cycles;
        this.playerList = playerList;
        this.playerListHeader = playerListHeader;
        this.playerListFooter = playerListFooter;
    }

}
