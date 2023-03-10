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
import panda.std.Option;

public class Region extends AbstractMutableEntity {

    private String name;
    private Guild guild;

    private World world;
    private Location center;
    private int enlargementLevel;
    private int size;

    private Location firstCorner;
    private Location secondCorner;

    public Region(String name, Location center) {
        this.name = name;
        this.world = center.getWorld();
        this.center = center;
    }

    public Region(Guild guild, Location center, int defaultSize) {
        this(guild.getName(), center);

        this.guild = guild;
        this.size = defaultSize;

        this.update();
    }

    public synchronized void update() {
        super.markChanged();

        if (this.center == null) {
            return;
        }

        if (this.size < 1) {
            return;
        }

        if (this.world == null) {
            this.world = Bukkit.getWorlds().get(0);
        }

        if (this.world != null) {
            int lx = this.center.getBlockX() + this.size;
            int lz = this.center.getBlockZ() + this.size;

            int px = this.center.getBlockX() - this.size;
            int pz = this.center.getBlockZ() - this.size;

            Vector l = new Vector(lx, LocationUtils.getMinHeight(this.world), lz);
            Vector p = new Vector(px, this.world.getMaxHeight(), pz);

            this.firstCorner = l.toLocation(this.world);
            this.secondCorner = p.toLocation(this.world);
        }
    }

    public boolean isIn(Location location) {
        if (location == null || this.firstCorner == null || this.secondCorner == null) {
            return false;
        }

        if (this.world == null) {
            return false;
        }

        if (!this.world.equals(location.getWorld())) {
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

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        super.markChanged();
    }

    public World getWorld() {
        return this.world;
    }

    public Location getCenter() {
        return this.center;
    }

    public Option<Location> getHeart() {
        return this.getHeartBlock().map(Block::getLocation);
    }

    public Option<Block> getHeartBlock() {
        return Option.of(this.center)
                .map(Location::getBlock)
                .map(block -> block.getRelative(BlockFace.DOWN));
    }

    void setCenter(Location location) {
        this.center = location;
        this.world = location.getWorld();
        this.update();
    }

    public int getEnlargementLevel() {
        return this.enlargementLevel;
    }

    void setEnlargementLevel(int enlargementLevel) {
        this.enlargementLevel = enlargementLevel;
        super.markChanged();
    }

    public int getSize() {
        return this.size;
    }

    void setSize(int size) {
        this.size = size;
        this.update();
    }

    public int getUpperX() {
        return compareCoordinates(true, this.firstCorner.getBlockX(), this.secondCorner.getBlockX());
    }

    public int getUpperY() {
        return compareCoordinates(true, this.firstCorner.getBlockY(), this.secondCorner.getBlockY());
    }

    public int getUpperZ() {
        return compareCoordinates(true, this.firstCorner.getBlockZ(), this.secondCorner.getBlockZ());
    }

    public int getLowerX() {
        return compareCoordinates(false, this.firstCorner.getBlockX(), this.secondCorner.getBlockX());
    }

    public int getLowerY() {
        return compareCoordinates(false, this.firstCorner.getBlockY(), this.secondCorner.getBlockY());
    }

    public int getLowerZ() {
        return compareCoordinates(false, this.firstCorner.getBlockZ(), this.secondCorner.getBlockZ());
    }

    public Location getUpperCorner() {
        return new Location(this.world, this.getUpperX(), this.getUpperY(), this.getUpperZ());
    }

    public Location getLowerCorner() {
        return new Location(this.world, this.getLowerX(), this.getLowerY(), this.getLowerZ());
    }

    public Location getFirstCorner() {
        return this.firstCorner;
    }

    public Location getSecondCorner() {
        return this.secondCorner;
    }

    private static int compareCoordinates(boolean upper, int a, int b) {
        if (upper) {
            return Math.max(b, a);
        }
        else {
            return Math.min(a, b);
        }
    }

    public FunnyBox toBox() {
        return FunnyBox.of(this.firstCorner, this.secondCorner);
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