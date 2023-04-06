package net.dzikoysk.funnyguilds.feature.placeholders;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import panda.std.stream.PandaStream;

public class GeneralPlaceholdersService implements PlaceholdersService<User> {

    private final BasicPlaceholdersService basicPlaceholdersService;
    private final TimePlaceholdersService timePlaceholdersService;
    private final UserPlaceholdersService userPlaceholdersService;
    private final GuildPlaceholdersService guildPlaceholdersService;

    public GeneralPlaceholdersService(BasicPlaceholdersService basicPlaceholdersService, TimePlaceholdersService timePlaceholdersService,
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
                PandaStream.of(this.guildPlaceholdersService.getPlaceholdersKeys())
                        .map(key -> "{G-" + key)
                        .toSet()
        );

        return PandaStream.of(keys)
                .flatMap(streamKeys -> streamKeys)
                .toSet();
    }

}
