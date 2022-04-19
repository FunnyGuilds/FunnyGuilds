package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ItemStackTransformer extends BidirectionalTransformer<String, ItemStack> {

    @Override
    public GenericsPair<String, ItemStack> getPair() {
        return this.genericsPair(String.class, ItemStack.class);
    }

    @Override
    public ItemStack leftToRight(String data, SerdesContext serdesContext) {
        return ItemUtils.parseItem(data);
    }

    @Override
    public String rightToLeft(ItemStack data, SerdesContext serdesContext) {
        return ItemUtils.toString(data);
    }
}
