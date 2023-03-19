package net.dzikoysk.funnyguilds.shared.adventure;

import dev.peri.yetanothermessageslibrary.replace.Replaceable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.stream.PandaStream;

public final class ItemComponentHelper {

    private static final Class<?> NMS_ITEM_STACK = Reflections.getNMSClass("ItemStack", "world.item");
    private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY = Reflections.getMethod(Reflections.getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class);
    private static final Method NMS_ITEM_STACK_GET_TAG = Reflections.getMethod(NMS_ITEM_STACK, "getTag");

    private ItemComponentHelper() {
    }

    public static Component itemAsComponent(ItemStack item, boolean displayAmount) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        Component itemComponent = Component.empty();
        if (displayAmount) {
            itemComponent = itemComponent.append(Component.text(item.getAmount() + config.itemAmountSuffix.getValue()));
        }

        Material material = item.getType();
        if (config.useTranslatableComponentsForMaterials) {
            itemComponent = itemComponent.append(Component.translatable(plugin.getLocaleManager().queryMaterial(material)));
        } else {
            itemComponent = itemComponent.append(Component.text(MaterialUtils.getMaterialName(material)));
        }

        if (config.enableItemComponent) {
            try {
                HoverEvent.ShowItem showItem = HoverEvent.ShowItem.of(getMaterialKey(material), item.getAmount(), getBinaryTagHolder(item));
                itemComponent = itemComponent.hoverEvent(HoverEvent.showItem(showItem));
            } catch (Exception ignored) {
            }
        }
        return itemComponent;
    }

    public static Key getMaterialKey(Material material) {
        NamespacedKey namespacedKey = material.getKey();
        return Key.key(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    @Nullable
    private static Object getTagCompound(ItemStack item) {
        Object nbtTagCompound;
        try {
            Object nsmItemStack = CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);
            nbtTagCompound = NMS_ITEM_STACK_GET_TAG.invoke(nsmItemStack);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
        return nbtTagCompound;
    }

    @Nullable
    private static BinaryTagHolder getBinaryTagHolder(ItemStack item) {
        Object nbtTagCompound = getTagCompound(item);
        if (nbtTagCompound == null) {
            return null;
        }
        return BinaryTagHolder.binaryTagHolder(nbtTagCompound.toString());
    }

    public static ItemReplacement prepareItemReplacement(ItemStack item) {
        return new ItemReplacement(item);
    }

    public static ItemsReplacement prepareItemsReplacement(Collection<ItemStack> items) {
        return new ItemsReplacement(items);
    }

    private static class ItemReplacement implements Replaceable {

        private final ItemStack item;

        private final TextReplacementConfig itemReplacement;
        private final TextReplacementConfig itemNoAmountReplacement;

        private ItemReplacement(ItemStack item) {
            this.item = item;

            this.itemReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEM}")
                    .replacement(itemAsComponent(this.item, true))
                    .build();
            this.itemNoAmountReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEM-NO-AMOUNT}")
                    .replacement(itemAsComponent(this.item, false))
                    .build();
        }

        @Override
        public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
            return new FunnyFormatter()
                    .register("{ITEM}", ItemUtils.itemAsString(this.item, true))
                    .register("{ITEM-NO-AMOUNT}", ItemUtils.itemAsString(this.item, false))
                    .format(text);
        }

        @Override
        public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
            return text.replaceText(this.itemReplacement).replaceText(this.itemNoAmountReplacement);
        }

    }

    private static class ItemsReplacement implements Replaceable {

        private final Collection<ItemStack> items;

        private final TextReplacementConfig itemsReplacement;
        private final TextReplacementConfig itemsNoAmountReplacement;

        private ItemsReplacement(Collection<ItemStack> items) {
            this.items = items;

            this.itemsReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEMS}")
                    .replacement(Component.join(JoinConfiguration.commas(true), PandaStream.of(this.items)
                            .map(itemStack -> itemAsComponent(itemStack, true))
                            .toList()))
                    .build();
            this.itemsNoAmountReplacement = TextReplacementConfig.builder()
                    .matchLiteral("{ITEMS-NO-AMOUNT}")
                    .replacement(Component.join(JoinConfiguration.commas(true), PandaStream.of(this.items)
                            .map(itemStack -> itemAsComponent(itemStack, false))
                            .toList()))
                    .build();
        }

        @Override
        public @NotNull String replace(@Nullable Locale locale, @NotNull String text) {
            return new FunnyFormatter()
                    .register("{ITEMS}", FunnyStringUtils.join(PandaStream.of(this.items)
                            .map(itemStack -> ItemUtils.itemAsString(itemStack, true))
                            .toList(), true))
                    .register("{ITEMS-NO-AMOUNT}", FunnyStringUtils.join(PandaStream.of(this.items)
                            .map(itemStack -> ItemUtils.itemAsString(itemStack, false))
                            .toList(), true))
                    .format(text);
        }

        @Override
        public @NotNull Component replace(@Nullable Locale locale, @NotNull Component text) {
            return text.replaceText(this.itemsReplacement).replaceText(this.itemsNoAmountReplacement);
        }

    }

}
