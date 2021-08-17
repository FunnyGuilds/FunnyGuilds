package net.dzikoysk.funnyguilds.feature.hooks;

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI;
import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public final class BungeeTabListPlusHook {

    public static void initVariableHook() {       
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        for (Entry<String,TablistVariable> variable : DefaultTablistVariables.getFunnyVariables().entrySet()) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_" + variable.getKey()) {
                
                @Override
                public String getReplacement(Player player) {                  
                    User user = User.get(player);
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
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_gtop-" + index) {

                @Override
                public String getReplacement(Player player) {
                    User user = User.get(player);
                    return RankUtils.parseRank(user, "{GTOP-" + index + "}");
                }
            });
        }

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new Variable("funnyguilds_ptop-" + index) {

                @Override
                public String getReplacement(Player player) {
                    return RankUtils.parseRank(null, "{PTOP-" + index + "}");
                }
            });
        }

        FunnyGuilds.getPluginLogger().info("BungeeTabListPlus hook has been enabled!");
    }

    private BungeeTabListPlusHook() {}
    
}
