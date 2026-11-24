package net.dzikoysk.funnyguilds.feature.hooks.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Location;

public class WorldEdit7Hook extends WorldEditHook {

    public WorldEdit7Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult init() throws Throwable {
        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean pasteSchematic(File schematicFile, Location location, boolean withAir, Consumer<Runnable> undoRegistration) {
        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()));
        try {
            BlockVector3 pasteLocation = BlockVector3.at(location.getX(), location.getY(), location.getZ());

            Clipboard clipboard = ClipboardFormats.findByFile(schematicFile).getReader(Files.newInputStream(schematicFile.toPath())).read();
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(!withAir)
                    .build();

            Operations.complete(operation);
            editSession.close();
            // The change set survives close(), so the undo action can run at any later point.
            undoRegistration.accept(() -> {
                try {
                    editSession.undo(editSession);
                }
                catch (Exception e) {
                    FunnyGuilds.getPluginLogger().error("Could not undo schematic paste: " + schematicFile.getAbsolutePath(), e);
                }
            });
            return true;
        }
        catch (Exception e) {
            editSession.close();
            FunnyGuilds.getPluginLogger().error("Could not paste schematic: " + schematicFile.getAbsolutePath(), e);
            return false;
        }
    }

}
