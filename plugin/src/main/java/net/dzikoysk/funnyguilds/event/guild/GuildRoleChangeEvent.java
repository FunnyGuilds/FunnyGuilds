package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class GuildRoleChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Guild guild;
    private final User actor;
    private final GuildRolePolicy previous;
    private final GuildRolePolicy current;

    public GuildRoleChangeEvent(Guild guild, User actor, GuildRolePolicy previous, GuildRolePolicy current) {
        this.guild = guild;
        this.actor = actor;
        this.previous = previous;
        this.current = current;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public User getActor() {
        return this.actor;
    }

    public GuildRolePolicy getPrevious() {
        return this.previous;
    }

    public GuildRolePolicy getCurrent() {
        return this.current;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
