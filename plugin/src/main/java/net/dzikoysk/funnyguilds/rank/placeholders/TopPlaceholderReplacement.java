package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.replace.replacement.ComponentReplacement;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.shared.Optionals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopPlaceholderReplacement extends ComponentReplacement {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
        "\\{(PTOP|GTOP)-([A-Za-z0-9_:.-]+)-([0-9]+)(?:-(NAME|TAG|UUID|VALUE))?}",
        Pattern.CASE_INSENSITIVE
    );

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final RankService rankService;
    private final User targetUser;

    TopPlaceholderReplacement(
        PluginConfiguration config,
        MessageService messageService,
        RankService rankService,
        User targetUser
    ) {
        super(PLACEHOLDER_PATTERN);
        this.config = config;
        this.messageService = messageService;
        this.rankService = rankService;
        this.targetUser = targetUser;
    }

    @Override
    public @NotNull TextReplacementConfig getReplacement(@Nullable Locale locale) {
        return this.newReplacementBuilder()
            .replacement((matchResult, builder) -> {
                String topType = matchResult.group(1);
                String comparatorType = matchResult.group(2);
                String indexString = matchResult.group(3);
                String subType = matchResult.group(4) == null
                    ? null
                    : matchResult.group(4).toUpperCase(Locale.ROOT);

                Optional<Integer> indexOption = Optionals.attempt(NumberFormatException.class, () -> Integer.parseInt(indexString));
                if (indexOption.isEmpty()) {
                    FunnyGuilds.getPluginLogger().error(indexString + " is invalid " + topType + " index!");
                    return this.messageService.getComponent(this.targetUser, config -> topType.equalsIgnoreCase("PTOP") ? config.ptopNoValue : config.gtopNoValue);
                }

                int index = indexOption.orElseThrow();
                if (index < 1) {
                    FunnyGuilds.getPluginLogger().error("Index in " + topType + " must be greater or equal to 1!");
                    return this.messageService.getComponent(this.targetUser, config -> topType.equalsIgnoreCase("PTOP") ? config.ptopNoValue : config.gtopNoValue);
                }

                return switch (topType.toUpperCase(Locale.ROOT)) {
                    case "PTOP" ->  this.getPlayerTopComponent(comparatorType, index, subType, targetUser);
                    case "GTOP" -> this.getGuildTopComponent(comparatorType, index, subType, targetUser);
                    default -> Component.empty();
                };
            }).build();
    }

    private Component getPlayerTopComponent(String metric, int index, String subType, User viewer) {
        Component noValue = this.messageService.getComponent(viewer, config -> config.ptopNoValue);
        return this.rankService.getUserRankTracker(metric)
                .flatMap(tracker -> tracker.findEntryByPosition(index))
                .map(entry -> {
                    User user = entry.getFirst();
                    Number value = entry.getSecond();
                    if (subType == null) {
                        return this.formatUserTop(user, value, metric);
                    }
                    return switch (subType) {
                        case "NAME" -> Component.text(user.getName());
                        case "UUID" -> Component.text(user.getUUID().toString());
                        case "VALUE" -> Component.text(rawValueString(value));
                        default -> Component.empty();
                    };
                }).orElse(noValue);
    }

    private Component getGuildTopComponent(String metric, int index, String subType, User viewer) {
        Component noValue = this.messageService.getComponent(viewer, config -> config.gtopNoValue);
        return this.rankService.getGuildRankTracker(metric)
                .flatMap(tracker -> tracker.findEntryByPosition(index))
                .map(entry -> {
                    Guild guild = entry.getFirst();
                    Number value = entry.getSecond();
                    if (subType == null) {
                        return this.formatGuildTop(viewer, guild, value, formattingMetric(metric));
                    }
                    return switch (subType) {
                        case "NAME" -> Component.text(guild.getName());
                        case "TAG" -> Component.text(guild.getTag());
                        case "UUID" -> Component.text(guild.getUUID().toString());
                        case "VALUE" -> Component.text(rawValueString(value));
                        default -> Component.empty();
                    };
                }).orElse(noValue);
    }

    // Value formatting keys use the underscore spelling (avg_kdr), while guild top ids accept both
    // spellings of the avg_ prefix alias - format both aliases through the underscore key.
    private static String formattingMetric(String metric) {
        String normalized = metric.toLowerCase(Locale.ROOT);
        return normalized.startsWith("avg-") ? "avg_" + normalized.substring(4) : normalized;
    }

    private Component formatUserTop(User user, Number topValue, String metric) {
        boolean online = user.isOnline();
        if (online && this.config.ptopRespectVanish) {
            online = !user.isVanished();
        }

        Component userNameComponent = Component.text(
            user.getName(),
            online ? this.config.onlineColor : this.config.offlineColor
        );
        return userNameComponent.append(this.config.getValueFormatting().top(false, metric, topValue, rawValueString(topValue)));
    }

    private Component formatGuildTop(
        User targetUser,
        Guild guild,
        Number topValue,
        String metric
    ) {
        Component guildTagComponent = null;
        if (this.config.top.useRelationshipColors && targetUser != null) {
            Guild viewerGuild = targetUser.getGuild().orElse(null);
            if (viewerGuild != null) {
                guildTagComponent = this.config.relationalTag.chooseAndPrepareTag(viewerGuild, guild);
            }
        }

        if (guildTagComponent == null) {
            TextColor tagColor = !guild.getOnlineMembers(!this.config.gtopRespectVanish).isEmpty()
                ? this.config.onlineColor
                : this.config.offlineColor;
            guildTagComponent = Component.text(guild.getTag(), tagColor);
        }

        return guildTagComponent.append(this.config.getValueFormatting().top(true, metric, topValue, rawValueString(topValue)));
    }

    private String rawValueString(Number topValue) {
        return topValue instanceof Float || topValue instanceof Double
            ? String.format(Locale.US, "%.2f", topValue.doubleValue())
            : topValue.toString();
    }
}
