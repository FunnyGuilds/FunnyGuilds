package net.dzikoysk.funnyguilds.user.placeholders;

import java.util.Locale;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.formatting.ValueFormatting;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class UserPlaceholders extends Placeholders<User, UserPlaceholders> {

    public UserPlaceholders playerProperty(String name, MonoResolver<Player> resolver) {
        return this.property(name, user -> resolver.resolve(user, FunnyGuilds.getInstance().getFunnyServer().getPlayer(user).orElse(null)));
    }

    public UserPlaceholders playerOptionProperty(String name, MonoResolver<Optional<Player>> resolver) {
        return this.playerProperty(name, player -> resolver.resolve(player, Optional.ofNullable(player)));
    }

    public UserPlaceholders rankProperty(String name, MonoResolver<User> resolver) {
        return this.property(name, (Object entity, User user) -> {
            Object value = resolver.resolve(user);
            if (value instanceof Float || value instanceof Double) {
                return String.format(Locale.US, "%.2f", ((Number) value).floatValue());
            }
            return Objects.toString(value);
        });
    }

    public UserPlaceholders numericRankProperty(String name, String metric, Function<User, Number> resolver, Supplier<ValueFormatting> formatting) {
        return this.rankProperty(name, resolver::apply)
                .property(name + "-format", user -> formatting.get().user(metric, resolver.apply(user)));
    }

    @Override
    public UserPlaceholders create() {
        return new UserPlaceholders();
    }

}
