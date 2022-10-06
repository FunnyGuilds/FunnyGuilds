package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.feature.prefix.Dummy;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.feature.tablist.IndividualPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public class UserCache {

    private final User user;

    private IndividualPlayerList playerList;
    private Scoreboard scoreboard;
    private Option<IndividualPrefix> prefix = Option.none();
    private Dummy dummy;

    private BukkitTask teleportation;
    private long notificationTime;
    private boolean enter;
    private boolean spy;

    public UserCache(User user) {
        this.user = user;
    }

    @Deprecated
    public DamageState getDamageHistory() {
        return FunnyGuilds.getInstance().getDamageManager().getDamageState(this.user.getUUID());
    }

    public Option<IndividualPlayerList> getPlayerList() {
        return Option.of(this.playerList);
    }

    public void setPlayerList(IndividualPlayerList playerList) {
        this.playerList = playerList;
    }

    public Option<IndividualPrefix> getIndividualPrefix() {
        return this.prefix;
    }

    public void setIndividualPrefix(@Nullable IndividualPrefix prefix) {
        this.prefix = Option.of(prefix);
    }

    public synchronized Option<Scoreboard> getScoreboard() {
        return Option.of(this.scoreboard);
    }

    public void updateScoreboardIfNull(Player player) {
        if (this.scoreboard != null) {
            return;
        }

        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        if (config.useSharedScoreboard) {
            this.setScoreboard(player.getScoreboard());
            return;
        }

        ScoreboardManager sbManager = Bukkit.getScoreboardManager();
        if (sbManager != null) {
            this.setScoreboard(sbManager.getNewScoreboard());
        }
    }

    public synchronized void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public Dummy getDummy() {
        if (this.dummy == null) {
            this.dummy = new Dummy(this.user);
        }

        return this.dummy;
    }

    public void setDummy(Dummy dummy) {
        this.dummy = dummy;
    }

    public BukkitTask getTeleportation() {
        return this.teleportation;
    }

    public void setTeleportation(BukkitTask teleportation) {
        this.teleportation = teleportation;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }

    public void setNotificationTime(long notification) {
        this.notificationTime = notification;
    }

    public boolean getEnter() {
        return this.enter;
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public boolean isSpy() {
        return this.spy;
    }

    public boolean toggleSpy() {
        this.spy = !this.spy;
        return this.spy;
    }

}
