package net.dzikoysk.funnyguilds.shared.adventure;

import com.google.common.collect.ImmutableMap;
import dev.peri.yetanothermessageslibrary.adventure.GlobalAdventureSerializer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ComponentUtil {
    
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .build();

    public static Component toComponent(@Nullable Object value) {
        if (value instanceof ComponentLike componentLike) {
            return componentLike.asComponent();
        }
        return Component.text(Objects.toString(value));
    }
    
    public static Component toComponent(@Nullable Object value, Component defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof ComponentLike componentLike) {
            return componentLike.asComponent();
        }
        return Component.text(Objects.toString(value));
    }
    
    public static Component colored(String text) {
        return GlobalAdventureSerializer.deserialize(text);
    }
    
    public static List<Component> coloredByNewLine(String text) {
        return Arrays.stream(text.split("\n"))
                .map(ComponentUtil::colored)
                .toList();
    }
    
    public static List<Component> colored(String... texts) {
        return Arrays.stream(texts)
                .map(ComponentUtil::colored)
                .toList();
    }
    
    public static List<Component> colored(List<String> texts) {
        return texts.stream()
                .map(ComponentUtil::colored)
                .toList();
    }
    
    public static <K> Map<K, Component> colored(Map<K, String> texts) {
        SequencedMap<K, Component> map = new LinkedHashMap<>(texts.size());
        for (Map.Entry<K, String> entry : texts.entrySet()) {
            map.put(entry.getKey(), colored(entry.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }
    
    public static <K> MapBuilder<K> coloredMapBuilder() {
        return new MapBuilder<>();
    }
    
    public static class MapBuilder<K> {
        
        private final ImmutableMap.Builder<K, Component> builder = ImmutableMap.builder();
        
        public MapBuilder<K> put(K key, String value) {
            this.builder.put(key, colored(value));
            return this;
        }
        
        public Map<K, Component> build() {
            return this.builder.build();
        }
        
    }

    /**
     * Converts component to legacy format using section sign (§) as color code character.
     *
     * @param component the component to convert
     *
     * @return the legacy formatted string
     */
    public static String toLegacy(Component component) {
        return LEGACY_SECTION.serialize(component);
    }
    
    //TODO: some better naming for this method
    public static String decolord(Component component) {
        return MINI_MESSAGE.serialize(component);
    }
    
    public static boolean isEmpty(Component component) {
        return component == null || component.equals(Component.empty());
    }
    
    public static Component join(Collection<? extends Component> components, String separator) {
        Validate.notEmpty(components, "Components cannot be empty");
        Validate.notNull(separator, "Separator cannot be null");
        
        Component componentSeparator = colored(separator);

        TextComponent.Builder builder = Component.text();
        Iterator<? extends Component> iterator = components.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(componentSeparator);
            }
        }
        return builder.build();
    }
    
    public static <T> Component join(Collection<T> elements, String separator, Function<T, ? extends ComponentLike> componentProvider) {
        Validate.notEmpty(elements, "Elements cannot be empty");
        Validate.notNull(separator, "Separator cannot be null");
        Validate.notNull(componentProvider, "Component provider cannot be null");
        
        Component componentSeparator = colored(separator);

        TextComponent.Builder builder = Component.text();
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Component component = componentProvider.apply(iterator.next()).asComponent();
            builder.append(component);
            if (iterator.hasNext()) {
                builder.append(componentSeparator);
            }
        }
        return builder.build();
    }
    
    public static Component joinColored(Collection<String> texts, String separator) {
        return join(texts, separator, ComponentUtil::colored);
    }
    
    public static Component joinOrDefault(Collection<? extends Component> components, String separator, Component defaultComponent) {
        return components.isEmpty() ? defaultComponent : join(components, separator);
    }
    
    public static <T> Component joinOrDefault(Collection<T> elements, String separator, Function<T, ? extends ComponentLike> componentProvider, Component defaultComponent) {
        return elements.isEmpty() ? defaultComponent : join(elements, separator, componentProvider);
    }
    
    public static Component joinColoredOrDefault(Collection<String> texts, String separator, Component defaultComponent) {
        return texts.isEmpty()
                ? defaultComponent
                : joinColored(
                        texts,
                        separator
                );
    }
    
    public static Component joinColoredOrDefault(Collection<String> texts, Component defaultComponent) {
        return joinColoredOrDefault(texts, ", ", defaultComponent);
    }
    
    public static Component repeat(Component component, int count) {
        Validate.notNull(component, "Component cannot be null");
        Validate.isTrue(count >= 0, "Count cannot be negative");
        
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < count; i++) {
            builder.append(component);
        }
        return builder.build();
    }
}
