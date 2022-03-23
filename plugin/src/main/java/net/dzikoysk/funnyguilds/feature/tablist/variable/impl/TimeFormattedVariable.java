package net.dzikoysk.funnyguilds.feature.tablist.variable.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;

public class TimeFormattedVariable implements TablistVariable {
    private final String[] names;
    private final BiFunction<User, LocalDateTime, Object> function;
    private LocalDateTime currentTime;

    public TimeFormattedVariable(String name, BiFunction<User, LocalDateTime, Object> function) {
        this(new String[]{name}, function);
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
