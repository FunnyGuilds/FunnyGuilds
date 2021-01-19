package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NameCommand {

    @FunnyCommand(
        name = "${admin.name.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }
        else if (args.length < 2) {
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

        User admin = (sender instanceof Player)
                ? User.get(sender.getName())
                : null;

        if (!SimpleEventHandler.handle(new GuildRenameEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, args[1]))) {
            return;
        }

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
                FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();
                dataModel.getRegionFile(region).delete();
            }

            if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
                new DatabaseRegion(region).delete();
            }
            
            region.setName(args[1]);
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();
            dataModel.getGuildFile(guild).delete();
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
            new DatabaseGuild(guild).delete();
        }
        
        guild.setName(args[1]);
        sender.sendMessage(messages.adminNameChanged.replace("{GUILD}", guild.getName()));
    }

}
