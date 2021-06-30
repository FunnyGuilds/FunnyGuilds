package net.dzikoysk.funnyguilds.element.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
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

            user.getCache().getPlayerList().send();
        }
    }
}
