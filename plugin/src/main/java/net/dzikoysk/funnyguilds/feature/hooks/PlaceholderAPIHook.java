package net.dzikoysk.funnyguilds.feature.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;

public class PlaceholderAPIHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    PlaceholderAPIHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void init() {
        new FunnyGuildsPlaceholder(plugin).register();
        super.init();
    }

    public String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    private static class FunnyGuildsPlaceholder extends PlaceholderExpansion {

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

            TablistVariable variable = DefaultTablistVariables.getFunnyVariables().get(identifier.toLowerCase());
            if (variable != null) {
                return variable.get(user);
            }

            return RankUtils.parseRank(user, "{" + identifier.toUpperCase() + "}");
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
