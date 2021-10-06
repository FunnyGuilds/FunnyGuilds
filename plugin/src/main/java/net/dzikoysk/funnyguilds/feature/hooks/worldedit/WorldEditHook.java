package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import java.io.File;
import org.bukkit.Location;

public interface WorldEditHook {

    boolean pasteSchematic(File schematicFile, Location location, boolean withAir);

    void init();
}
