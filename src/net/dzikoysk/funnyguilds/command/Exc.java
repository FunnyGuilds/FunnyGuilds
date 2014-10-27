package net.dzikoysk.funnyguilds.command;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class Exc implements CommandExecutor, TabExecutor {
    
    protected final String command;
    protected final List<String> alias;
    protected final String perm;
    
    public Exc(String command, String perm) {
        this.command = command.toLowerCase();
        this.perm = perm;
        this.alias = null;
    }
    
    public Exc(String command, String perm, String... aliases) {
        this.command = command.toLowerCase();
        this.perm = perm;
        this.alias = new ArrayList<String>(Arrays.asList(aliases));
    }
    
    public void register() {
    	try {
	    	Perform p = new Perform(this.command);
	        if (this.alias != null) p.setAliases(this.alias);
	        p.setPermissionMessage(Messages.getInstance().getMessage("permission"));
	        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
	        f.setAccessible(true);
	        CommandMap cmap = (CommandMap) f.get(Bukkit.getServer());
	        if(cmap.getCommand(this.command) == null) cmap.register("", p);
	        p.setExecutor(this);
    	} catch (Exception e) { 
    		if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
        }
    }
    
    private final class Perform extends Command {
        private Exc exe;
        
        protected Perform(String command) { 
        	super(command);
        }
        
        public void setExecutor(Exc exe) { 
        	this.exe = exe;
        }
        
        @Override 
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        	if (this.exe == null) return false;
    		if(sender instanceof Player){
				if(this.exe.perm != null && !sender.hasPermission(this.exe.perm)){
					sender.sendMessage(Messages.getInstance().getMessage("permission"));
					return true;
				}
			}
    		return this.exe.onCommand(sender, this, commandLabel, args);
        }
        
        @Override  
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        	if(this.exe == null) return null;
        	return exe.onTabComplete(sender, this, alias, args);
        }
    }
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}