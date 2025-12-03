package net.dzikoysk.funnyguilds.shared.adventure;

import java.util.Collection;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
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

        Component itemComponent = Component.empty();
        if (displayAmount) {
            itemComponent = itemComponent.append(Component.text(item.getAmount()).append(config.itemAmountSuffix));
        }

        Material material = item.getType();
        itemComponent = itemComponent.append(Component.translatable(item));
        itemComponent = itemComponent.style(Style.empty());

        itemComponent = itemComponent.hoverEvent(item);

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

}
