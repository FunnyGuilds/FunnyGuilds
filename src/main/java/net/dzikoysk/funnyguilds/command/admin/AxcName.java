package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.flat.Flat;

public class AxcName implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(messages.adminNoNewNameGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (GuildUtils.nameExists(args[1])) {
            sender.sendMessage(messages.createNameExists);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        Region region = RegionUtils.get(guild.getRegion());

        PluginConfig.DataType dataType = Settings.getConfig().dataType;
        Manager.getInstance().stop();
        
        if (dataType.flat) {
            Flat.getGuildFile(guild).delete();
            Flat.getRegionFile(region).delete();
        }
        
        if (dataType.mysql) {
            new DatabaseGuild(guild).delete();
            new DatabaseRegion(region).delete();
        }
        
        guild.setName(args[1]);
        region.setName(args[1]);
        
        Manager.getInstance().start();
        sender.sendMessage(messages.adminNameChanged.replace("{GUILD}", guild.getName()));
    }
}
