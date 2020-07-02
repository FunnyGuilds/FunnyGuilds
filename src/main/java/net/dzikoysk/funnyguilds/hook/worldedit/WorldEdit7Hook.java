package net.dzikoysk.funnyguilds.hook.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEdit7Hook implements WorldEditHook {
    @Override
    public boolean pasteSchematic(File schematicFile, Location location, boolean withAir) {
        try {
            BlockVector3 pasteLocation = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            World pasteWorld = BukkitAdapter.adapt(location.getWorld());

            Clipboard clipboard = ClipboardFormats.findByFile(schematicFile).getReader(new FileInputStream(schematicFile)).read();
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(pasteWorld, -1);

            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(!withAir)
                    .build();

            Operations.complete(operation);
            editSession.flushSession();
            return true;
        }
        catch (IOException | WorldEditException e) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not paste schematic: " + schematicFile.getAbsolutePath(), e);
            return false;
        }
    }

    @Override
    public void init() {

    }
}
