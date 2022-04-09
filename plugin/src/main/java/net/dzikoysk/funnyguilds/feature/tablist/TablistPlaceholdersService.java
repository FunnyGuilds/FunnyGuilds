package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.placeholders.AbstractPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.Placeholder;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholders;

public class TablistPlaceholdersService extends AbstractPlaceholdersService<User, UserPlaceholders> {

    public <M> void register(FunnyGuilds plugin, String name, Placeholders<M, ?> toMap, Function<String, String> nameMapper, BiFunction<User, Placeholder<M>, Object> dataMapper) {
        this.register(plugin, name, new UserPlaceholders().map(toMap, nameMapper, dataMapper));
    }

    public <M, P extends Placeholders<M, ?>> void register(FunnyGuilds plugin, String name, Map<String, P> toMap, Function<String, String> nameMapper, BiFunction<User, Placeholder<M>, Object> dataMapper) {
        toMap.forEach((key, value) ->
                this.register(plugin, name + "_" + key.split("_", 2)[1], value, nameMapper, dataMapper));
    }
}
