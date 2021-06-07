package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class NameCommand {

    @FunnyCommand(
        name = "${admin.name.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, PluginConfiguration config, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (args.length < 2, messages.adminNoNewNameGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (GuildUtils.nameExists(args[1]), messages.createNameExists);

        User admin = AdminUtils.getAdminUser(sender);

        String oldName = guild.getName();
        if (!SimpleEventHandler.handle(new GuildPreRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]))) {
            return;
        }

        DataModel dataModel = plugin.getDataModel();

        if (config.regionsEnabled) {
            Region region = guild.getRegion();

            if (dataModel instanceof FlatDataModel) {
                FlatDataModel flat = (FlatDataModel) dataModel;
                flat.getRegionFile(region).delete();
            }

            if (dataModel instanceof SQLDataModel) {
                SQLDataModel sqlDataModel = (SQLDataModel) dataModel;
                sqlDataModel.getDatabaseRegion().delete(region);
            }
            
            region.setName(args[1]);
        }

        if (dataModel instanceof FlatDataModel) {
            FlatDataModel flat = (FlatDataModel) dataModel;
            flat.getGuildFile(guild).delete();
        }

        if (dataModel instanceof SQLDataModel) {
            SQLDataModel sqlDataModel = (SQLDataModel) dataModel;
            sqlDataModel.getDatabaseGuild().delete(guild);
        }
        
        guild.setName(args[1]);
        sender.sendMessage(messages.adminNameChanged.replace("{GUILD}", guild.getName()));

        SimpleEventHandler.handle(new GuildRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]));
    }

}
