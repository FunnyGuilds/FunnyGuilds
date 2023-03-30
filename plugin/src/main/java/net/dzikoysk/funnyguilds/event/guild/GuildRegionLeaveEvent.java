package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves a guild region by crossing it's border or teleporting out of it
 */
public class GuildRegionLeaveEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildRegionLeaveEvent(EventCause cause, User doer, Guild guild) {
        super(cause, doer, guild, true);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild region leave has been cancelled by the server!";
    }

}
