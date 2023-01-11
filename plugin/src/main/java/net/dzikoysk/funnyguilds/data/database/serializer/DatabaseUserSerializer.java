package net.dzikoysk.funnyguilds.data.database.serializer;

import java.sql.ResultSet;
import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import panda.std.Option;

public final class DatabaseUserSerializer {

    private DatabaseUserSerializer() {
    }

    public static Option<User> deserialize(ResultSet resultSet) {
        if (resultSet == null) {
            return Option.none();
        }

        try {
            String uuid = resultSet.getString("uuid");
            String name = resultSet.getString("name");
            int points = resultSet.getInt("points");
            int kills = resultSet.getInt("kills");
            int deaths = resultSet.getInt("deaths");
            int assists = resultSet.getInt("assists");
            int logouts = resultSet.getInt("logouts");
            Instant ban = TimeUtils.positiveOrNullInstant(resultSet.getLong("ban"));
            String reason = resultSet.getString("reason");

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

            return DeserializationUtils.deserializeUser(FunnyGuilds.getInstance().getUserManager(), values);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize user", exception);
        }

        return Option.none();
    }

    public static void serialize(User user) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLNamedStatement statement = SQLBasicUtils.getInsert(dataModel.getUsersTable());

        statement.set("uuid", user.getUUID().toString());
        statement.set("name", user.getName());
        statement.set("points", user.getRank().getPoints());
        statement.set("kills", user.getRank().getKills());
        statement.set("deaths", user.getRank().getDeaths());
        statement.set("assists", user.getRank().getAssists());
        statement.set("logouts", user.getRank().getLogouts());
        statement.set("ban", user.getBan().map(UserBan::getTime).map(Instant::toEpochMilli).orElseGet(0L));
        statement.set("reason", user.getBan().map(UserBan::getReason).orNull());

        statement.executeUpdate();
        user.markUnchanged();
    }

    public static void updatePoints(User user) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLTable table = dataModel.getUsersTable();
        SQLNamedStatement statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points").orNull());

        statement.set("points", user.getRank().getPoints());
        statement.set("uuid", user.getUUID().toString());
        statement.executeUpdate();
    }

}
