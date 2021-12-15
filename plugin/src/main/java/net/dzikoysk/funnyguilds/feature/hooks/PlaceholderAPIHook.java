package net.dzikoysk.funnyguilds.feature.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends AbstractPluginHook{

    private static final String FUNNYGUILDS_VERSION = FunnyGuilds.getInstance().getDescription().getVersion();

    PlaceholderAPIHook(String name) {
        super(name);
    }

    @Override
    public void init() {
        new FunnyGuildsPlaceholder().register();
        super.init();
    }

    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }

    private static class FunnyGuildsPlaceholder extends PlaceholderExpansion {

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            if (player == null) {
                return "";
            }

            User user = UserUtils.get(player.getUniqueId());
            if (user == null) {
                return "";
            }

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
            return FUNNYGUILDS_VERSION;
        }

        @Override
        public boolean persist() {
            return true;
        }
    }

}
