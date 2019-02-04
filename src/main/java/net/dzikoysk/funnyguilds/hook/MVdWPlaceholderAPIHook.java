package net.dzikoysk.funnyguilds.hook;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.panda_lang.panda.utilities.commons.objects.StringUtils;

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
                    
                    User user = User.get(target.getUniqueId(), target.getName());

                    if (user == null) {
                        return StringUtils.EMPTY;
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
                    return RankUtils.parseRank("{GTOP-" + index + "}");
                }

            });
        }
        
        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-" + index, new PlaceholderReplacer() {
                
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
                    return RankUtils.parseRank("{PTOP-" + index + "}");
                }

            });
        }

        FunnyGuilds.getInstance().getPluginLogger().info("MVdWPlaceholderAPI hook has been enabled!");
    }
    
    public static String replacePlaceholders(Player user, String base) {
        return PlaceholderAPI.replacePlaceholders(user, base);
    }
    
    private MVdWPlaceholderAPIHook() {}
    
}
