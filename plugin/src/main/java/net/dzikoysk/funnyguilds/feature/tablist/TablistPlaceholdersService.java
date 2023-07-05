package net.dzikoysk.funnyguilds.feature.tablist;

import java.time.OffsetDateTime;
import java.util.Locale;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import org.jetbrains.annotations.Nullable;

public class TablistPlaceholdersService implements PlaceholdersService<User> {

    private final BasicPlaceholdersService basicPlaceholdersService;
    private final TimePlaceholdersService timePlaceholdersService;
    private final UserPlaceholdersService userPlaceholdersService;
    private final GuildPlaceholdersService guildPlaceholdersService;

    public TablistPlaceholdersService(BasicPlaceholdersService basicPlaceholdersService, TimePlaceholdersService timePlaceholdersService,
                                      UserPlaceholdersService userPlaceholdersService, GuildPlaceholdersService guildPlaceholdersService) {
        this.basicPlaceholdersService = basicPlaceholdersService;
        this.timePlaceholdersService = timePlaceholdersService;
        this.userPlaceholdersService = userPlaceholdersService;
        this.guildPlaceholdersService = guildPlaceholdersService;
    }

    @Override
    public String format(@Nullable Object entity, String text, User user) {
        text = this.basicPlaceholdersService.format(entity, text, null);
        text = this.timePlaceholdersService.format(entity, text, OffsetDateTime.now());
        text = this.userPlaceholdersService.format(entity, text, user);
        text = this.guildPlaceholdersService.formatCustom(entity, text, user.getGuild().orNull(), "{G-", "}", name -> name.toUpperCase(Locale.ROOT));

        return text;
    }

    public String formatIdentifier(@Nullable Object entity, String identifier, User user) {
        return this.format(entity, "{" + identifier.toUpperCase(Locale.ROOT) + "}", user);
    }

}
