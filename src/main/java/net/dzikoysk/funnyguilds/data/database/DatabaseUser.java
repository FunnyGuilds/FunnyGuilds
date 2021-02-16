package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;

public class DatabaseUser {

    private final User user;

    public DatabaseUser(User user) {
        this.user = user;
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
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not deserialize user", ex);
        }

        return null;
    }

    public void save(Database db) {
        String update = getInsert();
        if (update != null) {
            for (String query : update.split(";")) {
                try {
                    db.executeUpdate(query);
                }
                catch (Exception ex) {
                    FunnyGuilds.getInstance().getPluginLogger().error("[MySQL] Update: " + query);
                    FunnyGuilds.getInstance().getPluginLogger().error("Could not save user to database", ex);
                }
            }
        }
    }

    public void updatePoints() {
        Database db = Database.getInstance();
        StringBuilder update = new StringBuilder();

        update.append("UPDATE `");
        update.append(FunnyGuilds.getInstance().getPluginConfiguration().mysql.usersTableName);
        update.append("` SET `points`='");
        update.append(user.getRank().getPoints());
        update.append("' WHERE `uuid`='");
        update.append(user.getUUID().toString());
        update.append("'");

        db.executeUpdate(update.toString());
    }

    public String getInsert() {
        if (user.getUUID() == null) {
            return null;
        }

        String is = SQLDataModel.getBasicsInsert(SQLDataModel.tabUsers);

        is = StringUtils.replace(is, "%uuid%", user.getUUID().toString());
        is = StringUtils.replace(is, "%name%", user.getName());
        is = StringUtils.replace(is, "%points%", String.valueOf(user.getRank().getPoints()));
        is = StringUtils.replace(is, "%kills%", String.valueOf(user.getRank().getKills()));
        is = StringUtils.replace(is, "%deaths%", String.valueOf(user.getRank().getDeaths()));
        is = StringUtils.replace(is, "'%guild%'", user.hasGuild() ? "'" +  user.getGuild().getName() + "'" : "NULL");
        is = StringUtils.replace(is, "%ban%", String.valueOf((user.isBanned() ? user.getBan().getBanTime() : 0)));
        is = StringUtils.replace(is, "%reason%", (user.isBanned() ? user.getBan().getReason() : null));

        return is;
    }

}
