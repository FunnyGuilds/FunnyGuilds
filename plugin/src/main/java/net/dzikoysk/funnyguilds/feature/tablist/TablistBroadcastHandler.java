package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistBroadcastHandler implements Runnable {

    @Override
    public void run() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        TablistConfiguration tablistConfig = plugin.getTablistConfiguration();

        if (!tablistConfig.playerListEnable) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = plugin.getUserManager().getUser(player).getOrNull();

            if (user == null) {
                continue;
            }

            IndividualPlayerList playerList = user.getCache().getPlayerList();

            if (playerList == null) {
                continue;
            }

            playerList.send();
        }
    }
}
