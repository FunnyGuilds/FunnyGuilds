package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
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
            FunnyGuilds.getPluginLogger().error("Could not deserialize user", ex);
        }

        return null;
    }

    private DatabaseUser() {}
}
