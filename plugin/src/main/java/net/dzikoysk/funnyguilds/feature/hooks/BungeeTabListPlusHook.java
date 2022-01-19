package net.dzikoysk.funnyguilds.feature.hooks;

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI;
import codecrafter47.bungeetablistplus.api.bukkit.Variable;
import java.util.Map.Entry;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariable;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.entity.Player;

public class BungeeTabListPlusHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    BungeeTabListPlusHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void earlyInit() {
    }

    @Override
    public void init() {
        UserManager userManager = this.plugin.getUserManager();

        for (Entry<String, TablistVariable> variable : DefaultTablistVariables.getFunnyVariables().entrySet()) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable(variable.getKey(), player -> userManager.findByPlayer(player)
                    .map(user -> variable.getValue().get(user))
                    .orElseGet("")));
        }

        // Guild TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("guild_top_" + i, player -> {
                User user = userManager.findByPlayer(player).getOrNull();
                return RankUtils.parseRank(user, "{GTOP-" + index + "}");
            }));
        }

        // User TOP, positions 1-100
        for (int i = 1; i <= 100; i++) {
            final int index = i;
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, new FunctionVariable("funnyguilds_ptop-" + i, player ->
                    RankUtils.parseRank(null, "{PTOP-" + index + "}")));
        }
    }

    private static class FunctionVariable extends Variable {

        private final Function<Player, String> replacement;

        public FunctionVariable(String name, Function<Player, String> replacement) {
            super(name);
            this.replacement = replacement;
        }

        @Override
        public String getReplacement(Player player) {
            return this.replacement.apply(player);
        }

    }

}
