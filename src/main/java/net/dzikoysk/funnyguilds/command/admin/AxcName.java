package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcName implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages m = Messages.getInstance();
        Player player = (Player) sender;

        if (!player.hasPermission("funnyguilds.admin")) {
            player.sendMessage(m.getMessage("permission"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(StringUtils.colored("&cPodaj tag gildii!"));
            return;
        }
        else if (args.length < 2) {
            player.sendMessage(StringUtils.colored("&cPodaj nowa nazwe!"));
            return;
        }

        String tag = args[0];
        String name = args[1];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils.colored("&cGildia o takim tagu nie istnieje!"));
            return;
        }

        Guild guild = GuildUtils.byTag(tag);
        Region region = RegionUtils.get(guild.getRegion());

        final PluginConfig.DataType s = Settings.getConfig().dataType;

        Manager.getInstance().stop();
        if (s.flat) {
            Flat.getGuildFile(guild).delete();
            Flat.getRegionFile(region).delete();
        }
        if (s.mysql) {
            new DatabaseGuild(guild).delete();
            new DatabaseRegion(region).delete();
        }
        guild.getRegions().remove(region.getName());
        guild.setName(name);
        region.setName(name);
        guild.setRegion(name);
        guild.getRegions().add(name);
        Manager.getInstance().start();

        player.sendMessage(StringUtils.colored("&7Zmieniono nazwe gildii na &b" + guild.getName() + "&7!"));
        return;
    }

}
