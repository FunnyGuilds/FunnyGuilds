package net.dzikoysk.funnyguilds.event.guild;

import java.util.Collections;
import java.util.Set;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildChatEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Type type;
    private final Set<Guild> receivers;
    private final String message;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildChatEvent(EventCause eventCause, @Nullable User doer, Guild guild, Type type, Set<Guild> receivers, String message) {
        super(eventCause, doer, guild);
        this.type = type;
        this.receivers = Collections.unmodifiableSet(receivers);
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public Set<Guild> getReceivers() {
        return receivers;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("GuildChatEvent cannot be cancelled, use GuildPreChatEvent");
    }

    @Override
    public String getDefaultCancelMessage() {
        throw new UnsupportedOperationException("GuildChatEvent cannot be cancelled, use GuildPreChatEvent");
    }

    public static enum Type {
        PRIVATE,
        ALLY,
        ALL
    }

}
