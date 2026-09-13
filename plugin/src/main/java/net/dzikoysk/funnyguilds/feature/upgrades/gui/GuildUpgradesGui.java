package net.dzikoysk.funnyguilds.feature.upgrades.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.upgrades.UpgradesMenuConfiguration;
import net.dzikoysk.funnyguilds.feature.upgrades.GuildUpgradeService;
import net.dzikoysk.funnyguilds.feature.upgrades.GuildUpgradeService.PurchaseResult;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class GuildUpgradesGui {

    private final Player player;
    private final User user;
    private final Guild guild;
    private final UpgradesMenuConfiguration menuConfig;
    private final GuildUpgradeService upgradeService;
    private final MessageService messageService;

    private ChestGui chestGui;

    public GuildUpgradesGui(
            Player player,
            User user,
            Guild guild,
            UpgradesMenuConfiguration menuConfig,
            GuildUpgradeService upgradeService,
            MessageService messageService
    ) {
        this.player = player;
        this.user = user;
        this.guild = guild;
        this.menuConfig = menuConfig;
        this.upgradeService = upgradeService;
        this.messageService = messageService;
    }

    public void open() {
        this.chestGui = GuildUpgradesGuiFactory.buildChestGui(this);
        this.chestGui.show(this.player);
    }

    public void close() {
        if (this.player.isOnline()) {
            this.player.closeInventory();
        }
    }

    public void refresh() {
        if (this.chestGui == null) {
            return;
        }
        GuildUpgradesGuiFactory.populatePanes(this, this.chestGui);
        this.chestGui.update();
    }

    public void attemptPurchase(String key) {
        PurchaseResult result = this.upgradeService.purchase(this.player, this.user, this.guild, key);

        switch (result) {
            case SUCCESS -> {
                int newLevel = this.upgradeService.getLevel(this.guild, key);
                this.messageService.getMessage(config -> config.upgradesPurchased)
                        .receiver(this.guild)
                        .with("{PLAYER}", this.player.getName())
                        .with("{UPGRADE}", key)
                        .with("{LEVEL}", newLevel)
                        .send();
            }
            case MAX_LEVEL -> this.messageService.getMessage(config -> config.upgradesMaxLevel)
                    .receiver(this.player)
                    .send();
            case CANNOT_AFFORD -> this.messageService.getMessage(config -> config.upgradesCannotAfford)
                    .receiver(this.player)
                    .send();
            case DISABLED -> this.messageService.getMessage(config -> config.upgradesDisabled)
                    .receiver(this.player)
                    .send();
            case UNKNOWN_UPGRADE -> this.messageService.getMessage(config -> config.upgradesUnknownUpgrade)
                    .receiver(this.player)
                    .send();
            case CANCELLED -> {
            }
        }

        this.refresh();
    }

    public Player getPlayer() { return this.player; }
    public User getUser() { return this.user; }
    public Guild getGuild() { return this.guild; }
    public UpgradesMenuConfiguration getMenuConfig() { return this.menuConfig; }
    public GuildUpgradeService getUpgradeService() { return this.upgradeService; }
    public MessageService getMessageService() { return this.messageService; }
    public ChestGui getChestGui() { return this.chestGui; }

}
