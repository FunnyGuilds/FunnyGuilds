package net.dzikoysk.funnyguilds.command.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.Commands.FunnyCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExecutorCaller implements CommandExecutor, TabExecutor {

    private static final List<ExecutorCaller> ecs = new ArrayList<>();

    private Executor executor;
    private boolean enabled;
    private boolean playerOnly;
    private String overriding;
    private String permission;
    private String[] secondary;
    private List<String> aliases;
    private final List<ExecutorCaller> executors = new ArrayList<>();

    public ExecutorCaller(Executor exc, String perm, FunnyCommand command, boolean playerOnly) {
        this(exc, perm, command.name, command.aliases, command.enabled, playerOnly);
    }
    
    public ExecutorCaller(Executor exc, String command, List<String> aliases, boolean enabled, boolean playerOnly) {
        this(exc, "funnyguilds.admin", command, aliases, enabled, playerOnly);
    }
    
    public ExecutorCaller(Executor exc, String perm, String command, List<String> aliases, boolean enabled, boolean playerOnly) {
        if (exc == null || command == null) {
            return;
        }

        this.executor = exc;
        this.permission = perm;
        this.enabled = enabled;
        this.playerOnly = playerOnly;
        
        if (aliases != null && aliases.size() > 0) {
            this.aliases = aliases;
        }
        else {
            this.aliases = null;
        }

        String[] elements = command.split("\\s+");
        this.overriding = elements[0];

        if (elements.length > 1) {
            this.secondary = new String[elements.length - 1];
            System.arraycopy(elements, 1, this.secondary, 0, elements.length - 1);
        }
        else {
            this.secondary = null;
        }

        for (ExecutorCaller caller : ecs) {
            if (caller.overriding.equalsIgnoreCase(this.overriding)) {
                caller.executors.add(this);
                return;
            }
        }
        
        this.register();
        executors.add(this);
        ecs.add(this);
    }

    private boolean call(CommandSender sender, Command cmd, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(this.overriding)) {
            return false;
        }
        
        ExecutorCaller main = null;

        for (ExecutorCaller caller : this.executors) {
            if (caller.secondary != null) {
                if (caller.secondary.length > args.length) {
                    continue;
                }
                
                boolean sec = false;

                for (int index = 0; index < caller.secondary.length; index++) {
                    if (!caller.secondary[index].equalsIgnoreCase(args[index])) {
                        sec = true;
                        break;
                    }
                }
                
                if (sec) {
                    continue;
                }
                
                args = Arrays.copyOfRange(args, caller.secondary.length, args.length);
            }
            else {
                main = caller;
                continue;
            }
            
            if (sender instanceof Player) {
                if (caller.permission != null && !sender.hasPermission(caller.permission)) {
                    sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
                    return true;
                }
            }
            
            caller.executor.execute(sender, args);
            return true;
        }

        if (main == null) {
            throw new RuntimeException("Cannot find command " + cmd.getName());
        }

        if (sender instanceof Player) {
            if (main.permission != null && !sender.hasPermission(main.permission)) {
                sender.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
                return true;
            }
        }

        main.executor.execute(sender, args);
        return true;
    }

    private void register() {
        try {
            Performer performer = new Performer(this.overriding);

            if (this.aliases != null) {
                performer.setAliases(this.aliases);
            }

            performer.setPermissionMessage(FunnyGuilds.getInstance().getMessageConfiguration().permission);
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(FunnyGuilds.getInstance().getPluginConfiguration().pluginName, performer);
            performer.setExecutor(this);
        }
        catch (Exception exception) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not register command", exception);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        
        if (!this.enabled) {
            sender.sendMessage(messages.generalCommandDisabled);
            return true;
        }
        
        if (this.playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(messages.playerOnly);
            return true;
        }
        
        return call(sender, cmd, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        
        if (this.secondary != null) {
            return Arrays.asList(this.secondary);
        }
        else {
            return null;
        }
    }

}
