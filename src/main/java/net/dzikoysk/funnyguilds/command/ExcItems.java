package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.gui.GuiItem;
import net.dzikoysk.funnyguilds.element.gui.GuiWindow;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ExcItems implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PluginConfig config = Settings.getConfig();

        List<ItemStack> guiItems = config.guiItems;
        String title = config.guiItemsTitle;

        if (!config.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            guiItems = config.guiItemsVip;
            title = config.guiItemsVipTitle;
        }

        GuiWindow gui = new GuiWindow(title, guiItems.size() / 9 + (guiItems.size() % 9 != 0 ? 1 : 0));
        gui.setCloseEvent(close -> gui.unregister());

        for (ItemStack item : guiItems) {
            item = item.clone();
            
            if (config.addLoreLines && (config.createItems.contains(item) || config.createItemsVip.contains(item))) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                
                int pinvAmount = ItemUtils.getItemAmount(item, player.getInventory());
                int ecAmount = ItemUtils.getItemAmount(item, player.getEnderChest());
                
                for (String line : config.guiItemsLore) {
                    line = StringUtils.replace(line, "{PINV-AMOUNT}", Integer.toString(pinvAmount));
                    line = StringUtils.replace(line, "{PINV-PERCENT}", ChatUtils.getPercent(pinvAmount, item.getAmount()));
                    line = StringUtils.replace(line, "{EC-AMOUNT}", Integer.toString(ecAmount));
                    line = StringUtils.replace(line, "{EC-PERCENT}", ChatUtils.getPercent(ecAmount, item.getAmount()));
                    line = StringUtils.replace(line, "{ALL-AMOUNT}", Integer.toString(pinvAmount + ecAmount));
                    line = StringUtils.replace(line, "{ALL-PERCENT}", ChatUtils.getPercent(pinvAmount + ecAmount, item.getAmount()));
                    
                    lore.add(line);
                }
                
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            gui.setToNextFree(new GuiItem(item));
        }
        
        gui.open(player);
    }

}
