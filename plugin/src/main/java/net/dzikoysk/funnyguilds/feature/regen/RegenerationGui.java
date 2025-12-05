package net.dzikoysk.funnyguilds.feature.regen;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.CostType;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.RegenerationConfig;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for terrain regeneration.
 */
public class RegenerationGui {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final RegionRegenerationManager regenerationManager;
    private final Guild guild;
    private final User user;
    private final Player player;
    private final Duration maxBlockAge;
    
    private int selectedBlocks;
    private final int totalBlocks;

    public RegenerationGui(
            FunnyGuilds plugin,
            PluginConfiguration config,
            MessageService messageService,
            RegionRegenerationManager regenerationManager,
            Guild guild,
            User user,
            Player player
    ) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.regenerationManager = regenerationManager;
        this.guild = guild;
        this.user = user;
        this.player = player;
        this.maxBlockAge = config.guildPanel.regeneration.maxBlockAge;
        this.totalBlocks = regenerationManager.getDestroyedBlockCount(guild, maxBlockAge);
        this.selectedBlocks = Math.min(totalBlocks, 1);
    }

    /**
     * Opens the regeneration GUI for the player.
     */
    public void open() {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;
        PanelConfiguration panelConfig = this.config.guildPanel;

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(regenConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, regenConfig.rows);

        // Fill empty slots
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }

        // Info item
        addInfoItem(gui, regenConfig);

        // Amount buttons
        addAmountButtons(gui, regenConfig);

        // Preset buttons
        addPresetButtons(gui, regenConfig);

        // Confirm button
        addConfirmButton(gui, regenConfig);

        // Back/Cancel button
        addBackButton(gui, regenConfig, panelConfig);

        gui.open(this.player);
    }

    private void addInfoItem(GuiWindow gui, RegenerationConfig regenConfig) {
        String costStr = formatCost(calculateCost());

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{BLOCKS-COUNT}", this.totalBlocks)
                .register("{SELECTED-BLOCKS}", this.selectedBlocks)
                .register("{TOTAL-COST}", costStr);

        List<String> lore = new ArrayList<>();
        for (var line : regenConfig.infoItem.lore) {
            lore.add(formatter.replace(line.getValue()));
        }

        ItemStack item = new ItemBuilder(regenConfig.infoItem.material)
                .setName(formatter.replace(regenConfig.infoItem.name.getValue()), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(regenConfig.infoItem.slot, item);
    }

    private void addAmountButtons(GuiWindow gui, RegenerationConfig regenConfig) {
        RegenerationConfig.AmountButtons buttons = regenConfig.amountButtons;

        // -10 button
        ItemStack minus10 = new ItemBuilder(buttons.materialMinus10)
                .setName(buttons.nameMinus10.getValue(), true)
                .getItem();
        gui.setItem(buttons.slotMinus10, minus10, event -> {
            event.setCancelled(true);
            changeAmount(-10);
        });

        // -1 button
        ItemStack minus1 = new ItemBuilder(buttons.materialMinus1)
                .setName(buttons.nameMinus1.getValue(), true)
                .getItem();
        gui.setItem(buttons.slotMinus1, minus1, event -> {
            event.setCancelled(true);
            changeAmount(-1);
        });

        // +1 button
        ItemStack plus1 = new ItemBuilder(buttons.materialPlus1)
                .setName(buttons.namePlus1.getValue(), true)
                .getItem();
        gui.setItem(buttons.slotPlus1, plus1, event -> {
            event.setCancelled(true);
            changeAmount(1);
        });

        // +10 button
        ItemStack plus10 = new ItemBuilder(buttons.materialPlus10)
                .setName(buttons.namePlus10.getValue(), true)
                .getItem();
        gui.setItem(buttons.slotPlus10, plus10, event -> {
            event.setCancelled(true);
            changeAmount(10);
        });
    }

    private void addPresetButtons(GuiWindow gui, RegenerationConfig regenConfig) {
        RegenerationConfig.PresetButtons presets = regenConfig.presetButtons;

        // 25% button
        ItemStack preset25 = new ItemBuilder(presets.material)
                .setName(presets.name25.getValue(), true)
                .getItem();
        gui.setItem(presets.slot25, preset25, event -> {
            event.setCancelled(true);
            setPercentage(25);
        });

        // 50% button
        ItemStack preset50 = new ItemBuilder(presets.material)
                .setName(presets.name50.getValue(), true)
                .getItem();
        gui.setItem(presets.slot50, preset50, event -> {
            event.setCancelled(true);
            setPercentage(50);
        });

        // 100% button
        ItemStack preset100 = new ItemBuilder(presets.material)
                .setName(presets.name100.getValue(), true)
                .getItem();
        gui.setItem(presets.slot100, preset100, event -> {
            event.setCancelled(true);
            setPercentage(100);
        });
    }

    private void addConfirmButton(GuiWindow gui, RegenerationConfig regenConfig) {
        RegenerationConfig.ConfirmButton confirm = regenConfig.confirmButton;
        String costStr = formatCost(calculateCost());

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{SELECTED-BLOCKS}", this.selectedBlocks)
                .register("{TOTAL-COST}", costStr);

        List<String> lore = new ArrayList<>();
        for (var line : confirm.lore) {
            lore.add(formatter.replace(line.getValue()));
        }

        ItemStack item = new ItemBuilder(confirm.material)
                .setName(confirm.name.getValue(), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(confirm.slot, item, event -> {
            event.setCancelled(true);
            confirmRegeneration();
        });
    }

    private void addBackButton(GuiWindow gui, RegenerationConfig regenConfig, PanelConfiguration panelConfig) {
        RegenerationConfig.BackItem backItem = regenConfig.backItem;

        if (!backItem.enabled) {
            return;
        }

        List<String> lore = new ArrayList<>();
        for (var line : backItem.lore) {
            lore.add(line.getValue());
        }

        ItemStack item = new ItemBuilder(backItem.material)
                .setName(backItem.name.getValue(), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(backItem.slot, item, event -> {
            event.setCancelled(true);
            this.player.closeInventory();
        });
    }

    private void changeAmount(int delta) {
        int newAmount = this.selectedBlocks + delta;
        int maxBlocks = this.config.guildPanel.regeneration.maxBlocksToRegenerate;
        if (maxBlocks <= 0) {
            maxBlocks = this.totalBlocks;
        }
        
        this.selectedBlocks = Math.max(0, Math.min(newAmount, Math.min(this.totalBlocks, maxBlocks)));
        open();
    }

    private void setPercentage(int percentage) {
        int newAmount = (this.totalBlocks * percentage) / 100;
        int maxBlocks = this.config.guildPanel.regeneration.maxBlocksToRegenerate;
        if (maxBlocks <= 0) {
            maxBlocks = this.totalBlocks;
        }
        
        this.selectedBlocks = Math.max(1, Math.min(newAmount, Math.min(this.totalBlocks, maxBlocks)));
        open();
    }

    private double calculateCost() {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;
        
        if (regenConfig.costType == CostType.VAULT) {
            return this.selectedBlocks * regenConfig.vaultPricePerBlock;
        } else {
            // Item cost: (blocks * itemCostPer100Blocks) / 100
            return Math.ceil((double) (this.selectedBlocks * regenConfig.itemCostPer100Blocks) / 100.0);
        }
    }

    private String formatCost(double cost) {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;
        
        if (regenConfig.costType == CostType.VAULT) {
            return this.messageService.get(this.config.defaultLocale, config -> config.regenerationCostVault)
                    .replace("{AMOUNT}", String.format("%.2f", cost));
        } else {
            String itemName = regenConfig.itemCostMaterial.toString().toLowerCase(Locale.ROOT);
            return this.messageService.get(this.config.defaultLocale, config -> config.regenerationCostItem)
                    .replace("{AMOUNT}", String.valueOf((int) cost))
                    .replace("{ITEM}", itemName);
        }
    }

    private void confirmRegeneration() {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;

        // Check minimum blocks
        if (this.selectedBlocks < regenConfig.minBlocksToRegenerate) {
            this.messageService.getMessage(config -> config.regenerationMinBlocks)
                    .with("{MIN}", String.valueOf(regenConfig.minBlocksToRegenerate))
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check if already in progress
        if (this.regenerationManager.isRegenerationInProgress(this.guild)) {
            this.messageService.getMessage(config -> config.regenerationInProgress)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check and process payment
        double cost = calculateCost();
        if (!processPayment(cost)) {
            return;
        }

        // Get blocks to regenerate (with expiration check)
        List<DestroyedBlock> blocksToRegenerate = this.regenerationManager.getBlocksForRegeneration(
                this.guild, 
                this.selectedBlocks,
                this.maxBlockAge
        );

        if (blocksToRegenerate.isEmpty()) {
            this.messageService.getMessage(config -> config.regenerationNoBlocks)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Close inventory
        this.player.closeInventory();

        // Send start message
        this.messageService.getMessage(config -> config.regenerationStarted)
                .with("{BLOCKS}", String.valueOf(blocksToRegenerate.size()))
                .receiver(this.player)
                .send();

        // Start regeneration task
        final double finalCost = cost;
        RegenerationTask task = new RegenerationTask(
                this.plugin,
                this.regenerationManager,
                this.guild,
                blocksToRegenerate,
                regenConfig.batchSize,
                result -> handleRegenerationComplete(result, finalCost)
        );
        task.start();
    }

    private boolean processPayment(double cost) {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;

        if (regenConfig.costType == CostType.VAULT) {
            if (!HookManager.VAULT.isPresent() || !VaultHook.isEconomyHooked()) {
                this.messageService.getMessage(config -> config.panelVaultNotAvailable)
                        .receiver(this.player)
                        .send();
                return false;
            }

            if (!VaultHook.canAfford(this.player, cost)) {
                this.messageService.getMessage(config -> config.regenerationNotEnoughMoney)
                        .with("{PRICE}", formatCost(cost))
                        .receiver(this.player)
                        .send();
                return false;
            }

            VaultHook.withdrawFromPlayerBank(this.player, cost);
            return true;
        } else {
            int itemAmount = (int) cost;
            ItemStack requiredItem = new ItemStack(regenConfig.itemCostMaterial, itemAmount);
            
            if (!this.player.getInventory().containsAtLeast(requiredItem, itemAmount)) {
                this.messageService.getMessage(config -> config.regenerationNotEnoughItems)
                        .with("{ITEM}", regenConfig.itemCostMaterial.toString().toLowerCase(Locale.ROOT))
                        .with("{AMOUNT}", String.valueOf(itemAmount))
                        .receiver(this.player)
                        .send();
                return false;
            }

            this.player.getInventory().removeItem(requiredItem);
            return true;
        }
    }

    private void handleRegenerationComplete(RegenerationTask.RegenerationResult result, double paidCost) {
        // Send completion message
        this.messageService.getMessage(config -> config.regenerationCompleted)
                .with("{REGENERATED}", String.valueOf(result.getRegeneratedCount()))
                .with("{SKIPPED}", String.valueOf(result.getSkippedCount()))
                .with("{TOTAL}", String.valueOf(result.getTotalRequested()))
                .receiver(this.guild)
                .send();

        // Handle refund for skipped blocks
        if (result.getSkippedCount() > 0) {
            processRefund(result.getSkippedCount(), paidCost, result.getTotalRequested());
        }
    }

    private void processRefund(int skippedBlocks, double paidCost, int totalRequested) {
        RegenerationConfig regenConfig = this.config.guildPanel.regeneration;
        
        // Calculate refund amount proportionally
        double refundRatio = (double) skippedBlocks / totalRequested;
        double refundAmount = paidCost * refundRatio;

        if (refundAmount <= 0) {
            return;
        }

        if (regenConfig.costType == CostType.VAULT) {
            if (HookManager.VAULT.isPresent() && VaultHook.isEconomyHooked()) {
                // Deposit refund to the player who initiated the regeneration
                VaultHook.depositToPlayerBank(this.player, refundAmount);
                
                this.messageService.getMessage(config -> config.regenerationRefund)
                        .with("{REFUND}", String.format("%.2f$", refundAmount))
                        .receiver(this.guild)
                        .send();
            }
        } else {
            int itemRefund = (int) Math.floor(refundAmount);
            if (itemRefund > 0) {
                ItemStack refundItem = new ItemStack(regenConfig.itemCostMaterial, itemRefund);
                
                this.plugin.getFunnyServer().getPlayer(this.user).peek(p -> {
                    p.getInventory().addItem(refundItem);
                });
                
                this.messageService.getMessage(config -> config.regenerationRefund)
                        .with("{REFUND}", itemRefund + "x " + regenConfig.itemCostMaterial.toString().toLowerCase(Locale.ROOT))
                        .receiver(this.guild)
                        .send();
            }
        }
    }
}
