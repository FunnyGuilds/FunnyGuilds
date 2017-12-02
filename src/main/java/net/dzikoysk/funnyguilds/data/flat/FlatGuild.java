package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.Yamler;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlatGuild {

    private final Guild guild;

    public FlatGuild(Guild guild) {
        this.guild = guild;
    }

    public static Guild deserialize(File file) {
        Yamler pc = new Yamler(file);
        String id = pc.getString("uuid");
        String name = pc.getString("name");
        String tag = pc.getString("tag");
        String os = pc.getString("owner");
        String dp = pc.getString("deputy");
        String hs = pc.getString("home");
        String region = pc.getString("region");
        List<String> ms = pc.getStringList("members");
        List<String> rgs = pc.getStringList("regions");
        List<String> als = pc.getStringList("allies");
        List<String> ens = pc.getStringList("enemies");
        boolean pvp = pc.getBoolean("pvp");
        long born = pc.getLong("born");
        long validity = pc.getLong("validity");
        long attacked = pc.getLong("attacked");
        long ban = pc.getLong("ban");
        int lives = pc.getInt("lives");
        pc = null;

        if (name == null) {
            FunnyGuilds.error("[Deserialize] Cannot deserialize guild! Caused by: name is null");
            return null;
        } else if (tag == null) {
            FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: tag is null");
            return null;
        } else if (os == null) {
            FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: owner is null");
            return null;
        } else if (region == null) {
            FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region is null");
            return null;
        }
        Region rg = RegionUtils.get(region);
        if (rg == null) {
            FunnyGuilds.error("[Deserialize] Cannot deserialize guild: " + name + "! Caused by: region (object) is null");
            return null;
        }

        UUID uuid = UUID.randomUUID();
        if (id != null) {
            uuid = UUID.fromString(id);
        }

        User owner = User.get(os);
        User deputy = null;
        if (dp != null) {
            deputy = User.get(dp);
        }

        Location home = rg.getCenter();
        if (hs != null) {
            home = Parser.parseLocation(hs);
        }

        if ((ms == null) || ms.isEmpty()) {
            ms = new ArrayList<>();
            ms.add(os);
        }
        List<User> members = UserUtils.getUsers(ms);

        List<String> regions = new ArrayList<>();
        if (rgs != null) {
            for (String n : rgs) {
                if (RegionUtils.get(n) != null) {
                    regions.add(n);
                }
            }
        }

        List<Guild> allies = new ArrayList<>();
        if (als != null) {
            for (String s : als) {
                allies.add(Guild.get(s));
            }
        }

        List<Guild> enemies = new ArrayList<>();
        if (ens != null) {
            for (String s : ens) {
                enemies.add(Guild.get(s));
            }
        }

        if (born == 0) {
            born = System.currentTimeMillis();
        }

        if (validity == 0) {
            validity = System.currentTimeMillis() + Settings.getConfig().validityStart;
        }

        if (lives == 0) {
            lives = Settings.getConfig().warLives;
        }

        Object[] values = new Object[17];
        values[0] = uuid;
        values[1] = name;
        values[2] = tag;
        values[3] = owner;
        values[4] = home;
        values[5] = region;
        values[6] = members;
        values[7] = regions;
        values[8] = allies;
        values[9] = enemies;
        values[10] = born;
        values[11] = validity;
        values[12] = attacked;
        values[13] = lives;
        values[14] = ban;
        values[15] = deputy;
        values[16] = pvp;

        return DeserializationUtils.deserializeGuild(values);
    }

    public boolean serialize() {

        if (guild.getName() == null) {
            FunnyGuilds.error("[Serialize] Cannot serialize guild! Caused by: name is null");
            return false;
        } else if (guild.getTag() == null) {
            FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: tag is null");
            return false;
        } else if (guild.getOwner() == null) {
            FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: owner is null");
            return false;
        } else if (guild.getRegion() == null) {
            FunnyGuilds.error("[Serialize] Cannot serialize guild: " + guild.getName() + "! Caused by: region is null");
            return false;
        } else if (guild.getUUID() == null) {
            guild.setUUID(UUID.randomUUID());
        }

        File file = Flat.loadCustomFile(BasicType.GUILD, guild.getName());
        Yamler pc = new Yamler(file);

        pc.set("uuid", guild.getUUID().toString());
        pc.set("name", guild.getName());
        pc.set("tag", guild.getTag());
        pc.set("owner", guild.getOwner().getName());
        pc.set("home", Parser.toString(guild.getHome()));
        pc.set("members", UserUtils.getNames(guild.getMembers()));
        pc.set("region", guild.getRegion());
        pc.set("regions", guild.getRegions());
        pc.set("allies", GuildUtils.getNames(guild.getAllies()));
        pc.set("enemies", GuildUtils.getNames(guild.getEnemies()));
        pc.set("born", guild.getBorn());
        pc.set("validity", guild.getValidity());
        pc.set("attacked", guild.getAttacked());
        pc.set("lives", guild.getLives());
        pc.set("ban", guild.getBan());
        pc.set("pvp", guild.getPvP());

        if (guild.getDeputy() != null) {
            pc.set("deputy", guild.getDeputy().getName());
        }

        pc.save();
        pc = null;
        return true;
    }

}
