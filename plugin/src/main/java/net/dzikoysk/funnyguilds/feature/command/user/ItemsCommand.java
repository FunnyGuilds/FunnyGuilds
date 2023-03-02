package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import panda.std.stream.PandaStream;

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
        List<ItemStack> guiItems = this.config.guiItems;
        String title = this.config.guiItemsTitle.getValue();

        if (!this.config.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            guiItems = this.config.guiItemsVip;
            title = this.config.guiItemsVipTitle.getValue();
        }

        GuiWindow gui = new GuiWindow(title, guiItems.size() / 9 + (guiItems.size() % 9 != 0 ? 1 : 0));
        PandaStream.of(guiItems).forEach(item -> {
            item = item.clone();

            if (this.config.addLoreLines && (this.config.createItems.contains(item) || this.config.createItemsVip.contains(item))) {
                ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    FunnyGuilds.getPluginLogger().warning("Item meta is not defined (" + item + ")");
                    return;
                }

                int requiredAmount = item.getAmount();
                int inventoryAmount = ItemUtils.getItemAmount(item, player.getInventory());
                int enderChestAmount = ItemUtils.getItemAmount(item, player.getEnderChest());

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>(this.config.guiItemsLore.size());
                }

                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{REQ-AMOUNT}", requiredAmount)
                        .register("{PINV-AMOUNT}", inventoryAmount)
                        .register("{PINV-PERCENT}", FunnyStringUtils.getPercent(inventoryAmount, requiredAmount))
                        .register("{EC-AMOUNT}", enderChestAmount)
                        .register("{EC-PERCENT}", FunnyStringUtils.getPercent(enderChestAmount, requiredAmount))
                        .register("{ALL-AMOUNT}", inventoryAmount + enderChestAmount)
                        .register("{ALL-PERCENT}", FunnyStringUtils.getPercent(inventoryAmount + enderChestAmount, requiredAmount));

                lore.addAll(PandaStream.of(this.config.guiItemsLore).map(line -> formatter.format(line.getValue())).toList());

                if (!this.config.guiItemsName.isEmpty()) {
                    meta.setDisplayName(ItemUtils.translateTextPlaceholder(this.config.guiItemsName.getValue(), Collections.emptySet(), item));
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            gui.setToNextFree(item);
        });

        gui.open(player);
    }

}
