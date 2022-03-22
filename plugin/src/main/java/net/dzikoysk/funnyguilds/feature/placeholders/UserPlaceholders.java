package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Arrays;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.UserPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.UserRankPlaceholder;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final Placeholders<User, UserPlaceholder> USER_PLACEHOLDERS;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        USER_PLACEHOLDERS = new UserPlaceholders()
                .register(Arrays.asList("player", "name"), new UserPlaceholder(User::getName))
                .register("ping", new UserPlaceholder(User::getPing))
                .register("ping-format", new UserPlaceholder(user ->
                        NumberRange.inRangeToString(user.getPing(), config.pingFormat)
                                .replace("{PING}", String.valueOf(user.getPing()))))
                .register("points", new UserRankPlaceholder((user, rank) -> rank.getPoints()))
                .register("points-format", new UserRankPlaceholder((user, rank) ->
                        NumberRange.inRangeToString(rank.getPoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(rank.getPoints()))))
                .register("kills", new UserRankPlaceholder((user, rank) -> rank.getKills()))
                .register("deaths", new UserRankPlaceholder((user, rank) -> rank.getDeaths()))
                .register("kdr", new UserRankPlaceholder((user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR())))
                .register("assists", new UserRankPlaceholder((user, rank) -> rank.getAssists()))
                .register("logouts", new UserRankPlaceholder((user, rank) -> rank.getLogouts()));
    }

}
