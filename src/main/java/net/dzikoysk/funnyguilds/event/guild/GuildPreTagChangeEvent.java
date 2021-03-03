package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.event.HandlerList;

public class GuildPreTagChangeEvent extends GuildEvent {

    private final String oldTag;
    private final String newTag;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildPreTagChangeEvent(EventCause eventCause, User doer, Guild guild, String oldTag, String newTag) {
        super(eventCause, doer, guild);

        this.oldTag = oldTag;
        this.newTag = newTag;
    }

    public String getOldTag() {
        return oldTag;
    }

    public String getNewTag() {
        return this.newTag;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Changing guild tag has been cancelled by the server!";
    }

}
