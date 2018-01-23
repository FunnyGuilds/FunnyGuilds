package net.dzikoysk.funnyguilds.util.element.tablist.variable.impl;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.TablistVariable;

import java.util.function.Function;

public final class GuildDependentTablistVariable implements TablistVariable {

    private final String[] names;
    private final Function<User, String> whenInGuild;
    private final Function<User, String> whenNotInGuild;

    public GuildDependentTablistVariable(String name, Function<User, String> whenInGuild, Function<User, String> whenNotInGuild) {
        this(new String[]{ name }, whenInGuild, whenNotInGuild);
    }


    public GuildDependentTablistVariable(String[] names, Function<User, String> whenInGuild, Function<User, String> whenNotInGuild) {
        this.names = names;
        this.whenInGuild = whenInGuild;
        this.whenNotInGuild = whenNotInGuild;
    }

    @Override
    public String[] names() {
        return this.names;
    }

    @Override
    public String get(User user) {
        if (user.getGuild() != null) {
            return this.whenInGuild.apply(user);
        } else {
            return this.whenNotInGuild.apply(user);
        }
    }

}
