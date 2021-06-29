package net.dzikoysk.funnyguilds.hook.worldedit;

import org.bukkit.Location;

import java.io.File;

public interface WorldEditHook {

    boolean pasteSchematic(File schematicFile, Location location, boolean withAir);

    void init();
}
