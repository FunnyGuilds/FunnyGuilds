package net.dzikoysk.funnyguilds.user.placeholders;

import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.AbstractPlaceholdersService;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholdersService extends AbstractPlaceholdersService<User, UserPlaceholders> {

    public static UserPlaceholders createUserPlaceholders(FunnyGuilds plugin) {
        PluginConfiguration config = plugin.getPluginConfiguration();

        return new UserPlaceholders()
                .property("name", User::getName)
                .property("player", User::getName)
                .property("ping", User::getPing)
                .property("ping-format", user ->
                        NumberRange.inRangeToString(user.getPing(), config.pingFormat)
                                .replace("{PING}", String.valueOf(user.getPing())))
                .property("position", (user, rank) -> rank.getPosition(DefaultTops.USER_POINTS_TOP))
                .property("points", (user, rank) -> rank.getPoints())
                .property("points-format", (user, rank) ->
                        NumberRange.inRangeToString(rank.getPoints(), config.pointsFormat)
                                .replace("{POINTS}", String.valueOf(rank.getPoints())))
                .property("kills", (user, rank) -> rank.getKills())
                .property("deaths", (user, rank) -> rank.getDeaths())
                .property("kdr", (user, rank) -> String.format(Locale.US, "%.2f", rank.getKDR()))
                .property("assists", (user, rank) -> rank.getAssists())
                .property("logouts", (user, rank) -> rank.getLogouts());
    }

}
