package net.dzikoysk.funnyguilds.data.database;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseGuild {

    private final Guild guild;

    public DatabaseGuild(Guild guild) {
        this.guild = guild;
    }

    public static Guild deserialize(ResultSet rs) {
        if (rs == null) {
            return null;
        }
        
        try {
            String id = rs.getString("uuid");
            String name = rs.getString("name");
            String tag = rs.getString("tag");
            String os = rs.getString("owner");
            String dp = rs.getString("deputy");
            String home = rs.getString("home");
            String regionName = rs.getString("region");
            String m = rs.getString("members");
            boolean pvp = rs.getBoolean("pvp");
            long born = rs.getLong("born");
            long validity = rs.getLong("validity");
            long attacked = rs.getLong("attacked");
            long ban = rs.getLong("ban");
            int lives = rs.getInt("lives");

            if (name == null || tag == null || os == null) {
                FunnyLogger.error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
                return null;
            }

            UUID uuid = UUID.randomUUID();

            if (id != null) {
                uuid = UUID.fromString(id);
            }

            User owner = User.get(os);
            
            List<User> deputies = new ArrayList<>();

            if (dp != null && !dp.isEmpty()) {
                deputies = UserUtils.getUsers(ChatUtils.fromString(dp));
            }
            
            List<User> members = new ArrayList<>();

            if (m != null && !m.equals("")) {
                members = UserUtils.getUsers(ChatUtils.fromString(m));
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
            values[4] = Parser.parseLocation(home);
            values[5] = RegionUtils.get(regionName);
            values[6] = members;
            // values[7] = regions;
            values[8] = Lists.newArrayList();
            values[9] = Lists.newArrayList();
            values[10] = born;
            values[11] = validity;
            values[12] = attacked;
            values[13] = lives;
            values[14] = ban;
            values[15] = deputies;
            values[16] = pvp;

            return DeserializationUtils.deserializeGuild(values);
        } catch (Exception e) {
            if (FunnyLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    public void save(Database db) {
        String update = getInsert();
        if (update != null) {
            for (String query : update.split(";")) {
                db.executeUpdate(query);
            }
        }
    }

    public void delete() {
        if (guild == null) {
            return;
        }
        
        if (guild.getUUID() != null) {
            Database db = Database.getInstance();
            StringBuilder update = new StringBuilder();
            
            update.append("DELETE FROM `");
            update.append(Settings.getConfig().mysql.guildsTableName);
            update.append("` WHERE `uuid`='");
            update.append(guild.getUUID().toString());
            update.append("'");
            
            db.executeUpdate(update.toString());
        } else if (guild.getName() != null) {
            Database db = Database.getInstance();
            StringBuilder update = new StringBuilder();
            
            update.append("DELETE FROM `");
            update.append(Settings.getConfig().mysql.guildsTableName);
            update.append("` WHERE `name`='");
            update.append(guild.getName());
            update.append("'");
            
            db.executeUpdate(update.toString());
        }
    }

    public void updatePoints() {
        Database db = Database.getInstance();
        StringBuilder update = new StringBuilder();
        
        update.append("UPDATE `");
        update.append(Settings.getConfig().mysql.guildsTableName);
        update.append("` SET `points`=");
        update.append(guild.getRank().getPoints());
        update.append(" WHERE `uuid`='");
        update.append(guild.getUUID().toString());
        update.append("'");
        
        db.executeUpdate(update.toString());
    }

    public String getInsert() {
        StringBuilder sb = new StringBuilder();
        String members = ChatUtils.toString(UserUtils.getNames(guild.getMembers()), false);
        String deputies = ChatUtils.toString(UserUtils.getNames(guild.getDeputies()), false);
        String allies = ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
        String enemies = ChatUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);

        sb.append("INSERT INTO `");
        sb.append(Settings.getConfig().mysql.guildsTableName);
        sb.append("` (`uuid`, `name`, `tag`, `owner`, `home`, `region`, `members`, `regions`, `allies`, ");
        sb.append("`enemies`, `points`, `born`, `validity`, `attacked`, `ban`, `lives`, `pvp`, `deputy`");
        sb.append(") VALUES ('%uuid%','%name%','%tag%','%owner%','%home%','%region%',");
        sb.append("'%members%','%regions%','%allies%','%enemies%',%points%,%born%,");
        sb.append("%validity%,%attacked%,%ban%,%lives%,%pvp%,'%deputy%') ON DUPLICATE KEY UPDATE ");
        sb.append("`uuid`='%uuid%',`name`='%name%',`tag`='%tag%',`owner`='%owner%',`home`='%home%',");
        sb.append("`region`='%region%',`members`='%members%',`regions`='%regions%',`allies`='%allies%',");
        sb.append("`enemies`='%enemies%',`points`=%points%,`born`=%born%,`validity`=%validity%,");
        sb.append("`attacked`=%attacked%,`ban`=%ban%,`lives`=%lives%,`pvp`=%pvp%,`deputy`='%deputy%'");
        
        String is = sb.toString();
        
        is = StringUtils.replace(is, "%uuid%", guild.getUUID().toString());
        is = StringUtils.replace(is, "%name%", guild.getName());
        is = StringUtils.replace(is, "%tag%", guild.getTag());
        is = StringUtils.replace(is, "%owner%", guild.getOwner().getName());
        is = StringUtils.replace(is, "%home%", LocationUtils.toString(guild.getHome()));
        is = StringUtils.replace(is, "%region%", RegionUtils.toString(guild.getRegion()));
        is = StringUtils.replace(is, "%members%", members);
        is = StringUtils.replace(is, "%regions%", "#abandoned");
        is = StringUtils.replace(is, "%allies%", allies);
        is = StringUtils.replace(is, "%enemies%", enemies);
        is = StringUtils.replace(is, "%points%", Integer.toString(guild.getRank().getPoints()));
        is = StringUtils.replace(is, "%born%", Long.toString(guild.getBorn()));
        is = StringUtils.replace(is, "%validity%", Long.toString(guild.getValidity()));
        is = StringUtils.replace(is, "%attacked%", Long.toString(guild.getAttacked()));
        is = StringUtils.replace(is, "%ban%", Long.toString(guild.getBan()));
        is = StringUtils.replace(is, "%lives%", Integer.toString(guild.getLives()));
        is = StringUtils.replace(is, "%pvp%", Boolean.toString(guild.getPvP()));
        is = StringUtils.replace(is, "%deputy%", deputies);
        
        return is;
    }

}
