package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.feature.scoreboard.dummy.Dummy;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTag;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class UserCache {

    private final User user;

    private Optional<Scoreboard> scoreboard = Optional.empty();
    private Optional<IndividualNameTag> nameTag = Optional.empty();
    private Optional<Dummy> dummy = Optional.empty();

    private BukkitTask teleportation;
    private long notificationTime;
    private boolean spy;

    public UserCache(User user) {
        this.user = user;
    }

    public synchronized Optional<Scoreboard> getScoreboard() {
        return this.scoreboard;
    }

    public synchronized void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = Optional.ofNullable(scoreboard);
    }

    public Optional<IndividualNameTag> getIndividualNameTag() {
        return this.nameTag;
    }

    public void setIndividualNameTag(@Nullable IndividualNameTag nameTag) {
        this.nameTag = Optional.ofNullable(nameTag);
    }

    public Optional<Dummy> getDummy() {
        return this.dummy;
    }

    public void setDummy(@Nullable Dummy dummy) {
        this.dummy = Optional.ofNullable(dummy);
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

    public boolean isSpy() {
        return this.spy;
    }

    public boolean toggleSpy() {
        this.spy = !this.spy;
        return this.spy;
    }

}
