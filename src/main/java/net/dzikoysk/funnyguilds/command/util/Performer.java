package net.dzikoysk.funnyguilds.command.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Performer extends Command {

    private ExecutorCaller caller;

    protected Performer(String command) {
        super(command);
    }

    public void setExecutor(ExecutorCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.caller == null)
            return false;
        return this.caller.onCommand(sender, this, commandLabel, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (this.caller == null)
            return null;
        return caller.onTabComplete(sender, this, alias, args);
    }
}
