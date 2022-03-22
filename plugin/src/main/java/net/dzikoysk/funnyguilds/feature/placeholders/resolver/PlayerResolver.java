package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;

public interface PlayerResolver extends UserResolver{
    Object resolve(Player player);

    @Override
    default Object resolve(User user) {
        return resolve(Bukkit.getPlayer(user.getUUID()));
    }

    interface OptionResolver extends PlayerResolver {
        Object resolve(Option<Player> playerOption);

        @Override
        default Object resolve(Player player) {
            return resolve(Option.of(player));
        }
    }
}
