package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Arrays;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.user.UserPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.user.UserRankPlaceholder;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholders extends Placeholders<User, UserPlaceholder> {

    public static final Placeholders<User, UserPlaceholder> USER;

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        USER = new UserPlaceholders()
                .property(Arrays.asList("player", "name"), new UserPlaceholder(User::getName))
                .property("ping", new UserPlaceholder(User::getPing))
                .property("ping-format", new UserPlaceholder(user ->
                        NumberRange.inRangeToString(user.getPing(), config.pingFormat)
                                .replace("{PING}", String.valueOf(user.getPing()))))
                .property("position", new UserRankPlaceholder((user, rank) -> rank.getPosition(DefaultTops.USER_POINTS_TOP)))
                .property("points", new UserRankPlaceholder((user, rank) -> rank.getPoints()))
                .property("points-format", new UserRankPlaceholder((user, rank) ->
                        NumberRange.inRangeToString(rank.getPoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(rank.getPoints()))))
                .property("kills", new UserRankPlaceholder((user, rank) -> rank.getKills()))
                .property("deaths", new UserRankPlaceholder((user, rank) -> rank.getDeaths()))
                .property("kdr", new UserRankPlaceholder((user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR())))
                .property("assists", new UserRankPlaceholder((user, rank) -> rank.getAssists()))
                .property("logouts", new UserRankPlaceholder((user, rank) -> rank.getLogouts()));
    }

}
