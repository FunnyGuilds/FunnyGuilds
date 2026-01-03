package net.dzikoysk.funnyguilds.feature.tablist;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.tablist.TablistPage;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerList;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListConstants;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;
import net.dzikoysk.funnyguilds.shared.MapUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.function.TriConsumer;

public class TablistRenderer {

    private final PlayerList playerList;
    private final UserManager userManager;

    private final Map<Integer, Component> unformattedCells;
    private final int cellCount;
    private final Component header;
    private final Component footer;

    private final boolean animated;
    private final List<TablistPage> pages;
    private final int pagesCount;

    private final SkinTexture[] cellTextures;
    private final int cellPing;

    private volatile int cycle;
    private volatile int currentPage;

    public TablistRenderer(
            PlayerListAccessor playerListAccessor,
            UserManager userManager,
            Map<Integer, Component> unformattedCells,
            Component header,
            Component footer,
            boolean animated,
            List<TablistPage> pages,
            Map<NumberRange, SkinTexture> cellTextures,
            int cellPing,
            boolean fillCells
    ) {
        this.userManager = userManager;
        this.unformattedCells = new HashMap<>(unformattedCells);
        this.header = header;
        this.footer = footer;
        this.animated = animated;
        this.pages = pages;
        this.pagesCount = pages.size();
        this.cellTextures = prepareTextures(cellTextures);
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

    public void broadcast() {
        this.createUnformattedCellsAndRun((currentUnformattedCells, currentHeader, currentFooter) -> {
            // Don't remove this toArray - iterating over online players asynchronously without shallow copy could 
            // occur with ConcurrentModificationException (See GH-2031).
            for (Player player : Bukkit.getOnlinePlayers().toArray(new Player[0])) {
                User user = this.userManager.findByUuid(player.getUniqueId()).orNull();
                if (user == null) {
                    continue;
                }
                this.sendToPlayer(
                        player,
                        user,
                        currentUnformattedCells,
                        currentHeader,
                        currentFooter
                );
            }
        });
    }

    public void send(Player player, User user) {
        this.createUnformattedCellsAndRun((currentUnformattedCells, currentHeader, currentFooter) -> {
            this.sendToPlayer(player, user, currentUnformattedCells, currentHeader, currentFooter);
        });
    }
   
    private void sendToPlayer(Player player, User user, Map<Integer, Component> unformattedCells, Component header, Component footer) {
        Component[] preparedCells = this.putVarsPrepareCells(player, user, unformattedCells, header, footer);
        Component preparedHeader = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT];
        Component preparedFooter = preparedCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1];

        this.playerList.send(player, preparedCells, preparedHeader, preparedFooter, this.cellTextures, this.cellPing, Collections.emptySet());
    }

    private void createUnformattedCellsAndRun(TriConsumer<Map<Integer, Component>, Component, Component> unformattedCellsConsumer) {
        Map<Integer, Component> currentUnformattedCells = new HashMap<>(this.unformattedCells);
        Component currentHeader = this.header;
        Component currentFooter = this.footer;

        if (this.animated) {
            TablistPage page = this.pages.get(this.currentPage);
            if (page != null) {
                if (page.cells() != null) {
                    currentUnformattedCells.putAll(page.cells());
                }

                if (page.header() != null) {
                    currentHeader = page.header();
                }

                if (page.footer() != null) {
                    currentFooter = page.footer();
                }
            }
        }

        unformattedCellsConsumer.consume(
                currentUnformattedCells,
                currentHeader,
                currentFooter
        );
    }

    synchronized void updatePageCycle() {
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

    private Component[] putVarsPrepareCells(Player player, User user, Map<Integer, Component> tablistPattern, Component header, Component footer) {
        Component[] allCells = new Component[PlayerListConstants.DEFAULT_CELL_COUNT + 2]; // Additional two for header/footer
        for (int i = 0; i < this.cellCount; i++) {
            allCells[i] = this.putVars(player, user, tablistPattern.getOrDefault(i + 1, Component.empty()));
        }

        allCells[PlayerListConstants.DEFAULT_CELL_COUNT] = this.putVars(player, user, header);
        allCells[PlayerListConstants.DEFAULT_CELL_COUNT + 1] = this.putVars(player, user, footer);

        return allCells;
    }

    private Component putVars(Player player, User user, Component cell) {
        FunnyFormatter formatter = new FunnyFormatter();
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        formatter.register(plugin.getTablistPlaceholdersService().asReplaceable(user));
        formatter.register(plugin.getRankPlaceholdersService().asReplaceable(user));
        formatter.register(HookUtils.placeholdersReplaceable(player));

        return plugin.getMessageService().replaceInComponent(user, cell, formatter);
    }

    private static SkinTexture[] prepareTextures(Map<NumberRange, SkinTexture> cellTextures) {
        SkinTexture[] textures = new SkinTexture[PlayerListConstants.DEFAULT_CELL_COUNT];

        cellTextures.forEach((range, texture) -> {
            for (int i = range.getMinRange().intValue(); i <= range.getMaxRange().intValue(); i++) {
                textures[i - 1] = texture;
            }
        });

        return textures;
    }

}
