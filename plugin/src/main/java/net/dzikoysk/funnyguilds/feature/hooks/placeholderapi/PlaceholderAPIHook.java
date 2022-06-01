package net.dzikoysk.funnyguilds.feature.hooks.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;

public class PlaceholderAPIHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public PlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        new FunnyGuildsPlaceholder(plugin).register();
        return HookInitResult.SUCCESS;
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    public static String replacePlaceholders(Player userOne, Player userTwo, String base) {
        return PlaceholderAPI.setRelationalPlaceholders(userOne, userTwo, base);
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
            String lowerIdentifier = identifier.toLowerCase();

            if (lowerIdentifier.contains("position-")) {
                return rankPlaceholdersService.formatTopPosition("{" + identifier.toUpperCase() + "}", user);
            }
            else if (lowerIdentifier.contains("top-")) {
                String temp = rankPlaceholdersService.formatTop("{" + identifier.toUpperCase() + "}", user);
                if (this.plugin.getPluginConfiguration().top.enableLegacyPlaceholders) {
                    temp = rankPlaceholdersService.formatRank(temp, user);
                }

                return temp;
            }
            else {
                return this.plugin.getTablistPlaceholdersService().formatIdentifier(identifier, user);
            }
        }

        @Override // one - seeing the placeholder, two - about which the placeholder is
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            if (one == null || two == null || !identifier.equalsIgnoreCase("prefix")) {
                return "";
            }

            UserManager userManager = this.plugin.getUserManager();
            Option<User> userOneOption = userManager.findByPlayer(one);
            Option<User> userTwoOption = userManager.findByPlayer(two);

            if (userOneOption.isEmpty() || userTwoOption.isEmpty()) {
                return "";
            }

            return IndividualPrefix.chooseAndPreparePrefix(
                    this.plugin.getPluginConfiguration(),
                    userOneOption.get().getGuild().orElseGet((Guild) null),
                    userTwoOption.get().getGuild().orElseGet((Guild) null)
            );
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
            return funnyguildsVersion;
        }

        @Override
        public boolean persist() {
            return true;
        }
    }

}
