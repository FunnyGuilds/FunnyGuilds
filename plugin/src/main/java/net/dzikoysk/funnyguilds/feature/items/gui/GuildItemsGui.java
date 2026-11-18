package net.dzikoysk.funnyguilds.feature.items.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class GuildItemsGui {

    private final Player player;
    private final User user;
    private final GuildItemSet set;
    private final String setName;
    private final ItemsConfiguration config;
    private final MessageService messageService;

    private ChestGui chestGui;
    private StaticPane itemsPane;

    private boolean showOnlyMissing = false;

    public GuildItemsGui(
            Player player,
            User user,
            GuildItemSet set,
            String setName,
            ItemsConfiguration config,
            MessageService messageService
    ) {
        this.player = player;
        this.user = user;
        this.set = set;
        this.setName = setName;
        this.config = config;
        this.messageService = messageService;
    }

    public void open() {
        this.itemsPane = new StaticPane(9, this.config.gui.rows);
        this.chestGui = GuildItemsGuiFactory.buildChestGui(this, this.itemsPane);
        this.chestGui.show(player);
    }

    public void close() {
        if (player.isOnline()) {
            player.closeInventory();
        }
    }

    public void toggleView() {
        this.showOnlyMissing = !this.showOnlyMissing;
        if (chestGui == null) return;
        GuildItemsGuiFactory.populatePane(this, this.itemsPane);
        chestGui.update();
    }

    public Player getPlayer() { return player; }
    public User getUser() { return user; }
    public GuildItemSet getSet() { return set; }
    public String getSetName() { return setName; }
    public ItemsConfiguration getConfig() { return config; }
    public MessageService getMessageService() { return messageService; }
    public boolean isShowOnlyMissing() { return showOnlyMissing; }
    public ChestGui getChestGui() { return chestGui; }

}
