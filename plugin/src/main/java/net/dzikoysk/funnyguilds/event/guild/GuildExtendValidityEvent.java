package net.dzikoysk.funnyguilds.event.guild;

import java.time.Duration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildExtendValidityEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Duration extendTime;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildExtendValidityEvent(EventCause eventCause, User doer, Guild guild, Duration extendTime) {
        super(eventCause, doer, guild);
        this.extendTime = extendTime;
    }

    public Duration getExtendTime() {
        return this.extendTime;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild validity extension has been cancelled by the server!";
    }

}
