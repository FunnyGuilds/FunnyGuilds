package net.dzikoysk.funnyguilds.event.guild;

import java.util.Set;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildChatEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Type type;
    private final Set<Guild> receivers;
    private final Component inputMessage;
    private final Component formattedMessage;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildChatEvent(EventCause eventCause, @Nullable User doer, Guild guild, Type type, Set<Guild> receivers,
                          Component inputMessage, Component formattedMessage
    ) {
        super(eventCause, doer, guild);
        this.type = type;
        this.receivers = Set.copyOf(receivers);
        this.inputMessage = inputMessage;
        this.formattedMessage = formattedMessage;
    }

    public Type getType() {
        return this.type;
    }

    public Set<Guild> getReceivers() {
        return this.receivers;
    }
    
    public Component getInputMessage() {
        return this.inputMessage;
    }

    public Component getFormattedMessage() {
        return this.formattedMessage;
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
