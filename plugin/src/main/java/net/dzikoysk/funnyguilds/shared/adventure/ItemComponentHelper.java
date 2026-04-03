package net.dzikoysk.funnyguilds.shared.adventure;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.util.Collection;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public final class ItemComponentHelper {

    private ItemComponentHelper() {
    }

    public static Component itemAsComponent(
            ItemStack item,
            boolean displayAmount
    ) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        if (item == null) {
            return Component.translatable(Material.AIR).style(Style.empty());
        }

        Material material = item.getType();
        if (material.isAir() || item.getAmount() <= 0) {
            return Component.translatable(material).style(Style.empty());
        }

        Component itemComponent = Component.empty();
        if (displayAmount) {
            itemComponent = itemComponent.append(Component.text(item.getAmount()).append(config.itemAmountSuffix));
        }

        itemComponent = itemComponent.append(Component.translatable(item));
        itemComponent = itemComponent.style(Style.empty());

        ItemStack hoverItem = item.clone();
        hoverItem.setAmount(Math.max(1, Math.min(99, hoverItem.getAmount())));
        itemComponent = itemComponent.hoverEvent(hoverItem);

        return itemComponent;
    }

    public static Component materialAsComponent(Material material) {
        Component itemComponent = Component.translatable(material);
        itemComponent = itemComponent.style(Style.empty());

        ItemStack itemStack = new ItemStack(
                material,
                1
        );
        itemComponent = itemComponent.hoverEvent(itemStack);

        return itemComponent;
    }

    public static Component materialsAsComponent(Collection<Material> materials) {
        return ComponentUtil.join(materials, ", ", ItemComponentHelper::materialAsComponent);
    }
    
    public static Component blockAsComponent(Block block) {
        Component itemComponent = Component.translatable(block);
        itemComponent = itemComponent.style(Style.empty());
        
        ItemStack itemStack = new ItemStack(
                block.getType(),
                1
        );
        itemComponent = itemComponent.hoverEvent(itemStack);
        
        return itemComponent;
    }
    
    public static Component blocksAsComponent(Collection<Block> blocks) {
        return ComponentUtil.join(blocks, ", ", ItemComponentHelper::blockAsComponent);
    }

    public static Replaceable prepareItemReplacement(ItemStack item) {
        return new FunnyFormatter()
                .register("{ITEM}", itemAsComponent(item, true))
                .register("{ITEM-NO-AMOUNT}", itemAsComponent(item, false));
    }

    public static Replaceable prepareItemsReplacement(Collection<ItemStack> items) {
        return new FunnyFormatter()
                .register("{ITEMS}", ComponentUtil.join(items, ", ", itemStack -> itemAsComponent(itemStack, true)))
                .register("{ITEMS-NO-AMOUNT}", ComponentUtil.join(items, ", ", itemStack -> itemAsComponent(itemStack, false)));
    }
}
