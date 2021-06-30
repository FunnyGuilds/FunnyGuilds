package net.dzikoysk.funnyguilds.element.tablist;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariablesParser;
import net.dzikoysk.funnyguilds.element.tablist.variable.VariableParsingResult;
import net.dzikoysk.funnyguilds.hook.MVdWPlaceholderAPIHook;
import net.dzikoysk.funnyguilds.hook.PlaceholderAPIHook;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TablistBroadcastHandler implements Runnable {

    private final Map<UUID, IndividualPlayerList> PLAYER_LISTS = new HashMap<>();

    @Override
    public void run() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        if (! config.playerListEnable) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = plugin.getUserManager().getUser(player).getOrNull();

            IndividualPlayerList individualPlayerList = PLAYER_LISTS.computeIfAbsent(player.getUniqueId(),
                    k -> new IndividualPlayerList(
                            user,
                            plugin.getNmsAccessor().getPlayerListAccessor().createPlayerList(player, 80),
                            config.playerList,
                            config.playerListHeader, config.playerListFooter,
                            config.playerListPing,
                            config.playerListFillCells
                    ));

            individualPlayerList.send();
        }
    }

    static class IndividualPlayerList {
        private final User user;
        private final PlayerList playerList;
        private final TablistVariablesParser variableParser;

        private final Map<Integer, String> unformattedCells;
        private final int cellCount;
        private final String header;
        private final String footer;
        private final int cellPing;

        public IndividualPlayerList(User user,
                                    PlayerList playerList,
                                    Map<Integer, String> unformattedCells,
                                    String header, String footer,
                                    int cellPing,
                                    boolean fillCells) {
            this.user = user;
            this.playerList = playerList;
            this.variableParser = new TablistVariablesParser();

            this.unformattedCells = unformattedCells;
            this.header = header;
            this.footer = footer;
            this.cellPing = cellPing;

            if (! fillCells) {
                Entry<Integer, String> entry = MapUtil.findTheMaximumEntryByKey(unformattedCells);

                if (entry != null) {
                    this.cellCount = entry.getKey();
                }
                else {
                    this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
                }
            }
            else {
                this.cellCount = PlayerListConstants.DEFAULT_CELL_COUNT;
            }

            DefaultTablistVariables.install(this.variableParser);
        }

        public void send() {
            String[] preparedCells = this.putVarsPrepareCells(this.unformattedCells, this.header, this.footer);
            String preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
            String preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

            this.playerList.send(preparedCells, preparedHeader, preparedFooter, this.cellPing);
        }

        private String[] putVarsPrepareCells(Map<Integer, String> tablistPattern, String header, String footer) {
            String[] allCells = new String[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
            for (int i = 0; i < this.cellCount; i++) {
                allCells[i] = this.putRank(tablistPattern.getOrDefault(i + 1, ""));
            }
            allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = header;
            allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = footer;
            String mergedCells = StringUtils.join(allCells, '\0');
            return StringUtils.splitPreserveAllTokens(this.putVars(mergedCells), '\0');
        }

        private String putRank(String cell) {
            String temp = RankUtils.parseRank(this.user, cell);
            if (temp != null) {
                return temp;
            }
            return cell;
        }

        private String putVars(String cell) {
            String formatted = cell;

            Player player = this.user.getPlayer();

            if (player == null) {
                return formatted;
            }

            VariableParsingResult result = this.variableParser.createResultFor(this.user);
            formatted = result.replaceInString(formatted);
            formatted = ChatUtils.colored(formatted);

            if (PluginHook.isPresent(PluginHook.PLUGIN_PLACEHOLDERAPI)) {
                formatted = PlaceholderAPIHook.replacePlaceholders(player, formatted);
            }

            if (PluginHook.isPresent(PluginHook.PLUGIN_MVDWPLACEHOLDERAPI)) {
                formatted = MVdWPlaceholderAPIHook.replacePlaceholders(player, formatted);
            }

            return formatted;
        }
    }
}
