package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Locale;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRankManager;
import org.jetbrains.annotations.Nullable;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private final EntityLocaleProvider entityLocaleProvider;

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankPlaceholdersService(
        EntityLocaleProvider entityLocaleProvider,
        PluginConfiguration config,
        MessageService messageService,
        UserRankManager userRankManager,
        GuildRankManager guildRankManager
    ) {
        this.entityLocaleProvider = entityLocaleProvider;
        this.config = config;
        this.messageService = messageService;
        this.userRankManager = userRankManager;
        this.guildRankManager = guildRankManager;
    }

    @Override
    public Replaceable asReplaceable(User targetUser, String prefix, String suffix, UnaryOperator<String> nameModifier) {
        return new FunnyFormatter()
            .register(new TopPlaceholderReplacement(config, messageService, userRankManager, guildRankManager, targetUser))
            .register(new TopPositionPlaceholderReplacement(messageService, guildRankManager, targetUser));
    }

    @Override
    public Locale getEntityLocale(@Nullable Object entity) {
        return this.entityLocaleProvider.getEntityLocale(entity);
    }

}
