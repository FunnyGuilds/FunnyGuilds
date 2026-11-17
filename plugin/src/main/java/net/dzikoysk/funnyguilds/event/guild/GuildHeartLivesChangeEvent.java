package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildHeartLivesChangeEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int newHeartLives;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildHeartLivesChangeEvent(EventCause eventCause, User doer, Guild guild, int newHeartLives) {
        super(eventCause, doer, guild);
        this.newHeartLives = newHeartLives;
    }

    public int getNewHeartLives() {
        return this.newHeartLives;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild heart lives change has been cancelled by the server!";
    }

}
