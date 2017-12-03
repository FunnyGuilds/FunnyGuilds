package net.dzikoysk.funnyguilds.command;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.LocationUtils;

public class ExcEscape implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig pc = Settings.getConfig();
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;

        if (!pc.escapeEnable || !pc.baseEnable) {
            p.sendMessage(m.escapeDisabled);
            return;
        }
        
        
        User u = User.get(p);
        if (!u.hasGuild()) {
            p.sendMessage(m.escapeNoUserGuild);
            return;
        }

        if (u.getTeleportation() != null) {
            p.sendMessage(m.escapeInProgress);
            return;
        }
        
        Location loc = p.getLocation();
        Region r = RegionUtils.getAt(loc);     
        if (r == null) {
            p.sendMessage(m.escapeNoNeedToRun);
            return;
        }
        
        Guild guild = u.getGuild();
        if (guild.equals(r.getGuild())) {
            p.sendMessage(m.escapeOnYourRegion);
            return;
        }

        int time = Settings.getConfig().escapeDelay;
        if (time < 1) {
            p.teleport(guild.getHome());
            p.sendMessage(m.escapeSuccessfulUser);
            
            for (User member : r.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(m.escapeSuccessfulOpponents.replace("{PLAYER}", p.getName()));
            }
            
            return;
        }

        p.sendMessage(m.escapeStartedUser.replace("{TIME}", Integer.toString(time)));

        String msg = m.escapeStartedOpponents.replace("{TIME}", Integer.toString(time)).replace("{PLAYER}", p.getName())
                        .replace("{X}", Integer.toString(loc.getBlockX())).replace("{Y}", Integer.toString(loc.getBlockY()))
                        .replace("{Z}", Integer.toString(loc.getBlockZ()));
        
        for (User member : r.getGuild().getOnlineMembers()) {
            member.getPlayer().sendMessage(msg);
        }
        
        Location before = p.getLocation();
        AtomicInteger i = new AtomicInteger(0);

        u.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!p.isOnline()) {
                u.getTeleportation().cancel();
                u.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(p.getLocation(), before)) {
                u.getTeleportation().cancel();
                p.sendMessage(m.escapeCancelled);
                u.setTeleportation(null);
                return;
            }

            if (i.getAndIncrement() > time) {
                u.getTeleportation().cancel();
                p.teleport(guild.getHome());
                p.sendMessage(m.escapeSuccessfulUser);
                
                for (User member : r.getGuild().getOnlineMembers()) {
                    member.getPlayer().sendMessage(m.escapeSuccessfulOpponents.replace("{PLAYER}", p.getName()));
                }
                
                u.setTeleportation(null);
            }
        }, 0L, 20L));
    }
}