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

    private int player_list;
    private int ban_system;
    private int validity_system;
    private int funnyguilds_stats;
    private int player_list_time;

    public AsynchronouslyRepeater() {
        instance = this;
        player_list_time = Settings.getInstance().playerlistInterval;
    }

    public static AsynchronouslyRepeater getInstance() {
        if (instance == null)
            new AsynchronouslyRepeater();
        return instance;
    }

    public void start() {
        if (this.repeater != null)
            return;
        this.repeater = Bukkit.getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), this, 100, 20);
    }

    @Override
    public void run() {
        player_list++;
        ban_system++;
        validity_system++;
        funnyguilds_stats++;

        if (player_list == player_list_time)
            playerList();
        if (validity_system >= 10)
            validitySystem();
        if (ban_system >= 7)
            banSystem();
        if (funnyguilds_stats >= 10)
            funnyguildsStats();
    }

    private void playerList() {
        if (Settings.getInstance().playerlistEnable) {
            IndependentThread.action(ActionType.PLAYERLIST_GLOBAL_UPDATE);
            if (Settings.getInstance().playerlistPatch)
                for (Player p : Bukkit.getOnlinePlayers())
                    PacketSender.sendPacket(p, PacketPlayOutPlayerInfo.getPacket(p.getPlayerListName(), false, 0));
        }
        player_list = 0;
    }

    private void validitySystem() {
        ValiditySystem.getInstance().run();
        validity_system = 0;
    }

    private void banSystem() {
        BanSystem.getInstance().run();
        ban_system = 0;
    }

    private void funnyguildsStats() {
        MetricsCollector.getMetrics();
        funnyguilds_stats = 0;
    }

    public void reload() {
        player_list_time = Settings.getInstance().playerlistInterval;
    }

    public void stop() {
        if (repeater != null) {
            repeater.cancel();
            repeater = null;
        }
    }

}
