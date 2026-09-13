package net.dzikoysk.funnyguilds.feature.upgrades;

import java.io.File;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

public class UpgradeItemStore {

    private final YamlWrapper wrapper;

    public UpgradeItemStore(File file) {
        this.wrapper = new YamlWrapper(file);
    }

    public Option<ItemStack> getRequiredItem(String key, int level) {
        String raw = this.wrapper.getString(path(key, level));
        if (raw == null || raw.isEmpty()) {
            return Option.none();
        }

        return Option.attempt(Exception.class, () -> ItemUtils.parseItem(raw));
    }

    public void setRequiredItem(String key, int level, ItemStack item) {
        this.wrapper.set(path(key, level), ItemUtils.toString(item));
        this.wrapper.save();
    }

    private static String path(String key, int level) {
        return key + "." + level;
    }

}
