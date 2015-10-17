package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.core.DataCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;

public class Region implements Basic {

    private Location center;
    private int size;
    private Guild guild;
    private int enlarge;
    private World world;
    private Location l;
    private Location p;

    public Region(Location location, int size) {
        this.world = location.getWorld();
        this.center = location;
        this.size = size;
        this.update();
        RegionUtils.addRegion(this);
    }

    public Region(Guild guild, Location location, int size) {
        this(location, size);
        this.guild = guild;
    }

    public static Region get(String name) {
        for (Region region : RegionUtils.getRegions())
            if (region.getName() != null && region.getName().equalsIgnoreCase(name))
                return region;
        return null;
    }

    public void update() {
        if (this.center == null)
            return;
        if (this.size < 1)
            return;
        if (this.world == null)
            this.world = Bukkit.getWorlds().get(0);
        if (this.world != null) {
            int lx = this.center.getBlockX() + this.size;
            int lz = this.center.getBlockZ() + this.size;

            int px = this.center.getBlockX() - this.size;
            int pz = this.center.getBlockZ() - this.size;

            Vector l = new Vector(lx, 0, lz);
            Vector p = new Vector(px, this.world.getMaxHeight(), pz);

            this.l = l.toLocation(this.world);
            this.p = p.toLocation(this.world);
        }
    }

    public void delete() {
        RegionUtils.removeRegion(this);
        this.world = null;
        this.center = null;
        this.l = null;
        this.p = null;
    }

    public boolean isIn(Location loc) {
        this.update();
        if (loc == null || l == null || p == null)
            return false;
        if (!center.getWorld().equals(loc.getWorld()))
            return false;
        if (loc.getBlockX() > getLowerX() && loc.getBlockX() < getUpperX())
            if (loc.getBlockY() > getLowerY() && loc.getBlockY() < getUpperY())
                if (loc.getBlockZ() > getLowerZ() && loc.getBlockZ() < getUpperZ())
                    return true;
        return false;
    }

    @Override
    public String getName() {
        return this.guild != null ? this.guild.getName() : this.center.toString();
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Location getCenter() {
        return this.center;
    }

    public void setCenter(Location loc) {
        this.center = loc;
        this.world = loc.getWorld();
        this.update();
        this.passVariable("center");
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int i) {
        this.size = i;
        this.update();
        this.passVariable("size");
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
        this.update();
        this.passVariable("world");
    }

    public Location getL() {
        return this.l;
    }

    public void setL(Location loc) {
        this.l = loc;
    }

    public Location getP() {
        return this.p;
    }

    public void setP(Location loc) {
        this.p = loc;
    }

    public int getEnlarge() {
        return this.enlarge;
    }

    public void setEnlarge(int i) {
        this.enlarge = i;
        this.passVariable("enlarge");
    }

    public int getUpperX() {
        int x = this.l.getBlockX();
        int y = this.p.getBlockX();
        return y < x ? x : y;
    }

    public int getUpperY() {
        int x = this.l.getBlockY();
        int y = this.p.getBlockY();
        return y < x ? x : y;
    }

    public int getUpperZ() {
        int x = this.l.getBlockZ();
        int y = this.p.getBlockZ();
        return y < x ? x : y;
    }

    public int getLowerX() {
        int x = this.l.getBlockX();
        int y = this.p.getBlockX();
        return x < y ? y : x;
    }

    public int getLowerY() {
        int x = this.l.getBlockY();
        int y = this.p.getBlockY();
        return x < y ? y : x;
    }

    public int getLowerZ() {
        int x = this.l.getBlockZ();
        int y = this.p.getBlockZ();
        return x < y ? y : x;
    }

    @Override
    public void passVariable(String... field) {
        DataCore.getInstance().save(this, field);
    }

    @Override
    public Object getVariable(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getVariable(String field, Class<T> clazz) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return (T) f.get(this);
    }

    @Override
    public BasicType getType() {
        return BasicType.REGION;
    }

    @Override
    public String toString() {
        return this.guild != null ? this.guild.getName() : this.center.toString();
    }

}
