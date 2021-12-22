package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Region extends AbstractMutableEntity {

    private String name;
    private Guild guild;
    private Location center;
    private World world;
    private int size;
    private int enlarge;
    private Location firstCorner;
    private Location secondCorner;

    public Region(String name) {
        this.name = name;
    }

    public Region(Guild guild, Location loc, int size) {
        this(guild.getName());
        this.guild = guild;
        this.world = loc.getWorld();
        this.center = loc;
        this.size = size;
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

    public boolean isIn(Location loc) {
        if (loc == null || this.firstCorner == null || this.secondCorner == null) {
            return false;
        }

        if (!this.center.getWorld().equals(loc.getWorld())) {
            return false;
        }

        if (loc.getBlockX() > this.getLowerX() && loc.getBlockX() < this.getUpperX()) {
            if (loc.getBlockY() > this.getLowerY() && loc.getBlockY() < this.getUpperY()) {
                return loc.getBlockZ() > this.getLowerZ() && loc.getBlockZ() < this.getUpperZ();
            }
        }

        return false;
    }

    private int compareCoordinates(boolean upper, int a, int b) {
        if (upper) {
            return Math.max(b, a);
        }
        else {
            return Math.min(a, b);
        }
    }

    public void setName(String s) {
        this.name = s;
        super.markChanged();
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        super.markChanged();
    }

    public void setCenter(Location loc) {
        this.center = loc;
        this.world = loc.getWorld();
        this.update();
    }

    public void setSize(int i) {
        this.size = i;
        this.update();
    }

    public void setEnlarge(int i) {
        this.enlarge = i;
        super.markChanged();
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Location getCenter() {
        return this.center;
    }

    public Location getHeart() {
        return getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation();
    }

    public int getSize() {
        return this.size;
    }

    public World getWorld() {
        return this.world;
    }

    public int getEnlarge() {
        return this.enlarge;
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

    public Location getFirstCorner() {
        return this.firstCorner;
    }

    public Location getSecondCorner() {
        return this.secondCorner;
    }

    @Override
    public EntityType getType() {
        return EntityType.REGION;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}