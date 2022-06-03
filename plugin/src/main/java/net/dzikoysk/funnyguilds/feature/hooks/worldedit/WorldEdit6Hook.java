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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
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

        this.schematicReaderConstructor = Reflections.getConstructor(schematicReaderClass, Reflections.getClass("com.sk89q.jnbt.NBTInputStream"));
        this.pasteConstructor = Reflections.getConstructor(PasteBuilder.class, ClipboardHolder.class, Extent.class, worldDataClass);
        this.clipboardHolderConstructor = Reflections.getConstructor(ClipboardHolder.class, Clipboard.class, worldDataClass);
        this.vectorConstructor = Reflections.getConstructor(vectorClass, double.class, double.class, double.class);

        this.schematicReaderConstructor.setAccessible(true);
        this.pasteConstructor.setAccessible(true);
        this.clipboardHolderConstructor.setAccessible(true);
        this.vectorConstructor.setAccessible(true);

        this.getWorldData = com.sk89q.worldedit.world.World.class.getDeclaredMethod("getWorldData");
        this.readSchematic = schematicReaderClass.getDeclaredMethod("read", worldDataClass);
        this.pasteBuilderSetTo = PasteBuilder.class.getDeclaredMethod("to", vectorClass);

        this.getWorldData.setAccessible(true);
        this.readSchematic.setAccessible(true);
        this.pasteBuilderSetTo.setAccessible(true);

        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean pasteSchematic(File schematicFile, Location location, boolean withAir) {
        try {
            Object pasteLocation = this.vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ());
            com.sk89q.worldedit.world.World pasteWorld = new BukkitWorld(location.getWorld());
            Object pasteWorldData = this.getWorldData.invoke(pasteWorld);

            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(Files.newInputStream(schematicFile.toPath())));
            Object reader = this.schematicReaderConstructor.newInstance(nbtStream);
            Object clipboard = this.readSchematic.invoke(reader, pasteWorldData);

            @SuppressWarnings("deprecation")
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(pasteWorld, -1);

            ClipboardHolder clipboardHolder = (ClipboardHolder) this.clipboardHolderConstructor.newInstance(clipboard, pasteWorldData);
            PasteBuilder builder = ((PasteBuilder) this.pasteConstructor.newInstance(clipboardHolder, editSession, pasteWorldData));
            builder = (PasteBuilder) this.pasteBuilderSetTo.invoke(builder, pasteLocation);
            builder = builder.ignoreAirBlocks(!withAir);

            Operations.completeLegacy(builder.build());
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | MaxChangedBlocksException | IOException ex) {
            throw new RuntimeException("Could not paste schematic: " + schematicFile.getAbsolutePath(), ex);
        }

        return true;
    }

}
