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
        UserManager userManager = plugin.getUserManager();
        TablistConfiguration tablistConfig = plugin.getTablistConfiguration();

        if (!tablistConfig.playerListEnable) {
            return;
        }

        try (PandaStream<? extends Player> players = PandaStream.of(Bukkit.getOnlinePlayers())) {
            players.flatMap(userManager::findByPlayer)
                    .flatMap(user -> user.getCache().getPlayerList())
                    .forEach(IndividualPlayerList::send);
        }
    }
}
