package net.dzikoysk.funnyguilds.basic;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Player")
public class OfflineUser implements OfflinePlayer, ConfigurationSerializable {

    private static int type;
    private GameProfile profile;
    private String name;
    private UUID uuid;

    public OfflineUser(String name) {
        this.name = name;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        this.init();
        try {
            if (type == 1)
                this.profile = GameProfile.class.getConstructor(new Class<?>[]{
                        String.class,
                        String.class
                }).newInstance(this.uuid.toString(), name);
            else if (type == 2)
                this.profile = GameProfile.class.getConstructor(new Class<?>[]{
                        UUID.class,
                        String.class
                }).newInstance(this.uuid, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @SuppressWarnings("deprecation")
    public OfflinePlayer getReal() {
        return Bukkit.getOfflinePlayer(getName());
    }

    @Override
    public boolean isOp() {
        return getReal().isOp();
    }

    @Override
    public void setOp(boolean b) {
        getReal().setOp(b);
    }

    @Override
    public boolean isBanned() {
        if (getName() == null) return false;
        return Bukkit.getServer().getBannedPlayers().contains(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBanned(boolean value) {
        getReal().setBanned(value);
    }

    @Override
    public boolean isWhitelisted() {
        return Bukkit.getWhitelistedPlayers().contains(this);
    }

    @Override
    public void setWhitelisted(boolean value) {
        Bukkit.getWhitelistedPlayers().add(this);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("UUID", this.profile.getId().toString());
        result.put("name", this.profile.getName());
        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        if (args.get("name") == null) return null;
        return new OfflineUser((String) args.get("name"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[UUID=" + this.profile.getId() + "]";
    }

    @Override
    public Player getPlayer() {
        if (getName() == null) return null;
        return Bukkit.getPlayer(getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OfflinePlayer) {
            return ((OfflinePlayer) obj).getName().equalsIgnoreCase(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (getUniqueId() != null ? getUniqueId().hashCode() : 0);
        return hash;
    }

    @Override
    public long getFirstPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();
        return 0L;
    }

    @Override
    public long getLastPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getLastPlayed();
        return 0L;
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.getLastPlayed() != 0;
    }

    @Override
    public Location getBedSpawnLocation() {
        return getReal().getBedSpawnLocation();
    }

    @SuppressWarnings("rawtypes")
    private void init() {
        if (type != 0) return;
        for (Constructor c : GameProfile.class.getConstructors()) {
            if (Arrays.equals(c.getParameterTypes(), new Class<?>[]{String.class, String.class})) type = 1;
            else if (Arrays.equals(c.getParameterTypes(), new Class<?>[]{UUID.class, String.class})) type = 2;
            else FunnyGuilds.error("GameProfile constructor not found!");
        }
    }

}
