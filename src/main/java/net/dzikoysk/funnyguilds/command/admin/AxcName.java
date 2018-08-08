package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcName implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(messages.adminNoNewNameGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (GuildUtils.nameExists(args[1])) {
            sender.sendMessage(messages.createNameExists);
            return;
        }

        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildRenameEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, args[1]))) {
            return;
        }
        
        Manager.getInstance().stop();
        PluginConfig.DataType dataType = Settings.getConfig().dataType;

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (dataType.flat) {
                Flat.getRegionFile(region).delete();
            }
            
            if (dataType.mysql) {
                new DatabaseRegion(region).delete();
            }
            
            region.setName(args[1]);
        }
        
        if (dataType.flat) {
            Flat.getGuildFile(guild).delete();
        }
        
        if (dataType.mysql) {
            new DatabaseGuild(guild).delete();
        }
        
        guild.setName(args[1]);
        
        Manager.getInstance().start();
        sender.sendMessage(messages.adminNameChanged.replace("{GUILD}", guild.getName()));
    }

}
