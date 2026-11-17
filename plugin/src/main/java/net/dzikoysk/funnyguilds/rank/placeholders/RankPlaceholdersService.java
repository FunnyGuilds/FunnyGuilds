package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Locale;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private final EntityLocaleProvider entityLocaleProvider;

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final RankService rankService;

    public RankPlaceholdersService(
        EntityLocaleProvider entityLocaleProvider,
        PluginConfiguration config,
        MessageService messageService,
        RankService rankService
    ) {
        this.entityLocaleProvider = entityLocaleProvider;
        this.config = config;
        this.messageService = messageService;
        this.rankService = rankService;
    }

    @Override
    public Replaceable asReplaceable(User targetUser, String prefix, String suffix, UnaryOperator<String> nameModifier) {
        return new FunnyFormatter()
            .register(new TopPlaceholderReplacement(config, messageService, rankService, targetUser))
            .register(new TopPositionPlaceholderReplacement(messageService, rankService, targetUser));
    }

    @Override
    public Locale getEntityLocale(@Nullable Object entity) {
        return this.entityLocaleProvider.getEntityLocale(entity);
    }

}
