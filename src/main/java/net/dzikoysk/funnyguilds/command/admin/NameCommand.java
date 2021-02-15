package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
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

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

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
        if (!SimpleEventHandler.handle(new GuildRenameEvent(AdminUtils.getCause(admin), admin, guild, args[1]))) {
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
