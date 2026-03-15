package net.dzikoysk.funnyguilds.feature.items.gui;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class GuildGuiRefreshTask extends BukkitRunnable {

    private final GuildItemsGui gui;
    private final Player player;

    public GuildGuiRefreshTask(GuildItemsGui gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline() || gui.isClosed()) {
            cancel();
            return;
        }
        gui.refresh();
    }

}

