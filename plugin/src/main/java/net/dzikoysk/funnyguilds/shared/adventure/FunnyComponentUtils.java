package net.dzikoysk.funnyguilds.shared.adventure;

import java.util.Collection;
import java.util.Iterator;
import net.kyori.adventure.text.Component;

public final class FunnyComponentUtils {

    private static final Component SEPARATOR = Component.text(",");
    private static final Component SPACE_SEPARATOR = Component.text(", ");

    private FunnyComponentUtils() {
    }

    public static Component join(Collection<Component> components, boolean insertSpaces) {
        return join(components, insertSpaces ? SPACE_SEPARATOR : SEPARATOR);
    }

    public static Component join(Collection<Component> components) {
        return join(components, false);
    }

    public static Component join(Collection<Component> components, Component separator) {
        Component finalComponent = Component.empty();

        Iterator<Component> iterator = components.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            finalComponent = finalComponent.append(component);

            if (iterator.hasNext()) {
                finalComponent = finalComponent.append(separator);
            }
        }

        return finalComponent;
    }

}
