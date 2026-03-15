package net.dzikoysk.funnyguilds.feature.items.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class GuildItemsGui {

    private final Player player;
    private final User user;
    private final GuildItemSet set;
    private final String setName;
    private final ItemsConfiguration config;
    private final MessageService messageService;
    private final FunnyGuilds plugin;

    private ChestGui chestGui;
    private GuildGuiRefreshTask refreshTask;
    private boolean closed = false;

    private boolean showOnlyMissing = false;

    public GuildItemsGui(
            Player player,
            User user,
            GuildItemSet set,
            String setName,
            ItemsConfiguration config,
            MessageService messageService,
            FunnyGuilds plugin
    ) {
        this.player = player;
        this.user = user;
        this.set = set;
        this.setName = setName;
        this.config = config;
        this.messageService = messageService;
        this.plugin = plugin;
    }

    public void open() {
        this.chestGui = GuildItemsGuiFactory.buildChestGui(this);
        this.chestGui.show(player);

        this.refreshTask = new GuildGuiRefreshTask(this, player);
        this.refreshTask.runTaskTimer(plugin, 20L, 20L);
    }

    public void close() {
        this.closed = true;
        if (this.refreshTask != null && !this.refreshTask.isCancelled()) {
            this.refreshTask.cancel();
        }
        if (player.isOnline()) {
            player.closeInventory();
        }
    }

    public void refresh() {
        if (closed || chestGui == null) return;
        chestGui.getPanes().clear();
        GuildItemsGuiFactory.populatePanes(this, chestGui);
        chestGui.update();
    }

    public void toggleView() {
        this.showOnlyMissing = !this.showOnlyMissing;
        refresh();
    }

    public void onInventoryClosed() {
        this.closed = true;
        if (this.refreshTask != null && !this.refreshTask.isCancelled()) {
            this.refreshTask.cancel();
        }
    }

    public Player getPlayer() { return player; }
    public User getUser() { return user; }
    public GuildItemSet getSet() { return set; }
    public String getSetName() { return setName; }
    public ItemsConfiguration getConfig() { return config; }
    public MessageService getMessageService() { return messageService; }
    public boolean isShowOnlyMissing() { return showOnlyMissing; }
    public boolean isClosed() { return closed; }
    public ChestGui getChestGui() { return chestGui; }

}

