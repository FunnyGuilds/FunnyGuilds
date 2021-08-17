package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class GuildPreRenameEvent extends GuildEvent {

    private final String oldName;
    private final String newName;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildPreRenameEvent(EventCause eventCause, User doer, Guild guild, String oldName, String newName) {
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
