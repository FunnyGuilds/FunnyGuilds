package net.dzikoysk.funnyguilds.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.entity.Player;

public final class PlaceholderAPIHook {

    public static void initPlaceholderHook() {
        new FunnyGuildsPlaceholder().register();
        FunnyLogger.info("PlaceholderAPI hook has been enabled!");
    }
    
    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.setPlaceholders(user, base);
    }
    
    private PlaceholderAPIHook() {}
    
    private static class FunnyGuildsPlaceholder extends PlaceholderExpansion {

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {         
            if (player == null) {
                return "";
            }
            
            User user = User.get(player);
            if (user == null) {
                return "";
            }
            
            TablistVariable variable = DefaultTablistVariables.getFunnyVariables().get(identifier.toLowerCase());
            if (variable != null) {
                return variable.get(user);
            }
            
            return Parser.parseRank("{" + identifier.toUpperCase() + "}");
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
        public String getPlugin() {
            return "FunnyGuilds";
        }

        @Override
        public String getVersion() {
            return FunnyGuilds.getInstance().getDescription().getVersion();
        }
    }
    
}
