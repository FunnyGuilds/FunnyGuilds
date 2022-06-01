package net.dzikoysk.funnyguilds.feature.tablist;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
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

    public TablistPlaceholdersService(BasicPlaceholdersService basicPlaceholdersService,TimePlaceholdersService timePlaceholdersService,
                                      UserPlaceholdersService userPlaceholdersService, GuildPlaceholdersService guildPlaceholdersService) {
        this.basicPlaceholdersService = basicPlaceholdersService;
        this.timePlaceholdersService = timePlaceholdersService;
        this.userPlaceholdersService = userPlaceholdersService;
        this.guildPlaceholdersService = guildPlaceholdersService;
    }

    @Override
    public String format(String text, User user) {
        text = basicPlaceholdersService.format(text, null);
        text = timePlaceholdersService.format(text, OffsetDateTime.now());
        text = userPlaceholdersService.format(text, user);
        text = guildPlaceholdersService.formatCustom(text, user.getGuild().orNull(), "{G-", "}", String::toUpperCase);

        return text;
    }

    public String formatIdentifier(String identifier, User user) {
        return this.format("{" + identifier.toUpperCase() + "}", user);
    }

    public Set<String> getPlaceholdersKeys() {
        List<Set<String>> keys = Arrays.asList(
                basicPlaceholdersService.getPlaceholdersKeys(),
                timePlaceholdersService.getPlaceholdersKeys(),
                userPlaceholdersService.getPlaceholdersKeys(),
                guildPlaceholdersService.getPlaceholdersKeys().stream().map(key -> "{G-" + key).collect(Collectors.toSet())
        );

        try (PandaStream<Set<String>> keysStream = PandaStream.of(keys)) {
            return keysStream.flatMap(streamKeys -> streamKeys).toSet();
        }
    }

}
