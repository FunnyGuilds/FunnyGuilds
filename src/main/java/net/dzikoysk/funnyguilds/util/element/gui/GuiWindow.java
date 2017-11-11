package net.dzikoysk.funnyguilds.util.element.gui;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiWindow {
    private static final Map<String, GuiWindow> windows = new HashMap<>();

    private final Inventory inv;
    private final Map<Integer, GuiItem> items;
    private Consumer<InventoryOpenEvent> openHandler = null;
    private Consumer<InventoryCloseEvent> closeHandler = null;

    public GuiWindow(String name, int rows) {
        name = getValidName(name);

        this.inv = Bukkit.createInventory(null, rows > 6 ? 6 * 9 : rows * 9, name);
        this.items = new HashMap<>(rows > 6 ? 6 * 9 : rows * 9);

        windows.put(name, this);
    }

    public GuiWindow(String name, List<ItemStack> items) {
        name = this.getValidName(name);

        this.inv = Bukkit.createInventory(null, this.roundUp(items.size()), name);
        this.items = new HashMap<>(this.roundUp(items.size()));

        windows.put(name, this);
    }

    static GuiWindow getWindow(String inv) {
        return windows.get(inv);
    }

    public void setItem(int slot, GuiItem item) {
        this.items.put(slot, item);
        this.inv.setItem(slot, item.wrap());
    }

    public void setItem(int x, int y, GuiItem item) {
        setItem(x + y * 9, item);
    }

    public void setToNextFree(GuiItem item) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                this.items.put(i, item);
                this.inv.setItem(i, item.wrap());
                break;
            }
        }
    }

    public void setToNextFree(final GuiItem item, int start) {
        for (int i = start; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                this.items.put(i, item);
                this.inv.setItem(i, item.wrap());
                break;
            }
        }
    }

    public GuiItem getItem(int slot) {
        return this.items.get(slot);
    }

    public GuiItem getItem(int x, int y) {
        return getItem(x * 9 + y);
    }

    public void setOpenEvent(final Consumer<InventoryOpenEvent> e) {
        this.openHandler = e;
    }

    public void setCloseEvent(final Consumer<InventoryCloseEvent> e) {
        this.closeHandler = e;
    }

    public void handleOpen(final InventoryOpenEvent e) {
        if (this.openHandler != null) {
            this.openHandler.accept(e);
        }
    }

    public void handleClose(final InventoryCloseEvent e) {
        if (this.closeHandler != null) {
            this.closeHandler.accept(e);
        }
    }

    public Inventory wrap() {
        return this.inv;
    }

    public void open(HumanEntity entity) {
        Inventory inv = Bukkit.createInventory(entity, this.wrap().getSize(), this.wrap().getTitle());
        inv.setContents(this.wrap().getContents());
        entity.openInventory(inv);
    }

    public void unregister() {
        windows.remove(this.wrap().getTitle());
        this.items.clear();
    }

    private String getValidName(String name) {
        if (windows.containsKey(name)) {
            return getValidName(name + ChatColor.RESET);
        } else {
            return name;
        }
    }

    public void fillEmpty(final ItemStack itemStack) {
        Validate.notNull(itemStack, "ItemStack cannot be NULL!");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, itemStack);
            }
        }
    }

    public void fillEmpty(final Material material) {
        Validate.notNull(material, "Material cannot be NULL!");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemStack(material, 1, (short) 15));
            }
        }
    }

    private int roundUp(int size) {
        return (size + 8) / 9 * 9 > 54 ? 54 : (size + 8) / 9 * 9;
    }

}
