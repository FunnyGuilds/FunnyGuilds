package net.dzikoysk.funnyguilds.feature.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration;
import net.dzikoysk.funnyguilds.module.api.ExtensionCommands;

public final class ExtensionCommandRegistry implements ExtensionCommands {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    @Override
    public void register(Command command) {
        if (this.commands.putIfAbsent(command.id(), command) != null) {
            throw new IllegalArgumentException("Duplicate extension command ID: " + command.id());
        }
    }

    List<Command> entries() {
        return List.copyOf(this.commands.values());
    }

    static CommandsConfiguration.FunnyCommand settings(Command command, CommandsConfiguration.ExtensionCommand settings,
                                                        CommandsConfiguration.FunnyCommand legacy) {
        String name = legacy == null ? command.name() : legacy.name;
        List<String> aliases = legacy == null ? command.aliases() : legacy.aliases;
        boolean enabled = legacy == null || legacy.enabled;
        if (settings != null) {
            name = settings.name == null ? name : settings.name;
            aliases = settings.aliases == null ? aliases : settings.aliases;
            enabled = settings.enabled == null ? enabled : settings.enabled;
        }
        return new CommandsConfiguration.FunnyCommand(name, aliases, enabled);
    }
}
