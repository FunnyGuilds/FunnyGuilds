package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@FunnyComponent
public final class ItemsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.items.name}",
            description = "${user.items.description}",
            aliases = "${user.items.aliases}",
            permission = "funnyguilds.items",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player) {
        List<ItemStack> guiItems = config.guiItems;
        String title = config.guiItemsTitle.getValue();

        if (!config.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            guiItems = config.guiItemsVip;
            title = config.guiItemsVipTitle.getValue();
        }

        GuiWindow gui = new GuiWindow(title, guiItems.size() / 9 + (guiItems.size() % 9 != 0 ? 1 : 0));

        for (ItemStack item : guiItems) {
            item = item.clone();

            if (config.addLoreLines && (config.createItems.contains(item) || config.createItemsVip.contains(item))) {
                ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    FunnyGuilds.getPluginLogger().warning("Item meta is not defined (" + item + ")");
                    continue;
                }

                int requiredAmount = item.getAmount();
                int inventoryAmount = ItemUtils.getItemAmount(item, player.getInventory());
                int enderChestAmount = ItemUtils.getItemAmount(item, player.getEnderChest());

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>(config.guiItemsLore.size());
                }

                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{REQ-AMOUNT}", requiredAmount)
                        .register("{PINV-AMOUNT}", inventoryAmount)
                        .register("{PINV-PERCENT}", ChatUtils.getPercent(inventoryAmount, requiredAmount))
                        .register("{EC-AMOUNT}", enderChestAmount)
                        .register("{EC-PERCENT}", ChatUtils.getPercent(enderChestAmount, requiredAmount))
                        .register("{ALL-AMOUNT}", inventoryAmount + enderChestAmount)
                        .register("{ALL-PERCENT}", ChatUtils.getPercent(inventoryAmount + enderChestAmount, requiredAmount));

                lore.addAll(config.guiItemsLore.stream().map(line -> formatter.format(line.getValue())).collect(Collectors.toList()));

                if (!config.guiItemsName.isEmpty()) {
                    meta.setDisplayName(ItemUtils.translateTextPlaceholder(config.guiItemsName.getValue(), null, item));
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            gui.setToNextFree(item);
        }

        gui.open(player);
    }

}
