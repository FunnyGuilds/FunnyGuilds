package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistBroadcastHandler implements Runnable {

    private final FunnyGuilds plugin;

    public TablistBroadcastHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        UserManager userManager = this.plugin.getUserManager();
        TablistConfiguration tablistConfig = this.plugin.getTablistConfiguration();

        if (!tablistConfig.playerListEnable) {
            return;
        }

        // Don't remove this toArray - iterating over online players asynchronously could occur with ConcurrentModificationException (See GH-2031).
        for (Player player : Bukkit.getOnlinePlayers().toArray(new Player[0])) {
            userManager.findByUuid(player.getUniqueId())
                    .flatMap(user -> user.getCache().getPlayerList())
                    .forEach(playerList -> {
                        playerList.updatePageCycle();
                        playerList.send();
                    });
        }
    }
}
