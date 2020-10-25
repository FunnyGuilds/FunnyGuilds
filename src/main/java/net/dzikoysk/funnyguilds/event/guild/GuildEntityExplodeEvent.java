package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.event.FunnyEvent;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuildEntityExplodeEvent extends FunnyEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final List<Block> affectedBlocks;

    public GuildEntityExplodeEvent(EventCause cause, List<Block> affectedBlocks) {
        super(cause, null);
        this.affectedBlocks = affectedBlocks;
    }

    public List<Block> getAffectedBlocks() {
        return affectedBlocks;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Region entity explode has been cancelled by the server!";
    }

    public static HandlerList getHandlerList() { return HANDLER_LIST; }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
