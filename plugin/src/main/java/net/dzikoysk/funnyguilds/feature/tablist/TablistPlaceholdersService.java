package net.dzikoysk.funnyguilds.feature.tablist;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import panda.std.stream.PandaStream;

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
    public String format(String text, User user) {
        text = this.basicPlaceholdersService.format(text, null);
        text = this.timePlaceholdersService.format(text, OffsetDateTime.now());
        text = this.userPlaceholdersService.format(text, user);
        text = this.guildPlaceholdersService.formatCustom(text, user.getGuild().orNull(), "{G-", "}", name -> name.toUpperCase(Locale.ROOT));

        return text;
    }

    public String formatIdentifier(String identifier, User user) {
        return this.format("{" + identifier.toUpperCase(Locale.ROOT) + "}", user);
    }

    public Set<String> getPlaceholdersKeys() {
        List<Set<String>> keys = Arrays.asList(
                this.basicPlaceholdersService.getPlaceholdersKeys(),
                this.timePlaceholdersService.getPlaceholdersKeys(),
                this.userPlaceholdersService.getPlaceholdersKeys(),
                this.guildPlaceholdersService.getPlaceholdersKeys().stream().map(key -> "{G-" + key).collect(Collectors.toSet())
        );

        return PandaStream.of(keys).flatMap(streamKeys -> streamKeys).toSet();
    }

}
