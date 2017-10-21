package net.dzikoysk.funnyguilds.util.element.gui;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class GuiActionHandler implements Listener {
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getType().equals(InventoryType.CHEST)) {
            GuiWindow window = GuiWindow.getWindow(e.getInventory().getTitle());
            if (window != null) {
                GuiItem item = window.getItem(e.getSlot());
                if (item != null) {
                    item.handleClick(e);
                }
                e.setResult(Event.Result.DENY);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onOpen(final InventoryOpenEvent e) {
        GuiWindow window = GuiWindow.getWindow(e.getInventory().getTitle());
        if (window != null) {
            window.handleOpen(e);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        GuiWindow window = GuiWindow.getWindow(e.getInventory().getTitle());
        if (window != null) {
            window.handleClose(e);
        }
    }

    @EventHandler
    public void onInteract(final InventoryInteractEvent e) {
        if (GuiWindow.getWindow(e.getInventory().getTitle()) != null) {
            if (e.getInventory().getType().equals(InventoryType.CHEST)) {
                e.setResult(Event.Result.DENY);
                e.setCancelled(true);
            }
        }
    }
}
