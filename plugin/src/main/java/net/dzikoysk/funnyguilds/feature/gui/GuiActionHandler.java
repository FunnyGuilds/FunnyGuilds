package net.dzikoysk.funnyguilds.feature.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiActionHandler implements Listener {
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return;
        }

        InventoryHolder inventoryHolder = inventory.getHolder();
        if (!(inventoryHolder instanceof FunnyHolder)) {
            return;
        }

        FunnyHolder funnyHolder = (FunnyHolder) inventoryHolder;

        event.setCancelled(true);
        funnyHolder.handleClick(event);
    }

    @EventHandler
    public void onInteract(final InventoryInteractEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return;
        }

        if (!(inventory.getHolder() instanceof FunnyHolder)) {
            return;
        }

        event.setCancelled(true);
    }
}
