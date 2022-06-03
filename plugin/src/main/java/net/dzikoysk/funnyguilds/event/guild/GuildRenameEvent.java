package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildRenameEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();
    private final String oldName;
    private final String newName;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildRenameEvent(EventCause eventCause, User doer, Guild guild, String oldName, String newName) {
        super(eventCause, doer, guild);
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return this.oldName;
    }

    public String getNewName() {
        return this.newName;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild renaming has been cancelled by the server!";
    }

}
