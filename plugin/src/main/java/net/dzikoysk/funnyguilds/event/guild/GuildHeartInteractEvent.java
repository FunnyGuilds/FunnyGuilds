package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildHeartInteractEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Click click;
    private final boolean securityCheckPassed;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildHeartInteractEvent(EventCause eventCause, User doer, Guild guild, Click click, boolean securityCheckPassed) {
        super(eventCause, doer, guild);
        this.click = click;
        this.securityCheckPassed = securityCheckPassed;
    }

    public GuildHeartInteractEvent(EventCause eventCause, User doer, Guild guild, Click click) {
        this(eventCause, doer, guild, click, true);
    }

    public Click getClick() {
        return this.click;
    }

    public boolean isSecurityCheckPassed() {
        return this.securityCheckPassed;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild heart interact event has been canceled by the server!";
    }

    public enum Click {
        LEFT, RIGHT;
    }

}
