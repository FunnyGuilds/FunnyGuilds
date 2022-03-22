package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

@FunctionalInterface
public interface MemberResolver {
    Object resolve(User user, Guild guild);
}
