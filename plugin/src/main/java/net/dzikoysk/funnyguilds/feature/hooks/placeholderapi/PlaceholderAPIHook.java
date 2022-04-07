package net.dzikoysk.funnyguilds.feature.hooks.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;
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

    public String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    public String replacePlaceholders(Player userOne, Player userTwo, String base) {
        return PlaceholderAPI.setRelationalPlaceholders(userOne, userTwo, base);
    }

    private static class FunnyGuildsPlaceholder extends PlaceholderExpansion implements Relational {

        private final FunnyGuilds plugin;
        private final String funnyguildsVersion;

        private FunnyGuildsPlaceholder(FunnyGuilds plugin) {
            this.plugin = plugin;
            this.funnyguildsVersion = plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            if (player == null) {
                return "";
            }

            Option<User> userOption = this.plugin.getUserManager().findByPlayer(player);
            if (userOption.isEmpty()) {
                return "";
            }
            User user = userOption.get();

            String value = this.plugin.getPlaceholdersService().getTablistPlaceholders()
                    .getPlaceholder(identifier)
                    .map(placeholder -> placeholder.get(user))
                    .orNull();
            if (value != null) {
                return value;
            }

            if (identifier.toLowerCase().contains("position-")) {
                return RankUtils.parseTopPosition(user, "{" + identifier.toUpperCase() + "}");
            }
            else if (identifier.toLowerCase().contains("top-")) {
                String temp = RankUtils.parseTop(user, "{" + identifier.toUpperCase() + "}");
                if (this.plugin.getPluginConfiguration().top.enableLegacyPlaceholders) {
                    temp = RankUtils.parseRank(user, temp);
                }
                return temp;
            }

            return "";
        }

        @Override
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            PluginConfiguration config = this.plugin.getPluginConfiguration();
            UserManager userManager = this.plugin.getUserManager();

            if (one == null || two == null) {
                return null;
            }

            Option<User> userOneOption = userManager.findByPlayer(one);
            Option<User> userTwoOption = userManager.findByPlayer(two);
            if (userOneOption.isEmpty() || userTwoOption.isEmpty()) {
                return null;
            }
            User userOne = userOneOption.get();
            User userTwo = userTwoOption.get();

            if (identifier.equalsIgnoreCase("prefix")) {
                Option<Guild> guildOneOption = userOne.getGuild();
                Option<Guild> guildTwoOption = userTwo.getGuild();
                if (guildTwoOption.isEmpty()) {
                    return null;
                }
                Guild guildOne = guildOneOption.get();
                Guild guildTwo = guildTwoOption.get();

                if (guildOneOption.isPresent()) {
                    if (guildOne.getAllies().contains(guildTwo)) {
                        return IndividualPrefix.preparePrefix(config.prefixAllies.getValue(), guildTwo);
                    }
                    else if (guildOne.getEnemies().contains(guildTwo) || guildTwo.getEnemies().contains(guildOne)) {
                        return IndividualPrefix.preparePrefix(config.prefixEnemies.getValue(), guildTwo);
                    }
                    else {
                        return IndividualPrefix.preparePrefix(config.prefixOther.getValue(), guildTwo);
                    }
                }
                else {
                    return IndividualPrefix.preparePrefix(config.prefixOther.getValue(), guildTwo);
                }
            }

            return null;
        }


        @Override
        public String getAuthor() {
            return "FunnyGuilds Team";
        }

        @Override
        public String getIdentifier() {
            return "funnyguilds";
        }

        @Override
        public String getRequiredPlugin() {
            return "FunnyGuilds";
        }

        @Override
        public String getVersion() {
            return funnyguildsVersion;
        }

        @Override
        public boolean persist() {
            return true;
        }
    }

}
