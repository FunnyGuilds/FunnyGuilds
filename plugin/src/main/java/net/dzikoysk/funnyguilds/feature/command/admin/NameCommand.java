package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class NameCommand extends AbstractFunnyCommand {

    @Inject public DataModel dataModel;

    @FunnyCommand(
        name = "${admin.name.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfig.generalNoTagGiven);
        when (args.length < 2, this.messageConfig.adminNoNewNameGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (GuildUtils.nameExists(args[1]), this.messageConfig.createNameExists);

        User admin = AdminUtils.getAdminUser(sender);

        String oldName = guild.getName();
        if (!SimpleEventHandler.handle(new GuildPreRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]))) {
            return;
        }

        if (this.pluginConfig.regionsEnabled) {
            Region region = guild.getRegion();

            if (this.dataModel instanceof FlatDataModel) {
                FlatDataModel dataModel = (FlatDataModel) this.dataModel;
                dataModel.getRegionFile(region).delete();
            }

            if (this.dataModel instanceof SQLDataModel) {
                DatabaseRegion.delete(region);
            }
            
            region.setName(args[1]);
        }

        if (this.dataModel instanceof FlatDataModel) {
            FlatDataModel dataModel = (FlatDataModel) this.dataModel;
            dataModel.getGuildFile(guild).delete();
        }

        if (this.dataModel instanceof SQLDataModel) {
            DatabaseGuild.delete(guild);
        }
        
        guild.setName(args[1]);
        sender.sendMessage(this.messageConfig.adminNameChanged.replace("{GUILD}", guild.getName()));

        SimpleEventHandler.handle(new GuildRenameEvent(AdminUtils.getCause(admin), admin, guild, oldName, args[1]));
    }

}
