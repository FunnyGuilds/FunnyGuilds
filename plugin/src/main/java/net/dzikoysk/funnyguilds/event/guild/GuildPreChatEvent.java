package net.dzikoysk.funnyguilds.event.guild;

import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildPreChatEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final GuildChatEvent.Type type;
    private final Set<Guild> receivers;
    private String message;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildPreChatEvent(EventCause eventCause, @Nullable User doer, Guild guild, GuildChatEvent.Type type, Set<Guild> receivers, String message) {
        super(eventCause, doer, guild);
        this.type = type;
        this.receivers = new HashSet<>(receivers);
        this.message = message;
    }

    public GuildChatEvent.Type getType() {
        return this.type;
    }

    public Set<Guild> getReceivers() {
        return this.receivers;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild chat event has been cancelled by the server!";
    }

}
