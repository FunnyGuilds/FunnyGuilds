package net.dzikoysk.funnyguilds.element.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class GuiActionHandler implements Listener {
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        InventoryHolder normalHolder = event.getInventory().getHolder();

        if (!(normalHolder instanceof FunnyHolder)) {
            return;
        }

        FunnyHolder holder = (FunnyHolder) normalHolder;

        event.setCancelled(true);
        holder.handleClick(event);
    }

    @EventHandler
    public void onInteract(final InventoryInteractEvent event) {
        InventoryHolder normalHolder = event.getInventory().getHolder();

        if (!(normalHolder instanceof FunnyHolder)) {
            return;
        }

        event.setCancelled(true);
    }
}
