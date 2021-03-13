package net.dzikoysk.funnyguilds.data.database;

import com.google.common.collect.Sets;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DatabaseGuild {

    public static Guild deserialize(ResultSet rs) {
        if (rs == null) {
            return null;
        }

        String id = null;
        String name = null;

        try {
            id = rs.getString("uuid");
            name = rs.getString("name");
            String tag = rs.getString("tag");
            String os = rs.getString("owner");
            String dp = rs.getString("deputy");
            String home = rs.getString("home");
            String regionName = rs.getString("region");
            String membersString = rs.getString("members");
            boolean pvp = rs.getBoolean("pvp");
            long born = rs.getLong("born");
            long validity = rs.getLong("validity");
            long attacked = rs.getLong("attacked");
            long ban = rs.getLong("ban");
            int lives = rs.getInt("lives");

            if (name == null || tag == null || os == null) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
                return null;
            }

            UUID uuid = UUID.randomUUID();
            if (id != null && !id.isEmpty()) {
                uuid = UUID.fromString(id);
            }
            
            final User owner = User.get(os);
            
            Set<User> deputies = new HashSet<>();
            if (dp != null && !dp.isEmpty()) {
                deputies = UserUtils.getUsers(ChatUtils.fromString(dp));
            }

            Set<User> members = new HashSet<>();
            if (membersString != null && !membersString.equals("")) {
                members = UserUtils.getUsers(ChatUtils.fromString(membersString));
            }

            if (born == 0) {
                born = System.currentTimeMillis();
            }
            
            if (validity == 0) {
                validity = System.currentTimeMillis() + FunnyGuilds.getInstance().getPluginConfiguration().validityStart;
            }
            
            if (lives == 0) {
                lives = FunnyGuilds.getInstance().getPluginConfiguration().warLives;
            }

            final Object[] values = new Object[17];
            
            values[0] = uuid;
            values[1] = name;
            values[2] = tag;
            values[3] = owner;
            values[4] = LocationUtils.parseLocation(home);
            values[5] = RegionUtils.get(regionName);
            values[6] = members;
            values[7] = Sets.newHashSet();
            values[8] = Sets.newHashSet();
            values[9] = born;
            values[10] = validity;
            values[11] = attacked;
            values[12] = lives;
            values[13] = ban;
            values[14] = deputies;
            values[15] = pvp;

            return DeserializationUtils.deserializeGuild(values);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize guild (id: " + id + ", name: " + name + ")", ex);
        }
        
        return null;
    }

    public static void save(Guild guild) {
        String members = ChatUtils.toString(UserUtils.getNames(guild.getMembers()), false);
        String deputies = ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false);
        String allies = ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
        String enemies = ChatUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);
        SQLNamedStatement statement = SQLBasicUtils.getInsert(SQLDataModel.tabGuilds);

        statement.set("uuid",     guild.getUUID().toString());
        statement.set("name",     guild.getName());
        statement.set("tag",      guild.getTag());
        statement.set("owner",    guild.getOwner().getName());
        statement.set("home",     LocationUtils.toString(guild.getHome()));
        statement.set("region",   RegionUtils.toString(guild.getRegion()));
        statement.set("regions", "#abandoned");
        statement.set("members",  members);
        statement.set("deputy",   deputies);
        statement.set("allies",   allies);
        statement.set("enemies",  enemies);
        statement.set("points",   guild.getRank().getPoints());
        statement.set("lives",    guild.getLives());
        statement.set("born",     guild.getBorn());
        statement.set("validity", guild.getValidity());
        statement.set("attacked", guild.getAttacked());
        statement.set("ban",      guild.getBan());
        statement.set("pvp",      guild.getPvP());
        statement.set("info",     "");

        statement.executeUpdate();
    }

    public static void delete(Guild guild) {
        SQLNamedStatement statement = SQLBasicUtils.getDelete(SQLDataModel.tabGuilds);

        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
    }

    public static void updatePoints(Guild guild) {
        SQLTable table = SQLDataModel.tabGuilds;
        SQLNamedStatement statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points"));

        statement.set("points", guild.getRank().getPoints());
        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
    }

    private DatabaseGuild() {}

}
