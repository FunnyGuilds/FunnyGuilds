package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcName implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(messages.getMessage("permission"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(StringUtils.colored("&cPodaj tag gildii!"));
            return;
        } else if (args.length < 2) {
            player.sendMessage(StringUtils.colored("&cPodaj nowa nazwe!"));
            return;
        }

        String tag = args[0];
        String name = args[1];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils.colored("&cGildia o takim tagu nie istnieje!"));
            return;
        }

        //Settings settings = Settings.getInstance();
        Guild guild = GuildUtils.byTag(tag);
        //Region region = guild.getRegion();

        Manager.getInstance().stop();
        /*if(settings.flat){
			Flat.getGuildFile(guild).delete();
			Flat.getRegionFile(region).delete();
		}
		if(settings.mysql){
			new DatabaseGuild(guild).delete();
			new DatabaseRegion(region).delete();
		}*/
        guild.setName(name);
        Manager.getInstance().start();

        player.sendMessage(StringUtils.colored("&7Zmieniono nazwe gildii na &b" + guild.getName() + "&7!"));
        return;
    }

}
