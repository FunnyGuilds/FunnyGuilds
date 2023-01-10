package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;

public class TablistBroadcastHandler implements Runnable {

    private final FunnyGuilds plugin;

    public TablistBroadcastHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        UserManager userManager = this.plugin.getUserManager();
        TablistConfiguration tablistConfig = this.plugin.getTablistConfiguration();

        if (!tablistConfig.enabled) {
            return;
        }

        // Don't remove this toArray - iterating over online players asynchronously without shallow copy could occur with ConcurrentModificationException (See GH-2031).
        PandaStream.of(Bukkit.getOnlinePlayers().toArray(new Player[0]))
                .flatMap(player -> userManager.findByUuid(player.getUniqueId()))
                .flatMap(user -> user.getCache().getPlayerList())
                .forEach(playerList -> {
                    playerList.updatePageCycle();
                    playerList.send();
                });
    }
}
