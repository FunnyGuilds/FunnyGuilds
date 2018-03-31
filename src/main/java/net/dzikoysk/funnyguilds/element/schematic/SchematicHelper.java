package net.dzikoysk.funnyguilds.element.schematic;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import net.dzikoysk.funnyguilds.util.FunnyLogger;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class SchematicHelper {

    public static boolean pasteSchematic(File schematicFile, Location location, boolean withAir) {
        try {
            Vector pasteLocation = new Vector(location.getX(), location.getY(), location.getZ());
            World pasteWorld = new BukkitWorld(location.getWorld());
            WorldData pasteWorldData = pasteWorld.getWorldData();
            Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematicFile)).read(pasteWorldData);
            Region pasteRegion = clipboard.getRegion();

            Extent pasteExtent = WorldEdit.getInstance().getEditSessionFactory().getEditSession(pasteWorld, -1);
            AffineTransform transform = new AffineTransform();

            ForwardExtentCopy copy = new ForwardExtentCopy(pasteExtent, pasteRegion, clipboard.getOrigin(), pasteExtent, pasteLocation);
            if (!transform.isIdentity()) {
                copy.setTransform(transform);
            }
            
            if (!withAir) {
                copy.setSourceMask(new ExistingBlockMask(clipboard));
            }

            Operations.completeLegacy(copy);
            return true;
        } catch (IOException | MaxChangedBlocksException e) {
            FunnyLogger.exception(e);
            return false;
        }
    }

    private SchematicHelper() {}

}
