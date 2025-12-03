package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import panda.std.Option;

public class IndividualPlayerList {

    private final User user;
    private final PlayerList playerList;
    private final FunnyServer funnyServer;

    private final Map<Integer, Component> unformattedCells;
    private final int cellCount;
    private final Component header;
    private final Component footer;

    private final boolean animated;
    private final List<TablistPage> pages;
    private final int pagesCount;

    private final Map<NumberRange, SkinTexture> cellTextures;
    private final int cellPing;

    private int cycle;
    private int currentPage;

    public IndividualPlayerList(User user, PlayerListAccessor playerListAccessor, FunnyServer funnyServer,
                                Map<Integer, Component> unformattedCells, Component header, Component footer, boolean animated,
                                List<TablistPage> pages, Map<NumberRange, SkinTexture> cellTextures,
                                int cellPing, boolean fillCells) {
        this.user = user;
        this.funnyServer = funnyServer;

        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.animated = animated;
        this.pages = pages;
        this.pagesCount = pages.size();
        this.cellTextures = cellTextures;
        this.cellPing = cellPing;

        if (!fillCells) {
            Entry<Integer, Component> entry = MapUtils.findTheMaximumEntryByKey(unformattedCells);
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
        Map<Integer, Component> unformattedCells = new HashMap<>(this.unformattedCells);
        Component header = this.header;
        Component footer = this.footer;

        if (this.animated) {
            TablistPage page = this.pages.get(this.currentPage);
            if (page != null) {
                if (page.cells() != null) {
                    unformattedCells.putAll(page.cells());
                }

                if (page.header() != null) {
                    header = page.header();
                }

                if (page.footer() != null) {
                    footer = page.footer();
                }
            }
        }

        Component[] preparedCells = this.putVarsPrepareCells(unformattedCells, header, footer);
        Component preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
        Component preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

        SkinTexture[] preparedCellsTextures = this.putTexturePrepareCells();

        this.funnyServer.getPlayer(this.user).peek(player -> {
            this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, preparedCellsTextures, this.cellPing, Collections.emptySet());
        });
    }

    void updatePageCycle() {
        if (!this.animated) {
            return;
        }

        this.cycle++;

        int pageCycles = this.pages.get(this.currentPage).cycles();
        if (this.cycle + 1 >= pageCycles) {
            this.cycle = 0;
            this.currentPage++;

            if (this.currentPage >= this.pagesCount) {
                this.currentPage = 0;
            }
        }
    }

    private Component[] putVarsPrepareCells(Map<Integer, Component> tablistPattern, Component header, Component footer) {
        Component[] allCells = new Component[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
        for (int i = 0; i < this.cellCount; i++) {
            allCells[i] = this.putTop(tablistPattern.getOrDefault(i + 1, Component.empty()));
        }

        allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = header;
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = footer;

        //TODO: reimplement
        //Component mergedCells = Joiner.on("\0").join(allCells);
        //return StringUtils.splitPreserveAllTokens(this.putVars(mergedCells), '\0');
        throw new UnsupportedOperationException("Reimplement splitPreserveAllTokens for Component");
    }

    private Component putTop(Component cell) {
        return FunnyGuilds.getInstance().getRankPlaceholdersService().format(this.user, cell, this.user);
    }

    private Component putVars(Component cell) {
        Option<Player> playerOption = this.funnyServer.getPlayer(this.user);
        if (playerOption.isEmpty()) {
            return cell;
        }

        Player player = playerOption.get();

        //TODO: reimplement
//        formatted = FunnyGuilds.getInstance().getTablistPlaceholdersService().format(this.user, formatted, this.user);
//        formatted = HookUtils.replacePlaceholders(player, formatted);

        return cell;
    }

    public SkinTexture[] putTexturePrepareCells() {
        SkinTexture[] textures = new SkinTexture[PlayerListConstants.DEFAULT_CELL_COUNT];

        this.cellTextures.forEach((range, texture) -> {
            for (int i = range.getMinRange().intValue(); i <= range.getMaxRange().intValue(); i++) {
                textures[i - 1] = texture;
            }
        });

        return textures;
    }

}
