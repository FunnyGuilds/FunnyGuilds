package net.dzikoysk.funnyguilds.module.api;

import java.util.List;
import java.util.Objects;

/** Commands contributed during startup to the host's command framework. */
@FunctionalInterface
public interface ExtensionCommands {

    void register(Command command);

    /** An extension-owned instance with FunnyCommands annotations on its command method. */
    record Command(String id, String name, List<String> aliases, Object component) {

        public Command {
            if (!id.matches("[a-z0-9-]+\\.[a-z0-9-]+")) {
                throw new IllegalArgumentException("Use extension-name.command-name as the command ID: " + id);
            }
            if (name.isBlank()) {
                throw new IllegalArgumentException("Missing default command name for " + id);
            }
            aliases = List.copyOf(aliases);
            Objects.requireNonNull(component, "component");
        }
    }
}
