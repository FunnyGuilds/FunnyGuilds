package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildLifeGainEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final GainReason reason;
    private final int livesBefore;
    private final int livesAfter;

    public enum GainReason {
        HEART_CONQUER,
        WAR_WIN,
        BUY,
        ADMIN
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildLifeGainEvent(EventCause eventCause, User doer, Guild guild, GainReason reason, int livesBefore, int livesAfter) {
        super(eventCause, doer, guild);
        this.reason = reason;
        this.livesBefore = livesBefore;
        this.livesAfter = livesAfter;
    }

    public GainReason getReason() {
        return this.reason;
    }

    public int getLivesBefore() {
        return this.livesBefore;
    }

    public int getLivesAfter() {
        return this.livesAfter;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild life gain has been cancelled by the server!";
    }

}
