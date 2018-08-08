package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.YamlWrapper;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.io.File;
import java.util.*;

public class FlatGuild {

    private final Guild guild;

    public FlatGuild(Guild guild) {
        this.guild = guild;
    }

    public static Guild deserialize(File file) {
        PluginConfig configuration = Settings.getConfig();
        YamlWrapper data = new YamlWrapper(file);
        
        String id = data.getString("uuid");
        String name = data.getString("name");
        String tag = data.getString("tag");
        String ownerName = data.getString("owner");
        String deputyName = data.getString("deputy");
        String hs = data.getString("home");
        String regionName = data.getString("region");
        List<String> memberNames = data.getStringList("members");
        // List<String> regionNames = data.getStringList("regions");
        List<String> allyNames = data.getStringList("allies");
        List<String> enemyNames = data.getStringList("enemies");
        boolean pvp = data.getBoolean("pvp");
        long born = data.getLong("born");
        long validity = data.getLong("validity");
        long attacked = data.getLong("attacked");
        long ban = data.getLong("ban");
        int lives = data.getInt("lives");

        if (name == null) {
            FunnyGuildsLogger.error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
            return null;
        }
        else if (tag == null) {
            FunnyGuildsLogger.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
            return null;
        }
        else if (ownerName == null) {
            FunnyGuildsLogger.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
            return null;
        }
        else if (regionName == null && configuration.regionsEnabled) {
            FunnyGuildsLogger.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
            return null;
        }
        
        Region region = RegionUtils.get(regionName);

        if (region == null && configuration.regionsEnabled) {
            FunnyGuildsLogger.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
            return null;
        }

        UUID uuid = UUID.randomUUID();

        if (id != null) {
            uuid = UUID.fromString(id);
        }

        User owner = User.get(ownerName);
        Set<User> deputies = new HashSet<>(1);

        if (deputyName != null && !deputyName.isEmpty()) {
            deputies = UserUtils.getUsers(ChatUtils.fromString(deputyName));
        }

        Location home = null;

        if (region !=null) {
            home = region.getCenter();

            if (hs != null) {
                home = LocationUtils.parseLocation(hs);
            }
        }

        if (memberNames == null || memberNames.isEmpty()) {
            memberNames = new ArrayList<>();
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

        Object[] values = new Object[17];
        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = owner;
        values[4] = home;
        values[5] = region;
        values[6] = members;
        // values[7] = regions;
        values[8] = allies;
        values[9] = enemies;
        values[10] = born;
        values[11] = validity;
        values[12] = attacked;
        values[13] = lives;
        values[14] = ban;
        values[15] = deputies;
        values[16] = pvp;
        
        return DeserializationUtils.deserializeGuild(values);
    }

    public boolean serialize() {
        if (guild.getName() == null) {
            FunnyGuildsLogger.error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return false;
        } else if (guild.getTag() == null) {
            FunnyGuildsLogger.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return false;
        } else if (guild.getOwner() == null) {
            FunnyGuildsLogger.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return false;
        } else if (guild.getRegion() == null && Settings.getConfig().regionsEnabled) {
            FunnyGuildsLogger.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return false;
        } else if (guild.getUUID() == null) {
            guild.setUUID(UUID.randomUUID());
        }

        File file = Flat.loadCustomFile(BasicType.GUILD, guild.getName());
        YamlWrapper pc = new YamlWrapper(file);

        pc.set("uuid", guild.getUUID().toString());
        pc.set("name", guild.getName());
        pc.set("tag", guild.getTag());
        pc.set("owner", guild.getOwner().getName());
        pc.set("home", LocationUtils.toString(guild.getHome()));
        pc.set("members", UserUtils.getNames(guild.getMembers()));
        pc.set("region", RegionUtils.toString(guild.getRegion()));
        pc.set("regions", null);
        pc.set("allies", GuildUtils.getNames(guild.getAllies()));
        pc.set("enemies", GuildUtils.getNames(guild.getEnemies()));
        pc.set("born", guild.getBorn());
        pc.set("validity", guild.getValidity());
        pc.set("attacked", guild.getAttacked());
        pc.set("lives", guild.getLives());
        pc.set("ban", guild.getBan());
        pc.set("pvp", guild.getPvP());
        pc.set("deputy", ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false));
        
        pc.save();
        return true;
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
