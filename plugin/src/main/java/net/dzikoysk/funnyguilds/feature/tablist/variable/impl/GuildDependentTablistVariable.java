package net.dzikoysk.funnyguilds.feature.tablist.variable.impl;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public final class GuildDependentTablistVariable implements TablistVariable {

    private final String[] names;
    private final BiFunction<User, Guild, Object> whenInGuild;
    private final Function<User, Object> whenNotInGuild;

    public GuildDependentTablistVariable(String name, BiFunction<User, Guild, Object> whenInGuild, Function<User, Object> whenNotInGuild) {
        this(new String[] {name}, whenInGuild, whenNotInGuild);
    }

    public GuildDependentTablistVariable(String[] names, BiFunction<User, Guild, Object> whenInGuild, Function<User, Object> whenNotInGuild) {
        this.names = names.clone();
        this.whenInGuild = whenInGuild;
        this.whenNotInGuild = whenNotInGuild;
    }

    @Override
    public String[] names() {
        return this.names.clone();
    }

    @Override
    public String get(User user) {
        return user.hasGuild()
                ? Objects.toString(this.whenInGuild.apply(user, user.getGuild().get()))
                : Objects.toString(this.whenNotInGuild.apply(user));
    }

}
