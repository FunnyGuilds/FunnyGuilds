package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildWarWinEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Guild loser;
    private final int streak;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildWarWinEvent(EventCause eventCause, User winnerAttacker, Guild winner, Guild loser, int streak) {
        super(eventCause, winnerAttacker, winner);
        this.loser = loser;
        this.streak = streak;
    }

    public Guild getWinner() {
        return this.getGuild();
    }

    public Guild getLoser() {
        return this.loser;
    }

    public int getStreak() {
        return this.streak;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild war win has been cancelled by the server!";
    }

}
