package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import java.io.File;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import org.bukkit.Location;

public abstract class WorldEditHook extends AbstractPluginHook {

    public WorldEditHook(String name) {
        super(name);
    }

    public abstract boolean pasteSchematic(File schematicFile, Location location, boolean withAir);

}
