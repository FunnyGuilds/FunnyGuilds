package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildTagChangeEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final String oldTag;
    private final String newTag;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildTagChangeEvent(EventCause eventCause, User doer, Guild guild, String oldTag, String newTag) {
        super(eventCause, doer, guild);
        this.oldTag = oldTag;
        this.newTag = newTag;
    }

    public String getOldTag() {
        return this.oldTag;
    }

    public String getNewTag() {
        return this.newTag;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Changing guild tag has been cancelled by the server!";
    }

}
