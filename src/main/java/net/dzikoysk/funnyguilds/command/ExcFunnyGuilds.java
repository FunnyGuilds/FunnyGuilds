package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Data;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseBasic;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.FunnyLogger;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcFunnyGuilds implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        String parameter = args.length > 0 ? args[0].toLowerCase() : "";

        switch (parameter) {
            case "reload":
            case "rl":
                reload(sender);
                break;
            case "check":
            case "update":
                Version.isNewAvailable(sender, true);
                break;
            case "save-all":
                saveAll(sender);
                break;
            default:
                sender.sendMessage(ChatColor.GRAY + "FunnyGuilds " + ChatColor.AQUA + FunnyGuilds.getVersion() + ChatColor.GRAY + " by " + ChatColor.AQUA + "FunnyGuilds Team");
                break;
        }

    }

    private void reload(CommandSender sender) {
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
    }

    private void saveAll(CommandSender sender) {
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
                FunnyLogger.error("An error occurred while saving data to flat file! Caused by: Exception");
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }

        if (Settings.getConfig().dataType.sql) {
            try {
                DatabaseBasic.getInstance().save(true);
            } catch (Exception e) {
                FunnyLogger.error("An error occurred while saving data to database! Caused by: Exception");
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }

        Data.getInstance().save();
        sender.sendMessage(ChatColor.GRAY + "Zapisano (" + ChatColor.AQUA + (System.currentTimeMillis() - l) / 1000.0F + "s" + ChatColor.GRAY + ")!");
    }

}
