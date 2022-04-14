package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.shared.MapUtil;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import panda.std.Option;

public class IndividualPlayerList {

    private final User user;
    private final PlayerList playerList;

    private final Map<Integer, String> unformattedCells;
    private final int cellCount;
    private final String header;
    private final String footer;
    private final int cellPing;

    private final boolean animated;
    private final List<TablistPage> pages;
    private final int pagesCount;

    private final boolean enableLegacyPlaceholders;

    private int cycle = 0;
    private int currentPage = 0;

    public IndividualPlayerList(User user,
                                PlayerListAccessor playerListAccessor,
                                Map<Integer, String> unformattedCells,
                                String header, String footer,
                                boolean animated,
                                List<TablistPage> pages,
                                int cellPing,
                                boolean fillCells,
                                boolean enableLegacyPlaceholders) {
        this.user = user;

        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.animated = animated;
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
    }

    public void send() {
        Map<Integer, String> unformattedCells = this.unformattedCells;
        String header = this.header;
        String footer = this.footer;

        if (this.animated) {
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

        SkinTexture[] preparedCellsTextures = this.putTexturePrepareCells();

        this.user.getPlayer()
                .peek(player -> this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, preparedCellsTextures, this.cellPing));
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
        return FunnyGuilds.getInstance().getRankPlaceholdersService().format(cell, this.user);
    }

    private String putVars(String cell) {
        String formatted = cell;

        Option<Player> playerOption = this.user.getPlayer();
        if (playerOption.isEmpty()) {
            return formatted;
        }
        Player player = playerOption.get();

        formatted = FunnyGuilds.getInstance().getTablistPlaceholdersService().format(formatted, this.user);
        formatted = ChatUtils.colored(formatted);
        formatted = HookUtils.replacePlaceholders(player, formatted);

        return formatted;
    }

    public SkinTexture[] putTexturePrepareCells() {
        SkinTexture[] textures = new SkinTexture[PlayerListConstants.DEFAULT_CELL_COUNT];
        for (int i = 0; i < this.cellCount; i++) {
            textures[i] = new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTU5NDI5NjI1MTAzNiwKICAicHJvZmlsZUlkIiA6ICJhNzFjNTQ5MmQwNTE0ZDg3OGFiOTEwZmRmZmRmYzgyZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcHBsZTU0NDciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBlNGIyNmJkMDcwNjFmMDQwZGUxNmJiOTVlMTY3NDU2MWU0NjQzZGY3Nzg0MDcxYzUwOGFjZTMxNDkwZjJkOCIKICAgIH0KICB9Cn0=", "rHxHLDlEDkDvGGo8foVbmtsqUeQH++nA3cLDGYdkZ8mn9vCgE0dmbGKEnVydLB95pJoy9vCFE2Kzy7iG78H1iNWDi8VUGcY7TPRLfGihYuO9MavZ35nbKvY0to66dTMpW3GSFpOQ/sGGmBjXfEAipa7tiASK2hiDv/F5JMGqkMB0oczlXyYdcKsrRNWRL29qE3wZX2mrMX3g5liJduviAWyeNjlvX012mqDOQ2xeH1OPAotwE6mEWLwZUWfSjqbHrBRLX13DhwCylSNqP7FLGMeXghN6ESLD9vz26xpFzGVLKXww1RXH+2jJ/07G0QNz2oYURF84AerliODWoZJc0HTjchL0CVRv5Vkhy+OwagLsvcvcJSm//O4imxk44sa3n5j3yzHP4aHxq7SUTO53IsMD0y23tCYHsYahhAcwVLrKpoFF6sApvn4CNHsRzeuKRszg33h8OM9esCYqH8ZUmaf9XzXyM3xSQ65coc00bzl1UMQ8zCO7kNanb5klkICSG0xkbU4L9U2dc5zp0oK/2ORdP5oWsLfbnATSqFYSPcqABb0jTYf7XSU2rBUTeNF+YWXcUf2e2scc8zrwJP9L0qReCIfKAP3gnI28ujwk/AZ53RRVjDlxD39rqcIz2I1P6CSJzcJN6aOYo3BBrg2qZrSU77pDEmghrPVj6wBjMpM=");
        }
        return textures;
    }

}
