package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildRegionLeaveEvent extends GuildEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public GuildRegionLeaveEvent(EventCause cause, User doer, Guild guild) {
        super(cause, doer, guild, true);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild region leave has been cancelled by the server!";
    }
}
