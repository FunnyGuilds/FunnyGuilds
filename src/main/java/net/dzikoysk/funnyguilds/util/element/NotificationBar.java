package net.dzikoysk.funnyguilds.util.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class NotificationBar {

    private static PlayerMap<FakeDragon> bars = new PlayerMap<FakeDragon>();

    public static void remove(Player player) {
        if (has(player)) {
            sendPacket(player, bars.get(player).getDestroyPacket());
            bars.remove(player);
        }
    }

    public static void set(final Player player, String text, float percent, int time) {

        remove(player);
        FakeDragon dragon = bars.containsKey(player) ? bars.get(player) : null;

        if (text.length() > 64) {
            text = text.substring(0, 63);
        }
        if (percent > 1.0f) {
            percent = 1.0f;
        }
        if (percent < 0.05f) {
            percent = 0.05f;
        }

        if (text.isEmpty() && dragon != null) {
            remove(player);
        }

        if (dragon == null) {
            dragon = new FakeDragon(player.getLocation().add(0, -200, 0), text, percent);
            sendPacket(player, dragon.getSpawnPacket());
            bars.put(player, dragon);
        } else {
            dragon.setName(text);
            dragon.setHealth(percent);
            sendPacket(player, dragon.getMetaPacket(dragon.getWatcher()));
            sendPacket(player, dragon.getTeleportPacket(player.getLocation().add(0, -200, 0)));
        }

        Bukkit.getScheduler().runTaskLater(FunnyGuilds.getInstance(), new Runnable() {
            @Override
            public void run() {
                NotificationBar.remove(player);
            }
        }, time * 20);
    }

    public static boolean has(Player player) {
        return bars.containsKey(player) && bars.get(player) != null;
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = Reflections.getHandle(player);
            Field connectionField = nmsPlayer.getClass().getField("playerConnection");
            Object connection = connectionField.get(nmsPlayer);
            Method sendPacket = Reflections.getMethod(connection.getClass(), "sendPacket");
            sendPacket.invoke(connection, packet);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    private static class FakeDragon {

        private static final int MAX_HEALTH = 200;
        private int id;
        private int x;
        private int y;
        private int z;
        private int pitch = 0;
        private int yaw = 0;
        private byte xvel = 0;
        private byte yvel = 0;
        private byte zvel = 0;
        private float health;
        private boolean visible = false;
        private String name;
        private Object world;

        private Object dragon;

        public FakeDragon(Location loc, String name, float percent) {
            this.name = name;
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.health = percent * MAX_HEALTH;
            this.world = Reflections.getHandle(loc.getWorld());
        }

        public Object getMetaPacket(Object watcher) {
            try {
                Class<?> watcherClass = Reflections.getCraftClass("DataWatcher");
                Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutEntityMetadata");
                return packetClass.getConstructor(new Class<?>[]{int.class, watcherClass, boolean.class}).newInstance(id, watcher, true);
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public Object getTeleportPacket(Location loc) {
            try {
                Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutEntityTeleport");
                return packetClass.getConstructor(new Class<?>[]{int.class, int.class, int.class, int.class, byte.class, byte.class}).newInstance(
                        this.id, loc.getBlockX() * 32, loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360));
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public void setHealth(float percent) {
            this.health = percent / MAX_HEALTH;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getSpawnPacket() {
            Class<?> Entity = Reflections.getCraftClass("Entity");
            Class<?> EntityLiving = Reflections.getCraftClass("EntityLiving");
            Class<?> EntityEnderDragon = Reflections.getCraftClass("EntityEnderDragon");

            try {
                dragon = EntityEnderDragon.getConstructor(Reflections.getCraftClass("World")).newInstance(world);

                Reflections.getMethod(EntityEnderDragon, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(dragon, x, y, z, pitch, yaw);
                Reflections.getMethod(EntityEnderDragon, "setInvisible", boolean.class).invoke(dragon, visible);
                Reflections.getMethod(EntityEnderDragon, "setCustomName", String.class).invoke(dragon, name);
                Reflections.getMethod(EntityEnderDragon, "setHealth", float.class).invoke(dragon, health);

                Reflections.getField(Entity, "motX").set(dragon, xvel);
                Reflections.getField(Entity, "motY").set(dragon, yvel);
                Reflections.getField(Entity, "motZ").set(dragon, zvel);

                this.id = (Integer) Reflections.getMethod(EntityEnderDragon, "getId").invoke(dragon);

                Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutSpawnEntityLiving");
                return packetClass.getConstructor(new Class<?>[]{EntityLiving}).newInstance(dragon);
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public Object getDestroyPacket() {
            try {
                Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutEntityDestroy");
                return packetClass.getConstructor(new Class<?>[]{int[].class}).newInstance(new int[]{id});
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public Object getWatcher() {
            Class<?> Entity = Reflections.getCraftClass("Entity");
            Class<?> DataWatcher = Reflections.getCraftClass("DataWatcher");

            try {
                Object watcher = DataWatcher.getConstructor(new Class<?>[]{Entity}).newInstance(dragon);
                Method a = Reflections.getMethod(DataWatcher, "a", int.class, Object.class);

                a.invoke(watcher, 0, visible ? (byte) 0 : (byte) 0x20);
                a.invoke(watcher, 6, health);
                a.invoke(watcher, 7, 0);
                a.invoke(watcher, 8, (byte) 0);
                a.invoke(watcher, 10, name);
                a.invoke(watcher, 11, (byte) 1);
                return watcher;
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
                return null;
            }
        }

    }

    private static class PlayerMap<V> implements Map<Player, V> {

        private final V defaultValue;
        private final Map<String, V> contents;

        public PlayerMap() {
            contents = new HashMap<String, V>();
            defaultValue = null;
        }

        public void clear() {
            contents.clear();
        }

        public boolean containsKey(Object key) {
            if (key instanceof Player) {
                return contents.containsKey(((Player) key).getName());
            }
            if (key instanceof String) {
                return contents.containsKey(key);
            }
            return false;
        }

        public boolean containsValue(Object value) {
            return contents.containsValue(value);
        }

        public Set<Entry<Player, V>> entrySet() {
            Set<Entry<Player, V>> toReturn = new HashSet<Entry<Player, V>>();
            for (String name : contents.keySet()) {
                toReturn.add(new PlayerEntry(Bukkit.getPlayer(name), contents.get(name)));
            }
            return toReturn;
        }

        public V get(Object key) {
            V result = null;
            if (key instanceof Player) {
                result = contents.get(((Player) key).getName());
            }
            if (key instanceof String) {
                result = contents.get(key);
            }
            return (result == null) ? defaultValue : result;
        }

        public Set<Player> keySet() {
            Set<Player> toReturn = new HashSet<Player>();
            for (String name : contents.keySet()) {
                toReturn.add(Bukkit.getPlayer(name));
            }
            return toReturn;
        }

        public V put(Player key, V value) {
            if (key == null) {
                return null;
            }
            return contents.put(key.getName(), value);
        }

        public void putAll(Map<? extends Player, ? extends V> map) {
            for (Entry<? extends Player, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        public int size() {
            return contents.size();
        }

        public Collection<V> values() {
            return contents.values();
        }

        public V remove(Object key) {
            if (key instanceof Player) {
                return contents.remove(((Player) key).getName());
            }
            if (key instanceof String) {
                return contents.remove(key);
            }
            return null;
        }

        public boolean isEmpty() {
            return contents.isEmpty();
        }

        public String toString() {
            return contents.toString();
        }

        public class PlayerEntry implements Map.Entry<Player, V> {

            private Player key;
            private V value;

            public PlayerEntry(Player key, V value) {
                this.key = key;
                this.value = value;
            }

            public V setValue(V value) {
                V toReturn = this.value;
                this.value = value;
                return toReturn;
            }

            public Player getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }

        }
    }
}