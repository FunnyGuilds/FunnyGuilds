package net.dzikoysk.funnyguilds.util.runnable;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.validity.ValiditySystem;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.transition.PacketPlayOutPlayerInfo;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class AsynchronouslyRepeater implements Runnable {

    private static AsynchronouslyRepeater instance;
    private volatile BukkitTask repeater;

    private int playerList;
    private int banSystem;
    private int validitySystem;
    private int funnyguildsStats;
    private int playerListTime;

    public AsynchronouslyRepeater() {
        instance = this;
        playerListTime = Settings.getInstance().playerlistInterval;
    }

    public void start() {
        if (this.repeater != null) {
            return;
        }
        this.repeater = Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 100, 20);
    }

    @Override
    public void run() {
        playerList++;
        banSystem++;
        validitySystem++;
        funnyguildsStats++;

        if (playerList == playerListTime) {
            playerList();
        }
        if (validitySystem >= 10) {
            validitySystem();
        }
        if (banSystem >= 7) {
            banSystem();
        }
        if (funnyguildsStats >= 10) {
            funnyguildsStats();
        }
    }

    private void playerList() {
        if (Settings.getInstance().playerlistEnable) {
            IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
            if (Settings.getInstance().playerlistPatch) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PacketSender.sendPacket(p, PacketPlayOutPlayerInfo.getPacket(p.getPlayerListName(), false, 0));
                }
            }
        }
        playerList = 0;
    }

    private void validitySystem() {
        ValiditySystem.getInstance().run();
        validitySystem = 0;
    }

    private void banSystem() {
        BanSystem.getInstance().run();
        banSystem = 0;
    }

    private void funnyguildsStats() {
        MetricsCollector.getMetrics();
        funnyguildsStats = 0;
    }

    public void reload() {
        playerListTime = Settings.getInstance().playerlistInterval;
    }

    public void stop() {
        if (repeater != null) {
            repeater.cancel();
            repeater = null;
        }
    }

    public static AsynchronouslyRepeater getInstance() {
        if (instance == null) {
            new AsynchronouslyRepeater();
        }
        return instance;
    }

}
