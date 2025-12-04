package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        PandaStream.of(guiItems).forEach(originalItem -> {
            ItemStack item = originalItem.clone();
            ItemBuilder builder = new ItemBuilder(item);

            if (this.config.addLoreLines && (this.config.createItems.contains(item) || this.config.createItemsVip.contains(item))) {
                int requiredAmount = item.getAmount();
                int inventoryAmount = ItemUtils.getItemAmount(item, player.getInventory());
                int enderChestAmount = ItemUtils.getItemAmount(item, player.getEnderChest());

                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{REQ-AMOUNT}", requiredAmount)
                        .register("{PINV-AMOUNT}", inventoryAmount)
                        .register("{PINV-PERCENT}", FunnyStringUtils.getPercent(inventoryAmount, requiredAmount))
                        .register("{EC-AMOUNT}", enderChestAmount)
                        .register("{EC-PERCENT}", FunnyStringUtils.getPercent(enderChestAmount, requiredAmount))
                        .register("{ALL-AMOUNT}", inventoryAmount + enderChestAmount)
                        .register("{ALL-PERCENT}", FunnyStringUtils.getPercent(inventoryAmount + enderChestAmount, requiredAmount));

                List<String> loreLines = new ArrayList<>();
                PandaStream.of(this.config.guiItemsLore).map(line -> formatter.replace(line.getValue())).forEach(loreLines::add);

                builder.setLore(loreLines, true);

                if (!this.config.guiItemsName.isEmpty()) {
                    builder.setName(ItemUtils.translateTextPlaceholder(this.config.guiItemsName.getValue(), Collections.emptySet(), item), true);
                }
            }

            gui.setToNextFree(builder.getItem());
        });

        gui.open(player);
    }

}
