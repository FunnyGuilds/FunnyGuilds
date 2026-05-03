package net.dzikoysk.funnyguilds.feature.hooks.placeholderapi;

import java.util.Locale;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public class PlaceholderAPIHook extends AbstractPluginHook {
    
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final FunnyGuilds plugin;

    public PlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        new FunnyGuildsPlaceholder(this.plugin).register();
        return HookInitResult.SUCCESS;
    }

    public Option<String> replacePlaceholders(Player user, String base) {
        return Option.of(PlaceholderAPI.setPlaceholders(user, base))
                .filter(replaced -> !replaced.equals(base));
    }

    public Option<String> replacePlaceholders(Player userOne, Player userTwo, String base) {
        return Option.of(PlaceholderAPI.setRelationalPlaceholders(userOne, userTwo, base))
                .filter(replaced -> !replaced.equals(base));
    }

    private static final class FunnyGuildsPlaceholder extends PlaceholderExpansion implements Relational {

        private final FunnyGuilds plugin;
        private final RankPlaceholdersService rankPlaceholdersService;
        private final String funnyguildsVersion;

        private FunnyGuildsPlaceholder(FunnyGuilds plugin) {
            this.plugin = plugin;
            this.rankPlaceholdersService = plugin.getRankPlaceholdersService();
            this.funnyguildsVersion = plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String identifier) {
            if (player == null) {
                return "";
            }

            Option<User> userOption = this.plugin.getUserManager().findByPlayer(player);
            if (userOption.isEmpty()) {
                return "";
            }

            User user = userOption.get();
            
            Component inputText = Component.text("{" + identifier.toUpperCase(Locale.ROOT) + "}");
            Component replacedText = this.plugin.getTablistPlaceholdersService().format(
                    user,
                    inputText,
                    user
            );
            replacedText = this.rankPlaceholdersService.format(user, replacedText, user);
            return LEGACY_SERIALIZER.serialize(replacedText);
        }

        @Override // one - seeing the placeholder, two - about which the placeholder is
        public String onPlaceholderRequest(Player observer, Player target, String identifier) {
            if (observer == null || target == null || !identifier.equalsIgnoreCase("tag")) {
                return "";
            }

            UserManager userManager = this.plugin.getUserManager();
            Option<User> userObserverOption = userManager.findByPlayer(observer);
            Option<User> userTargetOption = userManager.findByPlayer(target);

            if (userObserverOption.isEmpty() || userTargetOption.isEmpty()) {
                return "";
            }

            Component relationalTag = this.plugin.getPluginConfiguration().relationalTag.chooseAndPrepareTag(
                    userObserverOption.get().getGuild().orNull(),
                    userTargetOption.get().getGuild().orNull()
            );
            return LEGACY_SERIALIZER.serialize(relationalTag);
        }

        @Override
        public @NotNull String getAuthor() {
            return "FunnyGuilds Team";
        }

        @Override
        public @NotNull String getIdentifier() {
            return "funnyguilds";
        }

        @Override
        public String getRequiredPlugin() {
            return "FunnyGuilds";
        }

        @Override
        public @NotNull String getVersion() {
            return this.funnyguildsVersion;
        }

        @Override
        public boolean persist() {
            return true;
        }
    }

}
