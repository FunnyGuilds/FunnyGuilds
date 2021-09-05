package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class TablistBroadcastHandler implements Runnable {

    @Override
    public void run() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        UserManager userManager = plugin.getUserManager();
        PluginConfiguration config = plugin.getPluginConfiguration();

        if (! config.playerListEnable) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(userManager::getUser)
                .forEach(user -> {
                    IndividualPlayerList playerList = user.getCache().getPlayerList();

                    if (playerList == null) {
                        return;
                    }

                    playerList.send();
                });
    }
}
