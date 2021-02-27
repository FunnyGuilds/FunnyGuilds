package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlatGuild {

    private final Guild guild;

    public FlatGuild(Guild guild) {
        this.guild = guild;
    }

    public static Guild deserialize(File file) {
        PluginConfiguration configuration = FunnyGuilds.getInstance().getPluginConfiguration();
        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        String tag = wrapper.getString("tag");
        String ownerName = wrapper.getString("owner");
        String deputyName = wrapper.getString("deputy");
        String hs = wrapper.getString("home");
        String regionName = wrapper.getString("region");
        boolean pvp = wrapper.getBoolean("pvp");
        long born = wrapper.getLong("born");
        long validity = wrapper.getLong("validity");
        long attacked = wrapper.getLong("attacked");
        long ban = wrapper.getLong("ban");
        int lives = wrapper.getInt("lives");

        if (name == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
            return null;
        }

        if (tag == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
            return null;
        }

        if (ownerName == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
            return null;
        }

        if (regionName == null && configuration.regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
            return null;
        }

        Set<String> memberNames = loadSet(wrapper, "members");
        Set<String> allyNames = loadSet(wrapper, "allies");
        Set<String> enemyNames = loadSet(wrapper, "enemies");

        final Region region = RegionUtils.get(regionName);
        if (region == null && configuration.regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
            return null;
        }

        UUID uuid = UUID.randomUUID();
        if (id != null && !id.isEmpty()) {
            uuid = UUID.fromString(id);
        }

        final User owner = User.get(ownerName);

        Set<User> deputies = ConcurrentHashMap.newKeySet(1);
        if (deputyName != null && !deputyName.isEmpty()) {
            deputies = UserUtils.getUsers(ChatUtils.fromString(deputyName));
        }

        Location home = null;

        if (region != null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        if (memberNames == null || memberNames.isEmpty()) {
            memberNames = new HashSet<>();
            memberNames.add(ownerName);
        }

        Set<User> members = UserUtils.getUsers(memberNames);
        Set<Guild> allies = loadGuilds(allyNames);
        Set<Guild> enemies = loadGuilds(enemyNames);

        if (born == 0) {
            born = System.currentTimeMillis();
        }

        if (validity == 0) {
            validity = System.currentTimeMillis() + configuration.validityStart;
        }

        if (lives == 0) {
            lives = configuration.warLives;
        }

        final Object[] values = new Object[17];

        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = owner;
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = allies;
        values[8] = enemies;
        values[9] = born;
        values[10] = validity;
        values[11] = attacked;
        values[12] = lives;
        values[13] = ban;
        values[14] = deputies;
        values[15] = pvp;

        return DeserializationUtils.deserializeGuild(values);
    }

    public boolean serialize(FlatDataModel flatDataModel) {
        if (guild.getName() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return false;
        }

        if (guild.getTag() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return false;
        }

        if (guild.getOwner() == null) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return false;
        }

        if (guild.getRegion() == null && FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return false;
        }

        File file = flatDataModel.loadCustomFile(BasicType.GUILD, guild.getName());
        YamlWrapper wrapper = new YamlWrapper(file);

        wrapper.set("uuid", guild.getUUID().toString());
        wrapper.set("name", guild.getName());
        wrapper.set("tag", guild.getTag());
        wrapper.set("owner", guild.getOwner().getName());
        wrapper.set("home", LocationUtils.toString(guild.getHome()));
        wrapper.set("members", UserUtils.getNames(guild.getMembers()));
        wrapper.set("region", RegionUtils.toString(guild.getRegion()));
        wrapper.set("regions", null);
        wrapper.set("allies", GuildUtils.getNames(guild.getAllies()));
        wrapper.set("enemies", GuildUtils.getNames(guild.getEnemies()));
        wrapper.set("born", guild.getBorn());
        wrapper.set("validity", guild.getValidity());
        wrapper.set("attacked", guild.getAttacked());
        wrapper.set("lives", guild.getLives());
        wrapper.set("ban", guild.getBan());
        wrapper.set("pvp", guild.getPvP());
        wrapper.set("deputy", ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false));

        wrapper.save();
        return true;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> loadSet(YamlWrapper data, String key) {
        Object collection = data.get(key);

        if (collection instanceof List) {
            return new HashSet<>((List<String>) collection);
        }
        else if (collection instanceof Set) {
            return (Set<String>) collection;
        }

        return null;
    }

    private static Set<Guild> loadGuilds(Collection<String> guilds) {
        Set<Guild> set = new HashSet<>();

        if (guilds == null) {
            return set;
        }

        for (String guildName : guilds) {
            Guild guild = GuildUtils.getByName(guildName);

            if (guild != null) {
                set.add(guild);
            }
        }

        return set;
    }

}
