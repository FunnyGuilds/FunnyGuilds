package net.dzikoysk.funnyguilds.feature.tablist;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService;
import org.jetbrains.annotations.Nullable;

public class TablistPlaceholdersService implements PlaceholdersService<User> {

    private final EntityLocaleProvider entityLocaleProvider;
    private final BasicPlaceholdersService basicPlaceholdersService;
    private final TimePlaceholdersService timePlaceholdersService;
    private final UserPlaceholdersService userPlaceholdersService;
    private final GuildPlaceholdersService guildPlaceholdersService;

    public TablistPlaceholdersService(
            EntityLocaleProvider entityLocaleProvider,
            BasicPlaceholdersService basicPlaceholdersService,
            TimePlaceholdersService timePlaceholdersService,
            UserPlaceholdersService userPlaceholdersService,
            GuildPlaceholdersService guildPlaceholdersService
    ) {
        this.entityLocaleProvider = entityLocaleProvider;
        this.basicPlaceholdersService = basicPlaceholdersService;
        this.timePlaceholdersService = timePlaceholdersService;
        this.userPlaceholdersService = userPlaceholdersService;
        this.guildPlaceholdersService = guildPlaceholdersService;
    }

    @Override
    public FunnyFormatter toFormatter(
            User data,
            String prefix,
            String suffix,
            UnaryOperator<String> nameModifier
    ) {
        return new FunnyFormatter()
                .register(this.basicPlaceholdersService.toFormatter(null, prefix, suffix, nameModifier))
                .register(this.timePlaceholdersService.toFormatter(OffsetDateTime.now(), prefix, suffix, nameModifier))
                .register(this.userPlaceholdersService.toFormatter(data, prefix, suffix, nameModifier))
                .register(this.guildPlaceholdersService.toFormatter(data.getGuild().orNull(), prefix + "G-", suffix, nameModifier));
    }

    @Override
    public Locale getEntityLocale(@Nullable Object entity) {
        return this.entityLocaleProvider.getEntityLocale(entity);
    }

}
