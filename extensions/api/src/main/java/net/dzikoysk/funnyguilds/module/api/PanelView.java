package net.dzikoysk.funnyguilds.module.api;

import java.util.List;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record PanelView(String title, int rows, List<String> pattern, Function<String, @Nullable Button> buttons) {

    public PanelView {
        pattern = List.copyOf(pattern);
    }

    public record Button(ItemStack icon, @Nullable Runnable action) {
    }
}
