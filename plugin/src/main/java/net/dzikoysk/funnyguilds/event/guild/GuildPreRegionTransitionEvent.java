package net.dzikoysk.funnyguilds.event.guild;

import java.util.Optional;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired synchronously before a player crosses a guild boundary by moving or teleporting.
 * Cancelling this event cancels the entire movement, including transitions between two guilds.
 */
public class GuildPreRegionTransitionEvent extends FunnyEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Optional<Guild> fromGuild;
    private final Optional<Guild> toGuild;

    public GuildPreRegionTransitionEvent(User user, @Nullable Guild fromGuild, @Nullable Guild toGuild) {
        super(EventCause.USER, user);
        this.fromGuild = Optional.ofNullable(fromGuild);
        this.toGuild = Optional.ofNullable(toGuild);
    }

    public Optional<Guild> getFromGuild() {
        return this.fromGuild;
    }

    public Optional<Guild> getToGuild() {
        return this.toGuild;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] The server cancelled the guild region transition!";
    }
}
