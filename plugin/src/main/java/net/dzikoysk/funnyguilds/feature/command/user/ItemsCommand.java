package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<ItemStack> guiItems = this.pluginConfig.guiItems;
        String title = this.pluginConfig.guiItemsTitle;

        if (!this.pluginConfig.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            guiItems = this.pluginConfig.guiItemsVip;
            title = this.pluginConfig.guiItemsVipTitle;
        }

        GuiWindow gui = new GuiWindow(title, guiItems.size() / 9 + (guiItems.size() % 9 != 0 ? 1 : 0));

        for (ItemStack item : guiItems) {
            item = item.clone();

            if (this.pluginConfig.addLoreLines && (this.pluginConfig.createItems.contains(item) || this.pluginConfig.createItemsVip.contains(item))) {
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
                    lore = new ArrayList<>(this.pluginConfig.guiItemsLore.size());
                }

                for (String line : this.pluginConfig.guiItemsLore) {
                    line = StringUtils.replace(line, "{REQ-AMOUNT}", Integer.toString(requiredAmount));
                    line = StringUtils.replace(line, "{PINV-AMOUNT}", Integer.toString(inventoryAmount));
                    line = StringUtils.replace(line, "{PINV-PERCENT}", ChatUtils.getPercent(inventoryAmount, requiredAmount));
                    line = StringUtils.replace(line, "{EC-AMOUNT}", Integer.toString(enderChestAmount));
                    line = StringUtils.replace(line, "{EC-PERCENT}", ChatUtils.getPercent(enderChestAmount, requiredAmount));
                    line = StringUtils.replace(line, "{ALL-AMOUNT}", Integer.toString(inventoryAmount + enderChestAmount));
                    line = StringUtils.replace(line, "{ALL-PERCENT}", ChatUtils.getPercent(inventoryAmount + enderChestAmount, requiredAmount));

                    lore.add(line);
                }

                if (!Objects.equals(this.pluginConfig.guiItemsName, "")) {
                    meta.setDisplayName(ItemUtils.translateTextPlaceholder(this.pluginConfig.guiItemsName, null, item));
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            gui.setToNextFree(item);
        }

        gui.open(player);
    }

}
