package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AxcValidity implements Executor {

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
        } else if (args.length < 2) {
            player.sendMessage(StringUtils.colored("&cPodaj czas na jaki ma byc zbanowana gildia!"));
            return;
        }

        String tag = args[0];
        String ts = args[1];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils.colored("&cGildia o takim tagu nie istnieje!"));
            return;
        }

        Guild guild = GuildUtils.byTag(tag);
        if (guild.isBanned()) {
            player.sendMessage(StringUtils.colored("&cTa gildia jest juz zbanowana!"));
            return;
        }

        long time = Parser.parseTime(ts);
        if (time < 1) {
            player.sendMessage(StringUtils.colored("&cPodano nieprawidlowy czas!"));
            return;
        }

        long c = guild.getValidity();
        if (c == 0)
            c = System.currentTimeMillis();
        c += time;
        guild.setValidity(c);

        DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date v = new Date(c);

        player.sendMessage(StringUtils.colored("&7Przedluzono waznosc gildii &b" + guild.getName() + " &7do &b" + date.format(v) + "&7!"));
        return;
    }

}
