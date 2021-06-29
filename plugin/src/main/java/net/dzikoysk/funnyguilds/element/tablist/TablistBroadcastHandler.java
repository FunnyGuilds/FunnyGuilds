package net.dzikoysk.funnyguilds.element.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistBroadcastHandler implements Runnable {

    @Override
    public void run() {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (! config.playerListEnable) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (! AbstractTablist.hasTablist(player)) {
                User user = User.get(player);

                if (user == null) {
                    continue;
                }

                AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, user);
            }

            AbstractTablist tablist = AbstractTablist.getTablist(player);
            tablist.send();
        }
    }
}
