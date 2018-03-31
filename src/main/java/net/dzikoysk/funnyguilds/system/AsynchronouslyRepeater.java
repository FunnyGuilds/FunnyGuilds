package net.dzikoysk.funnyguilds.system;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.validity.ValiditySystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AsynchronouslyRepeater implements Runnable {

    private static final AsynchronouslyRepeater instance = new AsynchronouslyRepeater();

    private volatile BukkitTask repeater;
    private int banSystem;
    private int validitySystem;

    private AsynchronouslyRepeater() {
    }

    public void start() {
        if (this.repeater != null) {
            return;
        }
        
        this.repeater = Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 100, 20);
    }

    @Override
    public void run() {
        ++banSystem;
        ++validitySystem;

        if (validitySystem >= 10) {
            validitySystem();
        }
        
        if (banSystem >= 7) {
            banSystem();
        }

        PluginConfig config = Settings.getConfig();

        if (!config.playerlistEnable) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!AbstractTablist.hasTablist(player)) {
                AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
            }

            AbstractTablist tablist = AbstractTablist.getTablist(player);
            tablist.send();
        }
    }

    public void stop() {
        if (repeater == null) {
            return;
        }

        repeater.cancel();
        repeater = null;
    }

    private void validitySystem() {
        ValiditySystem.getInstance().run();
        validitySystem = 0;
    }

    private void banSystem() {
        BanSystem.getInstance().run();
        banSystem = 0;
    }

    public static AsynchronouslyRepeater getInstance() {
        return instance;
    }

}
