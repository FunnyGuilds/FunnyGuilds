package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class TablistBroadcastHandler implements Runnable {

    @Override
    public void run() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        UserManager userManager = plugin.getUserManager();
        TablistConfiguration tablistConfig = plugin.getTablistConfiguration();

        if (!tablistConfig.playerListEnable) {
            return;
        }

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(userManager::getUser)
                .flatMap(user -> user.getCache().getPlayerList())
                .forEach(IndividualPlayerList::send);
    }
}
