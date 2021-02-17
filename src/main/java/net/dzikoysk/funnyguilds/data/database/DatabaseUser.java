package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.database.element.SQLBuilderStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLUtils;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;

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

    public void save() {
        if (user.getUUID() == null) {
            return;
        }

        SQLBuilderStatement builderPS = SQLUtils.getBuilderInsert(SQLDataModel.tabUsers);

        builderPS.set("uuid", user.getUUID().toString());
        builderPS.set("name", user.getName());
        builderPS.set("points", user.getRank().getPoints());
        builderPS.set("kills", user.getRank().getKills());
        builderPS.set("deaths", user.getRank().getDeaths());
        builderPS.set("guild", user.hasGuild() ? "'" +  user.getGuild().getName() + "'" : "");
        builderPS.set("ban", user.isBanned() ? user.getBan().getBanTime() : 0);
        builderPS.set("reason", (user.isBanned() ? user.getBan().getReason() : null));
        builderPS.executeUpdate();
    }

    public void updatePoints() {
        SQLTable table = SQLDataModel.tabUsers;
        SQLBuilderStatement builderPS = SQLUtils.getBuilderUpdate(table, table.getSQLElement("points"));

        builderPS.set("points", user.getRank().getPoints());
        builderPS.set("uuid", user.getUUID().toString());
        builderPS.executeUpdate();
    }
}
