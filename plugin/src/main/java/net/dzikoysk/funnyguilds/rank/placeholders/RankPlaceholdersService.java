package net.dzikoysk.funnyguilds.rank.placeholders;

import dev.peri.yetanothermessageslibrary.adventure.AdventureHelper;
import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.rank.RankTracker;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RankPlaceholdersService implements PlaceholdersService<User> {

    private static final Pattern TOP_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([A-Za-z0-9_:.-]+)-([0-9]+)}", Pattern.CASE_INSENSITIVE);
    private static final Pattern POSITION_PATTERN = Pattern.compile("\\{(POSITION|G-POSITION)-([A-Za-z0-9_:.-]+)}", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_PATTERN = Pattern.compile("\\{(PTOP|GTOP)-([0-9]+)}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([A-Za-z0-9_()-]+)}");

    private final PluginConfiguration config;
    private final TablistConfiguration tablistConfig;
    private final MessageService messageService;
    private final RankService rankService;

    public RankPlaceholdersService(PluginConfiguration config, TablistConfiguration tablistConfig,
                                   MessageService messageService, RankService rankService) {
        this.config = config;
        this.tablistConfig = tablistConfig;
        this.messageService = messageService;
        this.rankService = rankService;
    }

    @Override
    public String format(@Nullable Object entity, String text, User targetUser) {
        text = this.formatTop(text, targetUser);
        text = this.formatTopPosition(text, targetUser);
        return this.config.top.enableLegacyPlaceholders ? this.formatRank(text, targetUser) : text;
    }

    public String formatTop(String text, @Nullable User targetUser) {
        return replaceMatches(text, TOP_PATTERN, match -> {
            String top = normalize(match.group(2));
            int position = parsePosition(match.group(3));
            if (match.group(1).equalsIgnoreCase("PTOP")) {
                String fallback = this.messageService.get(targetUser, messages -> messages.ptopNoValue);
                if (position == 0) {
                    return fallback;
                }
                return this.rankService.getUserRankTracker(top)
                        .map(tracker -> this.formatUserTop(tracker, position, top, targetUser))
                        .orElse(fallback);
            }
            String fallback = this.messageService.get(targetUser, messages -> messages.gtopNoValue);
            if (position == 0) {
                return fallback;
            }
            return this.rankService.getGuildRankTracker(top)
                    .map(tracker -> this.formatGuildTop(tracker, position, top, targetUser))
                    .orElse(fallback);
        });
    }

    private String formatUserTop(RankTracker<User, ?> tracker, int position, String top, @Nullable User viewer) {
        return tracker.findEntityByPosition(position).map(user -> {
            Number value = tracker.valueOfEntity(user).orElseThrow(IllegalStateException::new);
            String suffix = formatTopValue(value, this.config.top.format.ptop.getValue(),
                    this.config.top.format.ptopValueFormatting.get(top));
            return this.formatUserRank(user, suffix);
        }).orElseGet(() -> this.messageService.get(viewer, messages -> messages.ptopNoValue));
    }

    private String formatGuildTop(RankTracker<Guild, ?> tracker, int position, String top, @Nullable User viewer) {
        return tracker.findEntityByPosition(position).map(guild -> {
            Number value = tracker.valueOfEntity(guild).orElseThrow(IllegalStateException::new);
            String suffix = formatTopValue(value, this.config.top.format.gtop.getValue(),
                    this.config.top.format.gtopValueFormatting.get(top));
            return this.formatGuildRank(viewer, guild, suffix);
        }).orElseGet(() -> this.messageService.get(viewer, messages -> messages.gtopNoValue));
    }

    public String formatTopPosition(String text, @Nullable User targetUser) {
        return replaceMatches(text, POSITION_PATTERN, match -> {
            String top = normalize(match.group(2));
            if (match.group(1).equalsIgnoreCase("POSITION")) {
                return targetUser == null ? "0" : this.rankService.getUserRankTracker(top)
                        .flatMap(tracker -> tracker.findPositionByEntity(targetUser))
                        .map(String::valueOf).orElse("0");
            }
            String fallback = this.messageService.get(targetUser, messages -> messages.minMembersToIncludeNoValue);
            Optional<Guild> guild = Optional.ofNullable(targetUser).flatMap(user -> Optional.ofNullable(user.getGuild().orNull()));
            return guild.flatMap(value -> this.rankService.getGuildRankTracker(top)
                    .flatMap(tracker -> tracker.findPositionByEntity(value)))
                    .map(String::valueOf).orElse(fallback);
        });
    }

    @Deprecated
    public String formatRank(String text, @Nullable User targetUser) {
        return replaceMatches(text, LEGACY_PATTERN, match -> {
            int position = parsePosition(match.group(2));
            if (match.group(1).equalsIgnoreCase("PTOP")) {
                String fallback = this.messageService.get(targetUser, messages -> messages.ptopNoValue);
                return this.rankService.getUserRankTracker(DefaultTops.USER_POINTS_TOP)
                        .flatMap(tracker -> tracker.findEntityByPosition(position))
                        .map(user -> {
                            int points = user.getRank().getPoints();
                            String suffix = new FunnyFormatter()
                                    .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                                    .register("{POINTS}", points).format(this.config.ptopPoints.getValue());
                            return this.formatUserRank(user, suffix);
                        }).orElse(fallback);
            }
            String fallback = this.messageService.get(targetUser, messages -> messages.gtopNoValue);
            return this.rankService.getGuildRankTracker(DefaultTops.GUILD_AVG_POINTS_TOP)
                    .flatMap(tracker -> tracker.findEntityByPosition(position))
                    .map(guild -> {
                        int points = guild.getRank().getAveragePoints();
                        String suffix = new FunnyFormatter()
                                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                                .register("{POINTS}", points).format(this.config.gtopPoints.getValue());
                        return this.formatGuildRank(targetUser, guild, suffix);
                    }).orElse(fallback);
        });
    }

    private static String normalize(String top) {
        String normalized = top.toLowerCase(Locale.ROOT);
        return normalized.startsWith("avg-") ? "avg_" + normalized.substring(4) : normalized;
    }

    private static int parsePosition(String text) {
        try {
            return Math.max(0, Integer.parseInt(text));
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static String replaceMatches(String text, Pattern pattern, Function<Matcher, String> replacement) {
        if (text == null) {
            return "";
        }
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement.apply(matcher)));
        }
        return matcher.appendTail(result).toString();
    }

    private static String formatTopValue(Number value, String format, @Nullable List<RangeFormatting> formats) {
        String raw = value instanceof Float || value instanceof Double
                ? String.format(Locale.US, "%.2f", value.doubleValue()) : value.toString();
        return new FunnyFormatter()
                .register("{VALUE-FORMAT}", formats == null ? raw : NumberRange.inRangeToString(value, formats))
                .register("{VALUE}", raw).format(format);
    }

    private String formatUserRank(User user, String suffix) {
        boolean online = user.isOnline() && (!this.config.ptopRespectVanish || !user.isVanished());
        RawString color = online ? this.config.ptopOnline : this.config.ptopOffline;
        return color + user.getName() + suffix;
    }

    private String formatGuildRank(@Nullable User viewer, Guild guild, String suffix) {
        String prefix = this.tablistConfig.useRelationshipColors
                ? this.config.relationalTag.chooseAndPrepareTag(viewer == null ? null : viewer.getGuild().orNull(), guild)
                : "{TAG}";
        return FunnyFormatter.format(prefix, "{TAG}", guild.getTag()) + suffix;
    }

    public Replaceable prepareReplacement(User targetUser) {
        return new Replaceable() {
            @Override
            public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
                return RankPlaceholdersService.this.format(targetUser, text, targetUser);
            }

            @Override
            public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
                return text.replaceText(TextReplacementConfig.builder()
                        .match(PLACEHOLDER_PATTERN)
                        .replacement((result, input) -> AdventureHelper.legacyToComponent(
                                RankPlaceholdersService.this.format(targetUser, result.group(), targetUser)))
                        .build());
            }
        };
    }
}
