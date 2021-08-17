package net.dzikoysk.funnyguilds.feature.tablist.variable.impl;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;

import java.util.Objects;
import java.util.function.Function;

public final class SimpleTablistVariable implements TablistVariable {

    private final String[] names;
    private final Function<User, Object> function;

    public SimpleTablistVariable(String name, Function<User, Object> function) {
        this(new String[]{ name }, function);
    }

    public SimpleTablistVariable(String[] names, Function<User, Object> function) {
        this.names = names;
        this.function = function;
    }

    @Override
    public String[] names() {
        return this.names;
    }

    @Override
    public String get(User user) {
        return Objects.toString(this.function.apply(user));
    }

}
