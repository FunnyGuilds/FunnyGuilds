package net.dzikoysk.funnyguilds.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;

public abstract class AbstractDataModel implements DataModel {

    // The model monitor serializes I/O. The state lock only protects queue changes and snapshot capture.
    private final Object stateLock = new Object();
    private final Map<String, BooleanSupplier> pendingDeletes = new LinkedHashMap<>();
    private final Map<Guild, Rename> pendingRenames = new LinkedHashMap<>();
    private Rename writingRename;
    private boolean closing;
    private boolean closed;

    @Override
    public final synchronized void save(boolean ignoreNotChanged) {
        if (!this.closed) {
            this.flushDeletes();
            this.saveRenames();
            this.saveRecords(ignoreNotChanged);
        }
    }

    @Override
    public final synchronized void saveAndClose() {
        synchronized (this.stateLock) {
            this.closing = true;
        }
        try {
            this.save(false);
        }
        finally {
            this.closed = true;
            synchronized (this.stateLock) {
                if (!this.pendingDeletes.isEmpty()) {
                    FunnyGuilds.getPluginLogger().error("Uncompleted storage deletions at shutdown: "
                            + String.join(", ", this.pendingDeletes.keySet()));
                }
                if (!this.pendingRenames.isEmpty()) {
                    FunnyGuilds.getPluginLogger().error("Uncompleted guild renames at shutdown: "
                            + this.pendingRenames.keySet().stream().map(Guild::getName).toList());
                }
            }
        }
    }

    protected abstract void saveRecords(boolean ignoreNotChanged);

    protected abstract boolean saveGuild(Guild guild);

    protected abstract boolean saveRegion(Region region);

    protected abstract String guildStorageKey(Guild guild);

    protected abstract BooleanSupplier guildDeletion(Guild guild);

    protected abstract BooleanSupplier regionDeletion(Region region);

    @Override
    public final void deleteGuild(Guild guild) {
        this.queueGuildDeletion(guild);
        this.flushDeletes();
    }

    @Override
    public final void queueGuildDeletion(Guild guild) {
        synchronized (this.stateLock) {
            if (!this.closing && !guild.isRemoved()) {
                Rename rename = this.pendingRenames.remove(guild);
                if (rename != null) {
                    this.pendingDeletes.putAll(rename.oldRecords());
                }
                this.queueRemoval(guild, this.guildDeletion(guild));
            }
        }
    }

    @Override
    public final void deleteRegion(Region region) {
        this.queueRegionDeletion(region);
        this.flushDeletes();
    }

    @Override
    public final void queueRegionDeletion(Region region) {
        synchronized (this.stateLock) {
            if (!this.closing && !region.isRemoved()) {
                this.queueRemoval(region, this.regionDeletion(region));
            }
        }
    }

    private void queueRemoval(AbstractMutableEntity entity, BooleanSupplier deletion) {
        entity.markRemoved();
        this.pendingDeletes.put(this.storageKey(entity), deletion);
    }

    @Override
    public final void renameGuild(Guild guild, String name) {
        synchronized (this.stateLock) {
            if (this.closing || guild.isRemoved() || guild.getName().equals(name)) {
                return;
            }

            Map<String, BooleanSupplier> oldRecords = new LinkedHashMap<>();
            Rename previous = this.pendingRenames.get(guild);
            if (previous != null) {
                oldRecords.putAll(previous.oldRecords());
            }
            String oldGuildKey = this.storageKey(guild);
            BooleanSupplier deleteOldGuild = this.guildDeletion(guild);
            Region region = guild.getRegion().orElse(null);
            if (region != null) {
                oldRecords.put(this.storageKey(region), this.regionDeletion(region));
                region.setName(name);
                oldRecords.remove(this.storageKey(region));
            }
            guild.setName(name);
            oldRecords.put(oldGuildKey, deleteOldGuild);
            oldRecords.remove(this.storageKey(guild));

            this.pendingRenames.put(guild, new Rename(guild, region, oldRecords));
        }
    }

    private void saveRenames() {
        List<Rename> renames;
        synchronized (this.stateLock) {
            renames = List.copyOf(this.pendingRenames.values());
        }
        boolean completed = false;
        for (Rename rename : renames) {
            this.writingRename = rename;
            try {
                // Keep the previous guild/region pair readable until both replacement records exist.
                if ((rename.region() == null || this.saveRegion(rename.region())) && this.saveGuild(rename.guild())) {
                    synchronized (this.stateLock) {
                        if (this.pendingRenames.get(rename.guild()) == rename) {
                            this.pendingRenames.remove(rename.guild());
                            this.pendingDeletes.putAll(rename.oldRecords());
                            completed = true;
                        }
                    }
                }
            }
            finally {
                this.writingRename = null;
            }
        }
        if (completed) {
            this.flushDeletes();
        }
    }

    @Override
    public final synchronized void flushDeletes() {
        if (this.closed) {
            return;
        }
        Map<String, BooleanSupplier> deletions;
        synchronized (this.stateLock) {
            deletions = new LinkedHashMap<>(this.pendingDeletes);
        }
        deletions.forEach((key, deletion) -> {
            try {
                if (deletion.getAsBoolean()) {
                    synchronized (this.stateLock) {
                        this.pendingDeletes.remove(key, deletion);
                    }
                }
            }
            catch (RuntimeException exception) {
                FunnyGuilds.getPluginLogger().error("Could not delete storage record " + key, exception);
            }
        });
    }

    public final synchronized <T> boolean persist(AbstractMutableEntity entity, Supplier<T> capture, Predicate<T> write) {
        try {
            BooleanSupplier save;
            synchronized (this.stateLock) {
                if (!this.canWrite(entity, true)) {
                    return false;
                }
                save = entity.prepareSave(capture, write);
            }
            return save.getAsBoolean();
        }
        catch (RuntimeException exception) {
            FunnyGuilds.getPluginLogger().error("Could not save " + entity.getType() + " " + entity.getName(), exception);
            return false;
        }
    }

    public final synchronized void update(AbstractMutableEntity entity, BooleanSupplier write) {
        synchronized (this.stateLock) {
            if (!this.canWrite(entity, false)) {
                return;
            }
        }
        if (!write.getAsBoolean()) {
            entity.markChanged();
        }
    }

    private boolean canWrite(AbstractMutableEntity entity, boolean fullRecord) {
        if (this.closed || entity.isRemoved()) {
            return false;
        }
        String key = this.storageKey(entity);
        if (this.pendingDeletes.containsKey(key)) {
            return false;
        }
        if (this.writingRename != null && this.writingRename.includes(entity)
                && this.pendingRenames.get(this.writingRename.guild()) != this.writingRename) {
            return false;
        }
        for (Rename rename : this.pendingRenames.values()) {
            if (rename.oldRecords().containsKey(key)
                    || (rename.includes(entity) && (!fullRecord || rename != this.writingRename))) {
                return false;
            }
        }
        return true;
    }

    private String storageKey(AbstractMutableEntity entity) {
        String key = entity instanceof Guild guild ? this.guildStorageKey(guild) : entity.getName();
        return entity.getType() + ":" + key.toLowerCase(Locale.ROOT);
    }

    private record Rename(Guild guild, Region region, Map<String, BooleanSupplier> oldRecords) {
        boolean includes(AbstractMutableEntity entity) {
            return this.guild == entity || this.region == entity;
        }
    }
}
