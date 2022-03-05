package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.feature.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.feature.tablist.variable.TablistVariablesParser;
import net.dzikoysk.funnyguilds.feature.tablist.variable.VariableParsingResult;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import panda.std.Option;

public class IndividualPlayerList {

    private final User user;
    private final PlayerList playerList;
    private final TablistVariablesParser variableParser;

    private final Map<Integer, String> unformattedCells;
    private final int cellCount;
    private final String header;
    private final String footer;
    private final int cellPing;

    private final List<TablistPage> pages;
    private final int pagesCount;

    private final boolean enableLegacyPlaceholders;

    private int cycle = 0;
    private int currentPage = 0;

    public IndividualPlayerList(User user,
                                PlayerListAccessor playerListAccessor,
                                Map<Integer, String> unformattedCells,
                                String header, String footer,
                                List<TablistPage> pages,
                                int cellPing,
                                boolean fillCells,
                                boolean enableLegacyPlaceholders) {
        this.user = user;
        this.variableParser = new TablistVariablesParser();

        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.pages = pages;
        this.pagesCount = pages.size();
        this.cellPing = cellPing;

        this.enableLegacyPlaceholders = enableLegacyPlaceholders;

        if (!fillCells) {
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

        this.playerList = playerListAccessor.createPlayerList(this.cellCount);

        DefaultTablistVariables.install(this.variableParser);
    }

    public void send() {
        Map<Integer, String> unformattedCells = this.unformattedCells;
        String header = this.header;
        String footer = this.footer;

        if (this.pagesCount > 0) {
            this.cycle++;

            int pageCycles = this.pages.get(this.currentPage).cycles;
            if (this.cycle + 1 >= pageCycles) {
                this.cycle = 0;
                this.currentPage++;

                if (this.currentPage >= this.pagesCount) {
                    this.currentPage = 0;
                }
            }

            TablistPage page = this.pages.get(this.currentPage);
            if (page != null) {
                if (page.playerList != null) {
                    unformattedCells.putAll(page.playerList);
                }

                if (page.playerListHeader != null) {
                    header = page.playerListHeader;
                }

                if (page.playerListFooter != null) {
                    footer = page.playerListFooter;
                }
            }
        }

        String[] preparedCells = this.putVarsPrepareCells(unformattedCells, header, footer);
        String preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
        String preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

        this.user.getPlayer()
                .peek(player -> this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, this.cellPing));
    }

    private String[] putVarsPrepareCells(Map<Integer, String> tablistPattern, String header, String footer) {
        String[] allCells = new String[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
        for (int i = 0; i < this.cellCount; i++) {
            allCells[i] = this.putTop(tablistPattern.getOrDefault(i + 1, ""));
        }
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = header;
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = footer;
        String mergedCells = StringUtils.join(allCells, '\0');
        return StringUtils.splitPreserveAllTokens(this.putVars(mergedCells), '\0');
    }

    private String putTop(String cell) {
        String temp = RankUtils.parseTopPosition(this.user, cell);

        temp = RankUtils.parseTop(this.user, temp);
        if (enableLegacyPlaceholders) {
            temp = RankUtils.parseRank(this.user, temp);
        }

        return temp;
    }

    private String putVars(String cell) {
        String formatted = cell;

        Option<Player> playerOption = this.user.getPlayer();
        if (playerOption.isEmpty()) {
            return formatted;
        }
        Player player = playerOption.get();

        VariableParsingResult result = this.variableParser.createResultFor(this.user);
        formatted = result.replaceInString(formatted);
        formatted = ChatUtils.colored(formatted);

        formatted = HookUtils.replacePlaceholders(player, formatted);

        return formatted;
    }

}
