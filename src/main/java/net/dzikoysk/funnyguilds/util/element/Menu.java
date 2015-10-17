package net.dzikoysk.funnyguilds.util.element;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Menu {

    private Inventory inv;
    private String name;
    private int row;
    private Map<Integer, ItemStack> item = new HashMap<>();
    private Map<Integer, String> lore = new HashMap<>();
    private Map<Integer, String> command = new HashMap<>();
    private Map<Integer, Boolean> cancelled = new HashMap<>();
    private Map<Integer, Boolean> closed = new HashMap<>();

    public Menu() {
    }

    public void setItem(int slot, ItemStack item) {
        this.item.put(slot, item);
    }

    public void setLore(int slot, String lore) {
        this.lore.put(slot, lore);
    }

    public void setCommand(int slot, String cmd) {
        this.command.put(slot, cmd);
    }

    public void setCancelled(int slot, boolean cancel) {
        this.cancelled.put(slot, cancel);
    }

    public void setClosed(int slot, boolean close) {
        this.closed.put(slot, close);
    }

    public void display(Player p) {
        updateInv();
        p.openInventory(this.inv);
    }

    public void updateInv() {
        String title = this.name;
        int slots = 9 * this.row;

        if (title == null)
            return;
        if (this.row > 6 || this.row < 1)
            return;

        Inventory inv = Bukkit.createInventory(null, slots, title);

        if (this.item != null && !this.item.isEmpty()) {
            for (Entry<Integer, ItemStack> entry : this.item.entrySet()) {
                int slot = entry.getKey() - 1;
                ItemStack item = entry.getValue();
                ItemMeta im = item.getItemMeta();
                if (slot <= slots || item != null) {
                    String l = this.lore.get(slot);
                    if (l != null) {
                        String[] table = l.split(";");
                        ArrayList<String> lore = new ArrayList<String>();
                        for (String line : table)
                            lore.add(line);
                        im.setLore(lore);
                        item.setItemMeta(im);
                    }
                    inv.setItem(slot, item);
                }
            }
        }
        this.inv = inv;
    }

    public void removeItem(int i) {
        this.item.remove(i);
    }

    public void removeLore(int i) {
        this.lore.remove(i);
    }

    public void removeCommand(int i) {
        this.command.remove(i);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public ItemStack getItem(int i) {
        if (this.item.containsKey(i))
            return this.item.get(i);
        return null;
    }

    public String getLore(int i) {
        if (this.lore.containsKey(i))
            return this.lore.get(i);
        return null;
    }

    public String getCommand(int i) {
        if (this.command.containsKey(i))
            return this.command.get(i);
        return null;
    }

    public boolean getCancelled(int i) {
        if (this.cancelled.containsKey(i))
            return this.cancelled.get(i);
        return true;
    }

    public boolean getClosed(int i) {
        if (this.closed.containsKey(i))
            return this.closed.get(i);
        return true;
    }

    public int getFirstEmpty() {
        for (int i = 1; i < this.row * 9 + 1; i++)
            if (!this.item.containsKey(i))
                return i;
        return 0;
    }
}

