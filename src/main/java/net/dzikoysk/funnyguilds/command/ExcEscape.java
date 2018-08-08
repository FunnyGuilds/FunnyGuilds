package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserCache;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class ExcEscape implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;

        if (!config.regionsEnabled) {
            player.sendMessage(messages.regionsDisabled);
            return;
        }
        
        if (!config.escapeEnable || !config.baseEnable) {
            player.sendMessage(messages.escapeDisabled);
            return;
        }
        
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.escapeNoUserGuild);
            return;
        }

        if (user.getCache().getTeleportation() != null) {
            player.sendMessage(messages.escapeInProgress);
            return;
        }
        
        Location playerLocation = player.getLocation();
        Region region = RegionUtils.getAt(playerLocation);

        if (region == null) {
            player.sendMessage(messages.escapeNoNeedToRun);
            return;
        }
        
        Guild guild = user.getGuild();

        if (guild.equals(region.getGuild())) {
            player.sendMessage(messages.escapeOnYourRegion);
            return;
        }

        int time = Settings.getConfig().escapeDelay;

        if (time < 1) {
            player.teleport(guild.getHome());
            player.sendMessage(messages.escapeSuccessfulUser);
            
            for (User member : region.getGuild().getOnlineMembers()) {
                member.getPlayer().sendMessage(messages.escapeSuccessfulOpponents.replace("{PLAYER}", player.getName()));
            }
            
            return;
        }

        player.sendMessage(messages.escapeStartedUser.replace("{TIME}", Integer.toString(time)));

        String msg = messages.escapeStartedOpponents.replace("{TIME}", Integer.toString(time)).replace("{PLAYER}", player.getName())
                        .replace("{X}", Integer.toString(playerLocation.getBlockX())).replace("{Y}", Integer.toString(playerLocation.getBlockY()))
                        .replace("{Z}", Integer.toString(playerLocation.getBlockZ()));
        
        for (User member : region.getGuild().getOnlineMembers()) {
            member.getPlayer().sendMessage(msg);
        }
        
        Location before = player.getLocation();
        AtomicInteger timeCounter = new AtomicInteger(0);
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(messages.escapeCancelled);
                cache.setTeleportation(null);
                return;
            }

            if (timeCounter.getAndIncrement() > time) {
                cache.getTeleportation().cancel();
                player.teleport(guild.getHome());
                player.sendMessage(messages.escapeSuccessfulUser);
                
                for (User member : region.getGuild().getOnlineMembers()) {
                    member.getPlayer().sendMessage(messages.escapeSuccessfulOpponents.replace("{PLAYER}", player.getName()));
                }
                
                cache.setTeleportation(null);
            }
        }, 0L, 20L));
    }

}