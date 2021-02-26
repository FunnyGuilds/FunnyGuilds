package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;

import java.sql.ResultSet;

public class DatabaseUser {

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

    public static void save(User user) {
        SQLNamedStatement namedPS = SQLBasicUtils.getInsert(SQLDataModel.tabUsers);

        namedPS.set("uuid", user.getUUID().toString());
        namedPS.set("name", user.getName());
        namedPS.set("points", user.getRank().getPoints());
        namedPS.set("kills", user.getRank().getKills());
        namedPS.set("deaths", user.getRank().getDeaths());
        namedPS.set("guild", user.hasGuild() ? "'" +  user.getGuild().getName() + "'" : "");
        namedPS.set("ban", user.isBanned() ? user.getBan().getBanTime() : 0);
        namedPS.set("reason", (user.isBanned() ? user.getBan().getReason() : null));
        namedPS.executeUpdate();
    }

    public static void updatePoints(User user) {
        SQLTable table = SQLDataModel.tabUsers;
        SQLNamedStatement namedPS = SQLBasicUtils.getUpdate(table, table.getSQLElement("points"));

        namedPS.set("points", user.getRank().getPoints());
        namedPS.set("uuid", user.getUUID().toString());
        namedPS.executeUpdate();
    }

    private DatabaseUser() {}
}
