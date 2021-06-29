package net.dzikoysk.funnyguilds.nms.api.playerlist;

public interface PlayerList {
    void send(String[] playerListCells, String header, String footer, int ping);
}
