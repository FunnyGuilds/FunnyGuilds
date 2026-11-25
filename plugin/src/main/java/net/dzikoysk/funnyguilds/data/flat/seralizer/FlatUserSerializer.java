package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.time.Instant;
import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.configuration.ConfigurationSection;

public final class FlatUserSerializer {

    private FlatUserSerializer() {
    }

    public static Optional<User> deserialize(File file) {
        if (file.isDirectory()) {
            return Optional.empty();
        }

        return deserialize(new YamlWrapper(file));
    }

    public static Optional<User> deserialize(ConfigurationSection wrapper) {
        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        int points = wrapper.getInt("points");
        int kills = wrapper.getInt("kills");
        int deaths = wrapper.getInt("deaths");
        int assists = wrapper.getInt("assists");
        int logouts = wrapper.getInt("logouts");
        Instant ban = TimeUtils.positiveOrNullInstant(wrapper.getLong("ban"));
        String reason = wrapper.getString("reason");
        String lastIP = wrapper.getString("last_ip");

        if (id == null || name == null) {
            return Optional.empty();
        }

        Object[] values = new Object[10];
        values[0] = id;
        values[1] = name;
        values[2] = points;
        values[3] = kills;
        values[4] = deaths;
        values[5] = assists;
        values[6] = logouts;
        values[7] = ban;
        values[8] = reason;
        values[9] = lastIP;

        return DeserializationUtils.deserializeUser(FunnyGuilds.getInstance().getUserManager(), values);
    }

    public static boolean serialize(User user) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        return dataModel.persistYaml(user, values -> {
            values.put("uuid", user.getUUID().toString());
            values.put("name", user.getName());
            values.put("points", user.getRank().getPoints());
            values.put("kills", user.getRank().getKills());
            values.put("deaths", user.getRank().getDeaths());
            values.put("assists", user.getRank().getAssists());
            values.put("logouts", user.getRank().getLogouts());
            values.put("last_ip", user.getLastIP());

            values.put("ban", user.getBan().map(ban -> ban.getTime().toEpochMilli()).orElse(null));
            values.put("reason", user.getBan().map(ban -> ban.getReason()).orElse(null));
        });
    }

}
