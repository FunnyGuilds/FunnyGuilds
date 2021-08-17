package net.dzikoysk.funnyguilds.feature.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistBroadcastHandler implements Runnable {

    @Override
    public void run() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        if (! config.playerListEnable) {
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
