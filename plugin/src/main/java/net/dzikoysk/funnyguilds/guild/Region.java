package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public class Region extends AbstractMutableEntity {

    private String name;
    private Option<Guild> guild = Option.none();

    private Option<World> world = Option.none();
    private Option<Location> center = Option.none();
    private int size;
    private int enlarge;

    private Location firstCorner;
    private Location secondCorner;

    public Region(String name) {
        this.name = name;
    }

    public Region(@NotNull Guild guild, @NotNull Location center, int size) {
        this(guild.getName());

        this.guild = Option.of(guild);
        this.world = Option.of(center.getWorld());
        this.center = Option.of(center);
        this.size = size;

        this.update();
    }

    public synchronized void update() {
        super.markChanged();

        if (this.center.isEmpty()) {
            return;
        }
        Location center = this.center.get();

        if (this.size < 1) {
            return;
        }

        if (this.world.isEmpty()) {
            this.world = Option.of(Bukkit.getWorlds().get(0));
        }
        World world = this.world.get();

        if (this.world != null) {
            int lx = center.getBlockX() + this.size;
            int lz = center.getBlockZ() + this.size;

            int px = center.getBlockX() - this.size;
            int pz = center.getBlockZ() - this.size;

            Vector l = new Vector(lx, LocationUtils.getMinHeight(world), lz);
            Vector p = new Vector(px, world.getMaxHeight(), pz);

            this.firstCorner = l.toLocation(world);
            this.secondCorner = p.toLocation(world);
        }
    }

    public boolean isIn(Location location) {
        if (location == null || this.firstCorner == null || this.secondCorner == null) {
            return false;
        }

        if(this.world.isEmpty()) {
            return false;
        }

        if (!this.world.get().equals(location.getWorld())) {
            return false;
        }

        if (location.getBlockX() > this.getLowerX() && location.getBlockX() < this.getUpperX()) {
            if (location.getBlockY() > this.getLowerY() && location.getBlockY() < this.getUpperY()) {
                return location.getBlockZ() > this.getLowerZ() && location.getBlockZ() < this.getUpperZ();
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        super.markChanged();
    }

    @NotNull
    public Option<Guild> getGuildOption() {
        return this.guild;
    }

    public boolean hasGuild() {
        return this.guild.isPresent();
    }

    public void setGuild(Guild guild) {
        this.guild = Option.of(guild);
        super.markChanged();
    }

    @NotNull
    public Option<World> getWorldOption() {
        return this.world;
    }

    public boolean hasWorld() {
        return this.world.isPresent();
    }

    @NotNull
    public Option<Location> getCenterOption() {
        return this.center;
    }

    public boolean hasCenter() {
        return this.center.isPresent();
    }

    @NotNull
    public Option<Location> getHeartOption() {
        return this.getHeartBlockOption()
                .map(Block::getLocation);
    }

    @NotNull
    public Option<Block> getHeartBlockOption() {
        return this.getCenterOption()
                .map(Location::getBlock)
                .map(block -> block.getRelative(BlockFace.DOWN));
    }

    public void setCenter(Location location) {
        this.center = Option.of(location);
        this.world = Option.of(location.getWorld());
        this.update();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.update();
    }

    public int getEnlarge() {
        return this.enlarge;
    }

    public void setEnlarge(int enlarge) {
        this.enlarge = enlarge;
        super.markChanged();
    }

    public int getUpperX() {
        return compareCoordinates(true, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getUpperY() {
        return compareCoordinates(true, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getUpperZ() {
        return compareCoordinates(true, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public int getLowerX() {
        return compareCoordinates(false, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getLowerY() {
        return compareCoordinates(false, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getLowerZ() {
        return compareCoordinates(false, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public Location getUpperCorner() {
        return new Location(this.world.getOrNull(), this.getUpperX(), this.getUpperY(), this.getUpperZ());
    }

    public Location getLowerCorner() {
        return new Location(this.world.getOrNull(), this.getLowerX(), this.getLowerY(), this.getLowerZ());
    }

    public Location getFirstCorner() {
        return this.firstCorner;
    }

    public Location getSecondCorner() {
        return this.secondCorner;
    }

    private int compareCoordinates(boolean upper, int a, int b) {
        if (upper) {
            return Math.max(b, a);
        }
        else {
            return Math.min(a, b);
        }
    }

    public FunnyBox toBox() {
        return FunnyBox.of(firstCorner, secondCorner);
    }

    @Override
    public EntityType getType() {
        return EntityType.REGION;
    }

    @Override
    public String toString() {
        return this.name;
    }

}