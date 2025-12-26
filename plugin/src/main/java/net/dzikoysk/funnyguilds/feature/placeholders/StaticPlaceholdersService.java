package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import net.dzikoysk.funnyguilds.config.message.EntityLocaleProvider;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public abstract class StaticPlaceholdersService<T, P extends Placeholders<T, P>> implements PlaceholdersService<T> {

    private final EntityLocaleProvider entityLocaleProvider;
    
    protected final Map<String, P> placeholders = new ConcurrentHashMap<>();

    protected StaticPlaceholdersService(EntityLocaleProvider entityLocaleProvider) {
        this.entityLocaleProvider = entityLocaleProvider;
    }

    /**
     * Register placeholders set.
     *
     * @param plugin       plugin which register placeholders set
     * @param name         name of placeholders set
     * @param placeholders placeholders set
     */
    public void register(JavaPlugin plugin, String name, P placeholders) {
        this.placeholders.put(
                plugin.getName().toLowerCase(Locale.ROOT) + "_" + name.toLowerCase(Locale.ROOT),
                placeholders
        );
    }
    
    @Override
    public FunnyFormatter toFormatter(T data, String prefix, String suffix, UnaryOperator<String> nameModifier) {
        FunnyFormatter formatter = new FunnyFormatter();
        for (P placeholders : this.placeholders.values()) {
            formatter.register(placeholders, data, prefix, suffix, nameModifier);
        }
        return formatter;
    }

    @Override
    public Locale getEntityLocale(@Nullable Object entity) {
        return this.entityLocaleProvider.getEntityLocale(entity);
    }
}
