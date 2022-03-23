package net.dzikoysk.funnyguilds.feature.tablist.variable.impl;

import java.util.Objects;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public final class GuildDependentTablistVariable implements TablistVariable {

    private final String[] names;
    private final MemberResolver whenInGuild;
    private final StandaloneUserResolver whenNotInGuild;

    public GuildDependentTablistVariable(String name, MemberResolver whenInGuild, StandaloneUserResolver whenNotInGuild) {
        this(new String[]{name}, whenInGuild, whenNotInGuild);
    }

    public GuildDependentTablistVariable(String[] names, MemberResolver whenInGuild, StandaloneUserResolver whenNotInGuild) {
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
        return Objects.toString(user.getGuild()
                .map(guild -> this.whenInGuild.resolve(user, guild))
                .orElseGet(this.whenNotInGuild.resolve(user)));
    }

    @FunctionalInterface
    public interface MemberResolver {
        Object resolve(User user, Guild guild);
    }

    @FunctionalInterface
    public interface StandaloneUserResolver {
        Object resolve(User user);
    }

}
