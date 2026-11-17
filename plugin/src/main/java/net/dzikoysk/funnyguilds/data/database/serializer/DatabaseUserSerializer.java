package net.dzikoysk.funnyguilds.data.database.serializer;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.UserStatsSerializer;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserBan;
import net.dzikoysk.funnyguilds.user.UserStatKeys;

public final class DatabaseUserSerializer {

    private DatabaseUserSerializer() {
    }

    public static Optional<User> deserialize(ResultSet resultSet) {
        if (resultSet == null) {
            return Optional.empty();
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
            String lastIP = resultSet.getString("last_ip");

            Object[] values = new Object[10];
            values[0] = uuid;
            values[1] = name;
            values[2] = points;
            values[3] = kills;
            values[4] = deaths;
            values[5] = assists;
            values[6] = logouts;
            values[7] = ban;
            values[8] = reason;
            values[9] = lastIP;

            return DeserializationUtils.deserializeUser(FunnyGuilds.getInstance().getUserManager(), values, resultSet.getString("stats"));
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize user", exception);
        }

        return Optional.empty();
    }

    public static void serialize(User user) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLNamedStatement statement = SQLBasicUtils.getInsert(dataModel.getUsersTable());
        RankService rankService = FunnyGuilds.getInstance().getRankService();

        statement.set("uuid", user.getUUID().toString());
        statement.set("name", user.getName());
        statement.set("points", rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0));
        statement.set("kills", rankService.getUserStatValue(user, UserStatKeys.KILLS).orElse(0));
        statement.set("deaths", rankService.getUserStatValue(user, UserStatKeys.DEATHS).orElse(0));
        statement.set("assists", rankService.getUserStatValue(user, UserStatKeys.ASSISTS).orElse(0));
        statement.set("logouts", rankService.getUserStatValue(user, UserStatKeys.LOGOUTS).orElse(0));
        statement.set("stats", UserStatsSerializer.serialize(user));
        statement.set("ban", user.getBan().map(UserBan::getTime).map(Instant::toEpochMilli).orElse(0L));
        statement.set("reason", user.getBan().map(UserBan::getReason).orElse(null));
        statement.set("last_ip", user.getLastIP());

        statement.executeUpdate();
        user.markUnchanged();
    }

    public static void updatePoints(User user) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLTable table = dataModel.getUsersTable();
        SQLNamedStatement statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points").orElse(null));

        statement.set("points", FunnyGuilds.getInstance().getRankService().getUserStatValue(user, UserStatKeys.POINTS).orElse(0));
        statement.set("uuid", user.getUUID().toString());
        statement.executeUpdate();
    }

}
