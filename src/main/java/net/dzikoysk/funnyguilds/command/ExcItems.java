package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.element.gui.GuiItem;
import net.dzikoysk.funnyguilds.util.element.gui.GuiWindow;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcItems implements Executor
{
    @Override
    public void execute(CommandSender sender, String[] args)
    {
        final Player p = (Player) sender;
        final PluginConfig c = Settings.getConfig();

        if (p.hasPermission("funnyguilds.vip")) {
            final GuiWindow window = new GuiWindow("Przedmioty potrzebne na gildie:", c.createItemsVip);
            window.setCloseEvent(close -> {
                window.unregister();
            });
            for (ItemStack is : c.createItemsVip) {
                final GuiItem item = new GuiItem(is);
                window.setToNextFree(item);
            }
            window.fillEmpty(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
            window.open(p);
            return;
        }

        final GuiWindow window = new GuiWindow("Przedmioty potrzebne na gildie:", c.createItems);
        window.setCloseEvent(close -> {
            window.unregister();
        });
        for (ItemStack is : c.createItems) {
            final GuiItem item = new GuiItem(is);
            window.setToNextFree(item);
        }
        window.fillEmpty(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        window.open(p);
    }

}
