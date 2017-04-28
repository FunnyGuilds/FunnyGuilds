package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;

import java.sql.ResultSet;

public class DatabaseUser {

    private final User user;

    public DatabaseUser(User user) {
        this.user = user;
    }

    public void save(Database db) {
        db.openConnection();
        String update = getInsert();
        if (update != null) {
            for (String query : update.split(";")) {
                try {
                    db.executeUpdate(query);
                } catch (Exception e) {
                    FunnyGuilds.error("[MySQL] Update: " + query);
                    if (FunnyGuilds.exception(e.getCause())) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void updatePoints() {
        Database db = Database.getInstance();
        db.openConnection();
        StringBuilder update = new StringBuilder();
        update.append("UPDATE `users` SET `points`='");
        update.append(user.getRank().getPoints());
        update.append("' WHERE `uuid`='");
        update.append(user.getUUID().toString());
        update.append("'");
        db.executeUpdate(update.toString());
        db.closeConnection();
    }

    public String getInsert() {
        if (user.getUUID() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `users` (`uuid`, `name`, `points`, `kills`, `deaths`, `ban`, `reason`) VALUES (");
        sb.append("'" + user.getUUID().toString() + "',");
        sb.append("'" + user.getName() + "',");
        sb.append("'" + user.getRank().getPoints() + "',");
        sb.append("'" + user.getRank().getKills() + "',");
        sb.append("'" + user.getRank().getDeaths() + "',");
        sb.append("'" + user.getBan() + "',");
        sb.append("'" + user.getReason() + "'");
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append("`name`='" + user.getName() + "',");
        sb.append("`points`='" + user.getRank().getPoints() + "',");
        sb.append("`kills`='" + user.getRank().getKills() + "',");
        sb.append("`deaths`='" + user.getRank().getDeaths() + "',");
        sb.append("`ban`='" + user.getBan() + "',");
        sb.append("`reason`='" + user.getReason() + "'");
        if (user.hasGuild()) {
            sb.append("; UPDATE `users` SET `guild`='");
            sb.append(user.getGuild().getName());
            sb.append("' WHERE `uuid`='");
            sb.append(user.getUUID().toString());
            sb.append("'");
        }
        else {
            sb.append("; UPDATE `users` SET `guild`=NULL WHERE `uuid`='");
            sb.append(user.getUUID().toString());
            sb.append("'");
        }
        return sb.toString();
    }

    public static User deserialize(ResultSet rs) {
        if (rs == null) {
            return null;
        }
        try {
            String uuid = rs.getString("uuid");
            String name = rs.getString("name");
            int points = rs.getInt("points");
            int kills = rs.getInt("kills");
            int deaths = rs.getInt("deaths");
            long ban = rs.getLong("ban");
            String reason = rs.getString("reason");

            Object[] values = new Object[7];
            values[0] = uuid;
            values[1] = name;
            values[2] = points;
            values[3] = kills;
            values[4] = deaths;
            values[5] = ban;
            values[6] = reason;
            return DeserializationUtils.deserializeUser(values);
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
