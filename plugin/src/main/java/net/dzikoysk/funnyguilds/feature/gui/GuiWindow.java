package net.dzikoysk.funnyguilds.feature.gui;

import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiWindow {

    private final Inventory inv;
    private final FunnyHolder holder;

    public GuiWindow(String name, int rows) {
        this.holder = new FunnyHolder(this);
        int size = rows > 6 ? 6 * 9 : rows * 9;
        Component title = LegacyComponentSerializer.legacySection().deserialize(name);
        this.inv = Bukkit.createInventory(this.holder, size, title);
        this.holder.setInventory(this.inv);
    }

    public void setItem(int slot, ItemStack item) {
        this.inv.setItem(slot, item);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.holder.setActionOnSlot(slot, consumer);
        this.inv.setItem(slot, item);
    }

    public void setToNextFree(ItemStack item) {
        this.setToNextFree(item, 0);
    }

    public void setToNextFree(ItemStack item, int start) {
        for (int slot = start; slot < this.inv.getSize(); slot++) {
            if (this.inv.getItem(slot) == null) {
                this.inv.setItem(slot, item);
                break;
            }
        }
    }

    public void open(HumanEntity entity) {
        entity.openInventory(this.inv);
    }

    //TODO: Use this method in the future. (Add ItemStack to configuration for fill inventory)
    public void fillEmpty(ItemStack itemStack) {
        for (int slot = 0; slot < this.inv.getSize(); slot++) {
            if (this.inv.getItem(slot) == null) {
                this.inv.setItem(slot, itemStack);
            }
        }
    }

}
