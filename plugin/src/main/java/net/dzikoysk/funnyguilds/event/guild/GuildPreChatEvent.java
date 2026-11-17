package net.dzikoysk.funnyguilds.event.guild;

import java.util.Collection;
import java.util.Set;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildPreChatEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final GuildChatEvent.Type type;
    private final Set<Guild> receivers;
    private final Component inputMessage;
    private Component formattedMessage;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildPreChatEvent(EventCause eventCause, @Nullable User doer, Guild guild, GuildChatEvent.Type type, Collection<Guild> receivers,
                             Component inputMessage, Component formattedMessage) {
        super(eventCause, doer, guild);
        this.type = type;
        this.receivers = Set.copyOf(receivers);
        this.inputMessage = inputMessage;
        this.formattedMessage = formattedMessage;
    }

    public GuildChatEvent.Type getType() {
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

    public void setFormattedMessage(Component formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild chat event has been cancelled by the server!";
    }

}
