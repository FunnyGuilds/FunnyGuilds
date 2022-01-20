package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.GZIPInputStream;
import net.dzikoysk.funnyguilds.nms.Reflections;
import org.bukkit.Location;

public class WorldEdit6Hook extends WorldEditHook {

    private Constructor<?> schematicReaderConstructor;
    private Constructor<?> clipboardHolderConstructor;
    private Constructor<?> pasteConstructor;
    private Constructor<?> vectorConstructor;

    private Method getWorldData;
    private Method pasteBuilderSetTo;
    private Method readSchematic;

    public WorldEdit6Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult init() throws Throwable {
        Class<?> schematicReaderClass = Reflections.getClass("com.sk89q.worldedit.extent.clipboard.io.SchematicReader");
        Class<?> worldDataClass = Reflections.getClass("com.sk89q.worldedit.world.registry.WorldData");
        Class<?> vectorClass = Reflections.getClass("com.sk89q.worldedit.Vector");

        schematicReaderConstructor = Reflections.getConstructor(schematicReaderClass,
                Reflections.getClass("com.sk89q.jnbt.NBTInputStream"));
        pasteConstructor = Reflections.getConstructor(PasteBuilder.class, ClipboardHolder.class, Extent.class, worldDataClass);
        clipboardHolderConstructor = Reflections.getConstructor(ClipboardHolder.class, Clipboard.class, worldDataClass);
        vectorConstructor = Reflections.getConstructor(vectorClass,
                double.class, double.class, double.class);

        schematicReaderConstructor.setAccessible(true);
        pasteConstructor.setAccessible(true);
        clipboardHolderConstructor.setAccessible(true);
        vectorConstructor.setAccessible(true);

        getWorldData = com.sk89q.worldedit.world.World.class.getDeclaredMethod("getWorldData");
        readSchematic = schematicReaderClass.getDeclaredMethod("read", worldDataClass);
        pasteBuilderSetTo = PasteBuilder.class.getDeclaredMethod("to", vectorClass);

        getWorldData.setAccessible(true);
        readSchematic.setAccessible(true);
        pasteBuilderSetTo.setAccessible(true);

        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean pasteSchematic(File schematicFile, Location location, boolean withAir) {
        try {
            Object pasteLocation = vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ());
            com.sk89q.worldedit.world.World pasteWorld = new BukkitWorld(location.getWorld());
            Object pasteWorldData = getWorldData.invoke(pasteWorld);

            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(schematicFile)));
            Object reader = schematicReaderConstructor.newInstance(nbtStream);
            Object clipboard = readSchematic.invoke(reader, pasteWorldData);

            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(pasteWorld, -1);

            ClipboardHolder clipboardHolder = (ClipboardHolder) clipboardHolderConstructor.newInstance(clipboard, pasteWorldData);
            PasteBuilder builder = ((PasteBuilder) pasteConstructor.newInstance(clipboardHolder, editSession, pasteWorldData));
            builder = (PasteBuilder) pasteBuilderSetTo.invoke(builder, pasteLocation);
            builder = builder.ignoreAirBlocks(!withAir);

            Operations.completeLegacy(builder.build());
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | MaxChangedBlocksException | IOException ex) {
            throw new RuntimeException("Could not paste schematic: " + schematicFile.getAbsolutePath(), ex);
        }

        return true;
    }

}
