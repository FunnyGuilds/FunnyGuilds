package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Data;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.database.DatabaseBasic;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcFunnyGuilds implements Executor {

    @Override
    public void execute(final CommandSender s, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (s instanceof Player && !s.hasPermission("funnyguilds.reload")) {
                    s.sendMessage(Messages.getInstance().getMessage("permission"));
                    return;
                }
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Manager dm = Manager.getInstance();
                        dm.stop();
                        dm.save();
                        new Messages();
                        new Settings();
                        dm.start();
                        s.sendMessage(ChatColor.AQUA + "FunnyGuilds " + ChatColor.GRAY + "przeladowano!");
                    }
                };
                s.sendMessage(ChatColor.GRAY + "Przeladowywanie...");
                thread.start();
                return;
            }
            else if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update")) {
                if (s instanceof Player) {
                    Version.check((Player) s);
                }
                else {
                    FunnyGuilds.info("Console can not use this command");
                }
                return;
            }
            else if (args[0].equalsIgnoreCase("save-all")) {
                if (s instanceof Player && !s.hasPermission("funnyguilds.admin")) {
                    s.sendMessage(Messages.getInstance().getMessage("permission"));
                    return;
                }
                s.sendMessage(ChatColor.GRAY + "Zapisywanie...");
                long l = System.currentTimeMillis();
                if (Settings.getInstance().flat) {
                    try {
                        Flat.getInstance().save(true);
                    } catch (Exception e) {
                        FunnyGuilds.error("An error occurred while saving data to flat file! Caused by: Exception");
                        if (FunnyGuilds.exception(e.getCause())) {
                            e.printStackTrace();
                        }
                    }
                }
                if (Settings.getInstance().mysql) {
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
                s.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA +
                        (System.currentTimeMillis() - l) / 1000F + "s" + ChatColor.GRAY + ")!");
                return;
            }
            if (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("zarzadzaj")) {
                return;
            }
        }
        s.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "Dzikoysk");
    }

}
