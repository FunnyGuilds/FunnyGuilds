package net.dzikoysk.funnyguilds.hook;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public final class MVdWPlaceholderAPIHook {
    
    public static void initPlaceholderHook() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        
        for (Entry<String,TablistVariable> variable : DefaultTablistVariables.getFunnyVariables().entrySet()) {
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_" + variable.getKey(), new PlaceholderReplacer() {

                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                    OfflinePlayer target = event.getOfflinePlayer();
                    if (target == null) {
                        return "";
                    }
                    
                    User user = User.get(target.getUniqueId());
                    if (user == null) {
                        return "";
                    }
                    
                    return variable.getValue().get(user);
                }
            });
        }
        
        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-" + index, new PlaceholderReplacer() {
                
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                    return Parser.parseRank("{GTOP-" + index + "}");
                }
            });
        }
        
        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + index, new PlaceholderReplacer() {
                
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                    return Parser.parseRank("{PTOP-" + index + "}");
                }
            });
        }
        
        FunnyLogger.info("MVdWPlaceholderAPI hook has been enabled!");
    }
    
    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }
    
    private MVdWPlaceholderAPIHook() {}
    
}
