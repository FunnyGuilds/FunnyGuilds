package net.dzikoysk.funnyguilds.feature.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class FunnyHolder implements InventoryHolder {

    private final GuiWindow guiWindow;
    private final Map<Integer, Consumer<InventoryClickEvent>> actions;
    private Inventory inventory;

    public FunnyHolder(GuiWindow guiWindow) {
        this.guiWindow = guiWindow;
        this.actions = new HashMap<>();
    }

    public void handleClick(InventoryClickEvent event) {
        this.actions.getOrDefault(event.getRawSlot(), e -> e.setCancelled(true)).accept(event);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public GuiWindow getGuiWindow() {
        return this.guiWindow;
    }

    public void setActionOnSlot(Integer slot, Consumer<InventoryClickEvent> consumer) {
        this.actions.put(slot, consumer != null ? consumer : event -> {});
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
