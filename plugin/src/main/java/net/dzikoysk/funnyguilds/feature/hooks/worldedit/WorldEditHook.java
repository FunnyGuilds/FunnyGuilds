package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import org.bukkit.Location;

import java.io.File;

public interface WorldEditHook {

    boolean pasteSchematic(File schematicFile, Location location, boolean withAir);

    void init();
}
