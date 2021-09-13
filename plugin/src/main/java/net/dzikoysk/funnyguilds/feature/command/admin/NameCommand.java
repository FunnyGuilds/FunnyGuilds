package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.region.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class NameCommand {

    @FunnyCommand(
        name = "${admin.name.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, PluginConfiguration config, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (args.length < 2, messages.adminNoNewNameGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (GuildUtils.nameExists(args[1]), messages.createNameExists);

        User admin = AdminUtils.getAdminUser(sender);

        String oldName = guild.getName();
        if (!SimpleEventHandler.handle(new GuildPreRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]))) {
            return;
        }

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
                FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();
                dataModel.getRegionFile(region).delete();
            }

            if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
                DatabaseRegion.delete(region);
            }
            
            region.setName(args[1]);
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) FunnyGuilds.getInstance().getDataModel();
            dataModel.getGuildFile(guild).delete();
        }

        if (FunnyGuilds.getInstance().getDataModel() instanceof SQLDataModel) {
            DatabaseGuild.delete(guild);
        }
        
        guild.setName(args[1]);
        sender.sendMessage(messages.adminNameChanged.replace("{GUILD}", guild.getName()));

        SimpleEventHandler.handle(new GuildRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]));
    }

}
