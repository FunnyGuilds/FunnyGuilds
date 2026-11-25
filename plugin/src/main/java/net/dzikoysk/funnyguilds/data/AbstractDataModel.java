package net.dzikoysk.funnyguilds.data;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;

public abstract class AbstractDataModel implements DataModel {

    // Use the same monitor for autosaves, direct writes, deletes, and shutdown.
    private final Map<String, BooleanSupplier> pendingDeletes = new LinkedHashMap<>();
    private boolean closed;

    @Override
    public final synchronized void save(boolean ignoreNotChanged) {
        if (!this.closed) {
            this.flushDeletes();
            this.saveRecords(ignoreNotChanged);
        }
    }

    @Override
    public final synchronized void saveAndClose() {
        try {
            this.save(false);
        }
        finally {
            this.closed = true;
        }
    }

    protected abstract void saveRecords(boolean ignoreNotChanged);

    protected abstract String guildStorageKey(Guild guild);

    protected abstract BooleanSupplier guildDeletion(Guild guild);

    protected abstract BooleanSupplier regionDeletion(Region region);

    @Override
    public final synchronized void deleteGuild(Guild guild) {
        this.queueGuildDeletion(guild);
        this.flushDeletes();
    }

    @Override
    public final synchronized void queueGuildDeletion(Guild guild) {
        if (!this.closed && !guild.isRemoved()) {
            this.queueRemoval(guild, this.guildDeletion(guild));
        }
    }

    @Override
    public final synchronized void deleteRegion(Region region) {
        if (!this.closed && !region.isRemoved()) {
            this.queueRemoval(region, this.regionDeletion(region));
        }
        this.flushDeletes();
    }

    private void queueRemoval(AbstractMutableEntity entity, BooleanSupplier deletion) {
        entity.markRemoved();
        this.pendingDeletes.put(this.storageKey(entity), deletion);
    }

    @Override
    public final synchronized void renameGuild(Guild guild, String name) {
        if (this.closed || guild.isRemoved()) {
            return;
        }

        String oldGuildKey = this.guildStorageKey(guild);
        BooleanSupplier deleteOldGuild = this.guildDeletion(guild);
        guild.getRegion().ifPresent(region -> {
            this.pendingDeletes.put(this.storageKey(region), this.regionDeletion(region));
            region.setName(name);
        });
        String oldStorageKey = this.storageKey(guild);
        guild.setName(name);
        if (!oldGuildKey.equals(this.guildStorageKey(guild))) {
            this.pendingDeletes.put(oldStorageKey, deleteOldGuild);
        }
        this.flushDeletes();
    }

    @Override
    public final synchronized void flushDeletes() {
        if (!this.closed) {
            this.pendingDeletes.values().removeIf(BooleanSupplier::getAsBoolean);
        }
    }

    public final synchronized <T> boolean persist(AbstractMutableEntity entity, Supplier<T> capture, Predicate<T> write) {
        if (!this.canWrite(entity)) {
            return false;
        }
        try {
            return entity.saveSnapshot(capture, write);
        }
        catch (RuntimeException exception) {
            FunnyGuilds.getPluginLogger().error("Could not save " + entity.getType() + " " + entity.getName(), exception);
            return false;
        }
    }

    public final synchronized void update(AbstractMutableEntity entity, BooleanSupplier write) {
        if (this.canWrite(entity) && !write.getAsBoolean()) {
            entity.markChanged();
        }
    }

    private boolean canWrite(AbstractMutableEntity entity) {
        // A new entity can reuse a deleted file name. Retry the old deletion before writing it.
        return !this.closed && !entity.isRemoved() && !this.pendingDeletes.containsKey(this.storageKey(entity));
    }

    private String storageKey(AbstractMutableEntity entity) {
        String key = entity instanceof Guild guild ? this.guildStorageKey(guild) : entity.getName();
        return entity.getType() + ":" + key.toLowerCase(Locale.ROOT);
    }
}
