package net.dzikoysk.funnyguilds.command.util;

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

public class ExecutorCaller implements CommandExecutor, TabExecutor {
    
	private final List<ExecutorCaller> ecs = new ArrayList<>();

	private final String overriding;
	private final Executor executor;
    private final String permission;
    private final List<String> aliases;
    private final String[] secondary;
    private final List<ExecutorCaller> executors = new ArrayList<>();
    
    public ExecutorCaller(Executor exc, String command, String perm, String... aliases) {
        this.executor = exc;
        this.permission = perm;
        if(aliases != null && aliases.length > 0) this.aliases = new ArrayList<String>(Arrays.asList(aliases));
        else this.aliases = null;
        
        String[] splited = command.split("\\s+");
        this.overriding = splited[0];
        if(splited.length > 1){
        	this.secondary = new String[splited.length - 1];
        	for(int i = 1; i < splited.length; i++) this.secondary[i-1] = splited[i];
        } else this.secondary = null;
        
        boolean register = false;
        for(ExecutorCaller ec : ecs){
        	if(ec.overriding.equalsIgnoreCase(this.overriding)){
        		ec.executors.add(this);
        		register = true;
        	}
        }
        if(register) return;
    	this.register();
    	executors.add(this);
    	ecs.add(this);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(!cmd.getName().equalsIgnoreCase(this.overriding)) return false;
    	System.out.println("1");
    	for(ExecutorCaller ec : this.executors){
    		System.out.println("2");
	    	if(ec.secondary != null){
	    		System.out.println("3");
	    		if(ec.secondary.length > args.length) continue;
	    		System.out.println("4");
	    		for(int i = 0; i < ec.secondary.length; i++){
	    			System.out.println("5");
	    			if(!ec.secondary[i].equalsIgnoreCase(args[i])) continue;
	    		}
	    		System.out.println("6");
    			args = Arrays.copyOfRange(args, ec.secondary.length, args.length);
	    	}
	    	System.out.println("7");
    		if(sender instanceof Player){
    			if(ec.permission != null && !sender.hasPermission(ec.permission)){
    				sender.sendMessage(Messages.getInstance().getMessage("permission"));
    				return true;
    			}
    		}
        	ec.executor.execute(sender, args);
        	return true;
		}
    	return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
    
    public void register() {
    	try {
	    	Performer p = new Performer(this.overriding);
	        if (this.aliases != null) p.setAliases(this.aliases);
	        p.setPermissionMessage(Messages.getInstance().getMessage("permission"));
	        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
	        f.setAccessible(true);
	        CommandMap cmap = (CommandMap) f.get(Bukkit.getServer());
	        if(cmap.getCommand(this.overriding) == null) cmap.register("", p);
	        p.setExecutor(this);
    	} catch (Exception e) { 
    		if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
        }
    }
}
