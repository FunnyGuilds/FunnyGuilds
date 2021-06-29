package net.dzikoysk.funnyguilds.element.tablist.variable.impl;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;

public class TimeFormattedVariable implements TablistVariable {
    private final String[] names;
    private final BiFunction<User, LocalDateTime, Object> function;
    private LocalDateTime currentTime;

    public TimeFormattedVariable(String name, BiFunction<User, LocalDateTime, Object> function) {
        this(new String[]{ name }, function);
    }

    public TimeFormattedVariable(String[] names, BiFunction<User, LocalDateTime, Object> function) {
        this.names = names;
        this.function = function;
    }

    @Override
    public String[] names() {
        return this.names;
    }

    public void provideCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String get(User user) {
        return ChatUtils.appendDigit(Objects.toString(this.function.apply(user, this.currentTime != null ? this.currentTime : LocalDateTime.now())));
    }
    
}
