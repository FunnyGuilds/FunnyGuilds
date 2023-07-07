package net.dzikoysk.funnyguilds.user.placeholders;

import java.util.Locale;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.entity.Player;
import panda.std.Option;

public class UserPlaceholders extends Placeholders<User, UserPlaceholders> {

    public UserPlaceholders playerProperty(String name, MonoResolver<Player> resolver) {
        return this.property(name, user -> resolver.resolve(user, FunnyGuilds.getInstance().getFunnyServer().getPlayer(user).orNull()));
    }

    public UserPlaceholders playerOptionProperty(String name, MonoResolver<Option<Player>> resolver) {
        return this.playerProperty(name, player -> resolver.resolve(player, Option.of(player)));
    }

    public UserPlaceholders rankProperty(String name, MonoResolver<UserRank> resolver) {
        return this.property(name, (Object entity, User user) -> {
            Object value = resolver.resolve(entity, user.getRank());
            if (value instanceof Float || value instanceof Double) {
                return String.format(Locale.US, "%.2f", ((Number) value).floatValue());
            }
            return Objects.toString(value);
        });
    }

    @Override
    public UserPlaceholders create() {
        return new UserPlaceholders();
    }

}
