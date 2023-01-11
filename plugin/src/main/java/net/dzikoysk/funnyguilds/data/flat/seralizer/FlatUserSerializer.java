package net.dzikoysk.funnyguilds.data.flat.seralizer;

import java.io.File;
import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

public final class FlatUserSerializer {

    private FlatUserSerializer() {
    }

    public static Option<User> deserialize(File file) {
        if (file.isDirectory()) {
            return Option.none();
        }

        YamlWrapper wrapper = new YamlWrapper(file);

        String id = wrapper.getString("uuid");
        String name = wrapper.getString("name");
        int points = wrapper.getInt("points");
        int kills = wrapper.getInt("kills");
        int deaths = wrapper.getInt("deaths");
        int assists = wrapper.getInt("assists");
        int logouts = wrapper.getInt("logouts");
        Instant ban = TimeUtils.positiveOrNullInstant(wrapper.getLong("ban"));
        String reason = wrapper.getString("reason");

        if (id == null || name == null) {
            return Option.none();
        }

        Object[] values = new Object[9];
        values[0] = id;
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

    public static boolean serialize(User user) {
        FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();

        Option<File> fileOption = dataModel.getUserFile(user);
        if (fileOption.isEmpty()) {
            return false;
        }

        File userFile = fileOption.get();
        if (userFile.isDirectory()) {
            return false;
        }

        YamlWrapper wrapper = new YamlWrapper(userFile);
        wrapper.set("uuid", user.getUUID().toString());
        wrapper.set("name", user.getName());
        wrapper.set("points", user.getRank().getPoints());
        wrapper.set("kills", user.getRank().getKills());
        wrapper.set("deaths", user.getRank().getDeaths());
        wrapper.set("assists", user.getRank().getAssists());
        wrapper.set("logouts", user.getRank().getLogouts());

        user.getBan().peek(ban -> {
            wrapper.set("ban", ban.getTime().toEpochMilli());
            wrapper.set("reason", ban.getReason());
        });

        wrapper.save();
        user.markUnchanged();

        return true;
    }

}
