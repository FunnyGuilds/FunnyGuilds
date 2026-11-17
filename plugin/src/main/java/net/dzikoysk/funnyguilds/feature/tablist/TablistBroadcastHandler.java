package net.dzikoysk.funnyguilds.feature.tablist;

public class TablistBroadcastHandler implements Runnable {

    private final TablistRenderer tablistRenderer;

    public TablistBroadcastHandler(TablistRenderer tablistRenderer) {
        this.tablistRenderer = tablistRenderer;
    }

    @Override
    public void run() {
        this.tablistRenderer.updatePageCycle();
        this.tablistRenderer.broadcast();
    }

}
