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
            int assists = rs.getInt("assists");
            int logouts = rs.getInt("logouts");
            long ban = rs.getLong("ban");
            String reason = rs.getString("reason");

            Object[] values = new Object[9];

            values[0] = uuid;
            values[1] = name;
            values[2] = points;
            values[3] = kills;
            values[4] = deaths;
            values[5] = assists;
            values[6] = logouts;
            values[7] = ban;
            values[8] = reason;

            return DeserializationUtils.deserializeUser(values);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not deserialize user", ex);
        }

        return null;
    }

    public static void save(User user) {
        SQLNamedStatement statement = SQLBasicUtils.getInsert(SQLDataModel.tabUsers);

        statement.set("uuid", user.getUUID().toString());
        statement.set("name", user.getName());
        statement.set("points", user.getRank().getPoints());
        statement.set("kills", user.getRank().getKills());
        statement.set("deaths", user.getRank().getDeaths());
        statement.set("assists", user.getRank().getAssists());
        statement.set("logouts", user.getRank().getLogouts());
        statement.set("ban", user.isBanned() ? user.getBan().getBanTime() : 0);
        statement.set("reason", (user.isBanned() ? user.getBan().getReason() : null));
        statement.executeUpdate();
    }

    public static void updatePoints(User user) {
        SQLTable table = SQLDataModel.tabUsers;
        SQLNamedStatement statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points"));

        statement.set("points", user.getRank().getPoints());
        statement.set("uuid", user.getUUID().toString());
        statement.executeUpdate();
    }

    private DatabaseUser() {}
}
