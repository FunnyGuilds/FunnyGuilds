package net.dzikoysk.funnyguilds.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Data;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseBasic;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;

public class ExcFunnyGuilds implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (!sender.hasPermission("funnyguilds.reload")) {
                    sender.sendMessage(Messages.getInstance().permission);
                    return;
                }

                Thread thread = new Thread(() -> {
                    Manager dm = Manager.getInstance();
                    dm.stop();
                    dm.save();
                    new Messages();
                    new Settings();
                    dm.start();

                    if (Settings.getConfig().playerlistEnable) {
                        PluginConfig config = Settings.getConfig();
                        AbstractTablist.wipeCache();

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
                        }
                    }
                    
                    sender.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano!");
                });

                sender.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
                thread.start();
                
                return;
            } else if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update")) {
                if (sender instanceof Player) {
                    Version.isNewAvailable((Player) sender);
                } else {
                    FunnyGuilds.info(Messages.getInstance().playerOnly);
                }
                
                return;
            } else if (args[0].equalsIgnoreCase("save-all")) {
                if (!sender.hasPermission("funnyguilds.admin")) {
                    sender.sendMessage(Messages.getInstance().permission);
                    return;
                }
                
                sender.sendMessage(ChatColor.GRAY + "Zapisywanie...");
                long l = System.currentTimeMillis();
                
                if (Settings.getConfig().dataType.flat) {
                    try {
                        Flat.getInstance().save(true);
                    } catch (Exception e) {
                        FunnyGuilds.error("An error occurred while saving data to flat file! Caused by: Exception");
                        if (FunnyGuilds.exception(e.getCause())) {
                            e.printStackTrace();
                        }
                    }
                }
                
                if (Settings.getConfig().dataType.mysql) {
                    try {
                        DatabaseBasic.getInstance().save(true);
                    } catch (Exception e) {
                        FunnyGuilds.error("An error occurred while saving data to database! Caused by: Exception");
                        if (FunnyGuilds.exception(e.getCause())) {
                            e.printStackTrace();
                        }
                    }
                }
                
                Data.getInstance().save();
                sender.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA + (System.currentTimeMillis() - l) / 1000.0F + "s" + ChatColor.GRAY + ")!");
                return;
            }
        }
        
        sender.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "Dzikoysk");
    }
}
