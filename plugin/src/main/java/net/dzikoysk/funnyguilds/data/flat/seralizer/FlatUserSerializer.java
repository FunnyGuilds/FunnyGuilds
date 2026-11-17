package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.time.Instant;
import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.UserStatsSerializer;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserStatKeys;
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

        return DeserializationUtils.deserializeUser(FunnyGuilds.getInstance().getUserManager(), values, wrapper.getString("stats"));
    }

    public static boolean serialize(User user) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        Optional<File> fileOption = dataModel.getUserFile(user);
        if (fileOption.isEmpty()) {
            return false;
        }

        File userFile = fileOption.orElseThrow();
        if (userFile.isDirectory()) {
            return false;
        }

        YamlWrapper wrapper = new YamlWrapper(userFile);
        RankService rankService = FunnyGuilds.getInstance().getRankService();
        wrapper.set("uuid", user.getUUID().toString());
        wrapper.set("name", user.getName());
        wrapper.set("points", rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0));
        wrapper.set("kills", rankService.getUserStatValue(user, UserStatKeys.KILLS).orElse(0));
        wrapper.set("deaths", rankService.getUserStatValue(user, UserStatKeys.DEATHS).orElse(0));
        wrapper.set("assists", rankService.getUserStatValue(user, UserStatKeys.ASSISTS).orElse(0));
        wrapper.set("logouts", rankService.getUserStatValue(user, UserStatKeys.LOGOUTS).orElse(0));
        wrapper.set("stats", UserStatsSerializer.serialize(user));
        wrapper.set("last_ip", user.getLastIP());

        user.getBan().ifPresent(ban -> {
            wrapper.set("ban", ban.getTime().toEpochMilli());
            wrapper.set("reason", ban.getReason());
        });

        wrapper.save();
        user.markUnchanged();

        return true;
    }

}
