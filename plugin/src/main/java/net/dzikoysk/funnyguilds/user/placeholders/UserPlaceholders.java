package net.dzikoysk.funnyguilds.user.placeholders;

import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;

public class UserPlaceholders extends Placeholders<User, UserPlaceholders> {

    public UserPlaceholders property(String name, PairResolver<User, UserRank> resolver) {
        return this.property(name, user -> resolver.resolve(user, user.getRank()));
    }

    public UserPlaceholders playerProperty(String name, MonoResolver<Player> resolver) {
        return this.property(name, user -> resolver.resolve(Bukkit.getPlayer(user.getUUID())));
    }

    public UserPlaceholders playerOptionProperty(String name, MonoResolver<Option<Player>> resolver) {
        return this.playerProperty(name, player -> resolver.resolve(Option.of(player)));
    }

    @Override
    public UserPlaceholders create() {
        return new UserPlaceholders();
    }

}
