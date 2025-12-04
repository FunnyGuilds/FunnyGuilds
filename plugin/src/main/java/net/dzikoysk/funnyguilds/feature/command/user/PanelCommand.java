package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.CostType;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.EffectItem;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import panda.std.Option;

@FunnyComponent
public class PanelCommand extends AbstractFunnyCommand {

    private static final int MILLIS_PER_TICK = 50;

    @FunnyCommand(
            name = "${user.panel.name}",
            description = "${user.panel.description}",
            aliases = "${user.panel.aliases}",
            permission = "funnyguilds.panel",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.PANEL) User user, Guild guild) {
        PanelConfiguration panelConfig = this.config.guildPanel;

        if (!panelConfig.enabled) {
            this.messageService.getMessage(config -> config.panelDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        openPanelGui(player, guild, panelConfig);
    }

    private void openPanelGui(Player player, Guild guild, PanelConfiguration panelConfig) {
        String title = new FunnyFormatter()
                .register("{TAG}", guild.getTag())
                .register("{GUILD}", guild.getName())
                .replace(panelConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, panelConfig.rows);

        // Wypełnienie pustych slotów
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }

        // Item informacyjny
        if (panelConfig.infoItem.enabled) {
            addInfoItem(gui, guild, panelConfig);
        }

        // Item przedłużenia
        if (panelConfig.extendItem.enabled) {
            addExtendItem(gui, player, guild, panelConfig);
        }

        // Item powiększenia
        if (panelConfig.enlargeItem.enabled) {
            addEnlargeItem(gui, player, guild, panelConfig);
        }

        // Item PvP
        if (panelConfig.pvpItem.enabled) {
            addPvpItem(gui, player, guild, panelConfig);
        }

        // Efekty gildii
        if (panelConfig.effects.enabled) {
            addEffectItems(gui, player, guild, panelConfig);
        }

        gui.open(player);
    }

    private void addInfoItem(GuiWindow gui, Guild guild, PanelConfiguration panelConfig) {
        GuildRank rank = guild.getRank();
        
        String validityStr = this.messageService.get(this.config.defaultLocale, config -> config.dateFormat)
                .format(guild.getValidity());

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{TAG}", guild.getTag())
                .register("{GUILD}", guild.getName())
                .register("{POINTS}", rank.getPoints())
                .register("{KILLS}", rank.getKills())
                .register("{DEATHS}", rank.getDeaths())
                .register("{ASSISTS}", rank.getAssists())
                .register("{MEMBERS}", guild.getMembers().size())
                .register("{VALIDITY}", validityStr)
                .register("{LIVES}", guild.getLives());

        List<String> lore = new ArrayList<>();
        for (var line : panelConfig.infoItem.lore) {
            lore.add(formatter.replace(line.getValue()));
        }

        ItemStack item = new ItemBuilder(panelConfig.infoItem.material)
                .setName(formatter.replace(panelConfig.infoItem.name.getValue()), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(panelConfig.infoItem.slot, item);
    }

    private void addExtendItem(GuiWindow gui, Player player, Guild guild, PanelConfiguration panelConfig) {
        String validityStr = this.messageService.get(this.config.defaultLocale, config -> config.dateFormat)
                .format(guild.getValidity());
        String extendTimeStr = TimeUtils.formatTime(this.config.validityTime);
        String priceStr = formatItemPrice(this.config.validityItems);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{VALIDITY}", validityStr)
                .register("{EXTEND-TIME}", extendTimeStr)
                .register("{PRICE}", priceStr);

        List<String> lore = new ArrayList<>();
        for (var line : panelConfig.extendItem.lore) {
            lore.add(formatter.replace(line.getValue()));
        }

        ItemStack item = new ItemBuilder(panelConfig.extendItem.material)
                .setName(formatter.replace(panelConfig.extendItem.name.getValue()), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(panelConfig.extendItem.slot, item, event -> {
            event.setCancelled(true);
            player.closeInventory();
            player.performCommand(this.config.commands.validity.name);
        });
    }

    private void addEnlargeItem(GuiWindow gui, Player player, Guild guild, PanelConfiguration panelConfig) {
        Option<Region> regionOption = guild.getRegion();
        
        if (regionOption.isEmpty()) {
            return;
        }

        Region region = regionOption.get();
        int currentSize = region.getSize();
        int enlargementLevel = region.getEnlargementLevel();
        
        FunnyFormatter formatter;
        List<String> lore = new ArrayList<>();

        if (enlargementLevel >= this.config.enlargeItems.size()) {
            // Maksymalny rozmiar
            formatter = new FunnyFormatter()
                    .register("{CURRENT-SIZE}", currentSize);
            
            for (var line : panelConfig.enlargeItem.maxSizeLore) {
                lore.add(formatter.replace(line.getValue()));
            }
        } else {
            int newSize = currentSize + this.config.enlargeSize;
            ItemStack need = this.config.enlargeItems.get(enlargementLevel);
            String priceStr = need.getAmount() + "x " + need.getType().toString().toLowerCase(Locale.ROOT);

            formatter = new FunnyFormatter()
                    .register("{CURRENT-SIZE}", currentSize)
                    .register("{NEW-SIZE}", newSize)
                    .register("{PRICE}", priceStr);

            for (var line : panelConfig.enlargeItem.lore) {
                lore.add(formatter.replace(line.getValue()));
            }
        }

        ItemStack item = new ItemBuilder(panelConfig.enlargeItem.material)
                .setName(formatter.replace(panelConfig.enlargeItem.name.getValue()), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(panelConfig.enlargeItem.slot, item, event -> {
            event.setCancelled(true);
            player.closeInventory();
            player.performCommand(this.config.commands.enlarge.name);
        });
    }

    private void addPvpItem(GuiWindow gui, Player player, Guild guild, PanelConfiguration panelConfig) {
        boolean pvpEnabled = guild.hasPvPEnabled();
        
        Material material = pvpEnabled ? panelConfig.pvpItem.materialOn : panelConfig.pvpItem.materialOff;
        String name = pvpEnabled ? panelConfig.pvpItem.nameOn.getValue() : panelConfig.pvpItem.nameOff.getValue();
        List<String> lore = new ArrayList<>();
        
        var loreSource = pvpEnabled ? panelConfig.pvpItem.loreOn : panelConfig.pvpItem.loreOff;
        for (var line : loreSource) {
            lore.add(line.getValue());
        }

        ItemStack item = new ItemBuilder(material)
                .setName(name, true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(panelConfig.pvpItem.slot, item, event -> {
            event.setCancelled(true);
            player.closeInventory();
            player.performCommand(this.config.commands.pvp.name);
        });
    }

    private void addEffectItems(GuiWindow gui, Player player, Guild guild, PanelConfiguration panelConfig) {
        addSingleEffectItem(gui, player, guild, panelConfig.effects.strength);
        addSingleEffectItem(gui, player, guild, panelConfig.effects.speed);
        addSingleEffectItem(gui, player, guild, panelConfig.effects.fireResistance);
        addSingleEffectItem(gui, player, guild, panelConfig.effects.regeneration);
    }

    private void addSingleEffectItem(GuiWindow gui, Player player, Guild guild, EffectItem effectConfig) {
        if (!effectConfig.enabled) {
            return;
        }

        PotionEffectType effectType = effectConfig.getPotionEffectType();
        if (effectType == null) {
            return;
        }

        String durationStr = TimeUtils.formatTime(effectConfig.duration);
        String priceStr = effectConfig.costType == CostType.VAULT 
                ? String.format("%.2f$", effectConfig.vaultPrice)
                : effectConfig.itemCostAmount + "x " + effectConfig.itemCostMaterial.toString().toLowerCase(Locale.ROOT);
        int amplifierDisplay = effectConfig.amplifier + 1;

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{DURATION}", durationStr)
                .register("{AMPLIFIER}", amplifierDisplay)
                .register("{PRICE}", priceStr);

        List<String> lore = new ArrayList<>();
        for (var line : effectConfig.lore) {
            lore.add(formatter.replace(line.getValue()));
        }

        ItemStack item = new ItemBuilder(effectConfig.material)
                .setName(formatter.replace(effectConfig.name.getValue()), true)
                .setLore(lore, true)
                .getItem();

        gui.setItem(effectConfig.slot, item, event -> {
            event.setCancelled(true);
            
            // Sprawdź czy gracz nadal jest liderem
            Option<User> userOption = this.userManager.findByUuid(player.getUniqueId());
            if (userOption.isEmpty()) {
                return;
            }
            
            User user = userOption.get();
            Option<Guild> currentGuildOption = user.getGuild();
            if (currentGuildOption.isEmpty() || !currentGuildOption.get().isOwner(user)) {
                this.messageService.getMessage(config -> config.panelIsNotLeader)
                        .receiver(player)
                        .send();
                player.closeInventory();
                return;
            }

            Guild currentGuild = currentGuildOption.get();

            // Obsługa płatności
            if (effectConfig.costType == CostType.VAULT) {
                if (!HookManager.VAULT.isPresent() || !VaultHook.isEconomyHooked()) {
                    this.messageService.getMessage(config -> config.panelVaultNotAvailable)
                            .receiver(player)
                            .send();
                    return;
                }

                if (!VaultHook.canAfford(player, effectConfig.vaultPrice)) {
                    this.messageService.getMessage(config -> config.panelNotEnoughMoney)
                            .receiver(player)
                            .with("{PRICE}", String.format("%.2f$", effectConfig.vaultPrice))
                            .send();
                    return;
                }

                VaultHook.withdrawFromPlayerBank(player, effectConfig.vaultPrice);
            } else {
                ItemStack requiredItem = new ItemStack(effectConfig.itemCostMaterial, effectConfig.itemCostAmount);
                if (!player.getInventory().containsAtLeast(requiredItem, effectConfig.itemCostAmount)) {
                    this.messageService.getMessage(config -> config.panelNotEnoughItems)
                            .receiver(player)
                            .with("{ITEM}", effectConfig.itemCostMaterial.toString().toLowerCase(Locale.ROOT))
                            .with("{AMOUNT}", String.valueOf(effectConfig.itemCostAmount))
                            .send();
                    return;
                }

                player.getInventory().removeItem(requiredItem);
            }

            // Nadaj efekt wszystkim członkom gildii
            int durationTicks = (int) (effectConfig.duration.toMillis() / MILLIS_PER_TICK);
            PotionEffect effect = new PotionEffect(effectType, durationTicks, effectConfig.amplifier);
            
            for (User member : currentGuild.getMembers()) {
                this.funnyServer.getPlayer(member).peek(memberPlayer -> memberPlayer.addPotionEffect(effect));
            }

            String effectName = ChatUtils.decolor(effectConfig.name.getValue()).replaceAll("&[0-9a-fA-FklmnorKLMNOR]", "");
            this.messageService.getMessage(config -> config.panelEffectBought)
                    .receiver(currentGuild)
                    .with("{EFFECT}", effectName)
                    .with("{DURATION}", TimeUtils.formatTime(effectConfig.duration))
                    .send();

            // Odśwież GUI
            player.closeInventory();
            openPanelGui(player, currentGuild, this.config.guildPanel);
        });
    }

    private String formatItemPrice(List<ItemStack> items) {
        if (items.isEmpty()) {
            return this.messageService.get(this.config.defaultLocale, config -> config.panelFreePrice);
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            sb.append(item.getAmount()).append("x ").append(item.getType().toString().toLowerCase(Locale.ROOT));
            if (i < items.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

