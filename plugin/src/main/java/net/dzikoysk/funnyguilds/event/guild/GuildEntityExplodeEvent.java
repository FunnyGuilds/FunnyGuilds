package net.dzikoysk.funnyguilds.event.guild;

import java.util.List;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildEntityExplodeEvent extends FunnyEvent {

    private static final HandlerList handlers = new HandlerList();
    private final List<Block> affectedBlocks;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public GuildEntityExplodeEvent(EventCause cause, List<Block> affectedBlocks) {
        super(cause, null);
        this.affectedBlocks = affectedBlocks;
    }

    public List<Block> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Region entity explode has been cancelled by the server!";
    }

}
