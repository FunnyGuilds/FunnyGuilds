package net.dzikoysk.funnyguilds.element.tablist.variable.impl;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public class TimeFormattedVariable implements TablistVariable {
    private final String[] names;
    private final BiFunction<User, LocalDateTime, String> function;
    private LocalDateTime currentTime;

    public TimeFormattedVariable(String name, BiFunction<User, LocalDateTime, String> function) {
        this(new String[]{ name }, function);
    }

    public TimeFormattedVariable(String[] names, BiFunction<User, LocalDateTime, String> function) {
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
        return ChatUtils.appendDigit(this.function.apply(user, this.currentTime != null ? this.currentTime : LocalDateTime.now()));
    }
    
}
