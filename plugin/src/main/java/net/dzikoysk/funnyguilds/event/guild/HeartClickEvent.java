package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class HeartClickEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Click click;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HeartClickEvent(EventCause eventCause, User doer, Guild guild, Click click) {
        super(eventCause, doer, guild);

        this.click = click;
    }

    public Click getClick() {
        return click;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild heart click event has been canceled by the server!";
    }

    public enum Click {
        LEFT, RIGHT;
    }

}
