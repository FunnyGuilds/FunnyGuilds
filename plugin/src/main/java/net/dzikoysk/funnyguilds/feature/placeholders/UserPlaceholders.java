package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.feature.placeholders.impl.MemberPlaceholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class UserPlaceholders extends Placeholders<User> {

    public void registerGuildPlaceholders(Placeholders<Guild> guildPlaceholders) {
        guildPlaceholders.getPlaceholders()
                .forEach((name, placeholder) -> this.register("g-" + name,
                        new MemberPlaceholder((user, guild) -> placeholder.get(guild), user -> "Brak")
                ));
    }

}
