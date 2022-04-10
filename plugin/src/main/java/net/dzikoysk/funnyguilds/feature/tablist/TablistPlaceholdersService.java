package net.dzikoysk.funnyguilds.feature.tablist;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.feature.placeholders.DefaultPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import panda.std.stream.PandaStream;

public class TablistPlaceholdersService implements PlaceholdersService<User> {

    private final DefaultPlaceholdersService defaultPlaceholdersService;
    private final TimePlaceholdersService timePlaceholdersService;
    private final UserPlaceholdersService userPlaceholdersService;
    private final GuildPlaceholdersService guildPlaceholdersService;
    private final RankPlaceholdersService rankPlaceholdersService;

    public TablistPlaceholdersService(DefaultPlaceholdersService defaultPlaceholdersService, TimePlaceholdersService timePlaceholdersService, UserPlaceholdersService userPlaceholdersService, GuildPlaceholdersService guildPlaceholdersService, RankPlaceholdersService rankPlaceholdersService) {
        this.defaultPlaceholdersService = defaultPlaceholdersService;
        this.timePlaceholdersService = timePlaceholdersService;
        this.userPlaceholdersService = userPlaceholdersService;
        this.guildPlaceholdersService = guildPlaceholdersService;
        this.rankPlaceholdersService = rankPlaceholdersService;
    }

    @Override
    public String format(String text, User user) {
        text = defaultPlaceholdersService.format(text, null);
        text = timePlaceholdersService.format(text, OffsetDateTime.now());
        text = userPlaceholdersService.format(text, user);
        text = guildPlaceholdersService.formatCustom(text, user.getGuild().orNull(), "{G-", "}", String::toUpperCase);
        text = rankPlaceholdersService.format(text, user);
        return text;
    }

    public String formatIdentifier(String identifier, User user) {
        return this.format("{" + identifier.toUpperCase() + "}", user);
    }

    public Set<String> getPlaceholdersKeys() {
        List<Set<String>> keys = Arrays.asList(
                defaultPlaceholdersService.getPlaceholdersKeys(),
                timePlaceholdersService.getPlaceholdersKeys(),
                userPlaceholdersService.getPlaceholdersKeys(),
                guildPlaceholdersService.getPlaceholdersKeys().stream()
                        .map(key -> "{G-" + key)
                        .collect(Collectors.toSet())
        );
        return PandaStream.of(keys)
                .flatMap(streamKeys -> streamKeys)
                .toSet();
    }

}
