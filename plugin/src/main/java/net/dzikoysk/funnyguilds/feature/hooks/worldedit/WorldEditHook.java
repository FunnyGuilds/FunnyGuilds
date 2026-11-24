package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import java.io.File;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import org.bukkit.Location;

public abstract class WorldEditHook extends AbstractPluginHook {

    public WorldEditHook(String name) {
        super(name);
    }

    public boolean pasteSchematic(File schematicFile, Location location, boolean withAir) {
        return this.pasteSchematic(schematicFile, location, withAir, undo -> { });
    }

    /**
     * Pastes the schematic and hands an undo action to the consumer once the paste succeeded,
     * so a caller can restore the previous terrain when creation fails afterwards.
     */
    public abstract boolean pasteSchematic(File schematicFile, Location location, boolean withAir, Consumer<Runnable> undoRegistration);

}
