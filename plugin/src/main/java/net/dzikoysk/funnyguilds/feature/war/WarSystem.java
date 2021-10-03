package net.dzikoysk.funnyguilds.feature.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.time.Duration;
import java.time.Instant;

public class WarSystem {

    private static WarSystem instance;

    public WarSystem() {
        instance = this;
    }

    public static WarSystem getInstance() {
        if (instance == null) {
            new WarSystem();
        }

        return instance;
    }

    public void attack(Player player, Guild guild) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        UserManager userManager = plugin.getUserManager();
        PluginConfiguration config = plugin.getPluginConfiguration();
        Option<User> userOp = userManager.findByPlayer(player);

        if (userOp.isEmpty()) {
            return;
        }

        User user = userOp.get();

        if (!user.hasGuild()) {
            WarUtils.message(player, 0);
            return;
        }
        
        Guild attacker = user.getGuild();

        if (attacker.equals(guild)) {
            return;
        }
        
        if (attacker.getAllies().contains(guild)) {
            WarUtils.message(player, 1);
            return;
        }

        if (!config.warEnabled){
            WarUtils.message(player, 5);
            return;
        }
        
        if (!guild.canBeAttacked()) {
            WarUtils.message(player, 2, Duration.between(Instant.now(), Instant.ofEpochMilli(guild.getProtection())).toMillis());
            return;
        }

        guild.setProtection(Instant.now().plus(config.warWait).toEpochMilli());
        
        if (SimpleEventHandler.handle(new GuildLivesChangeEvent(EventCause.SYSTEM, user, guild, guild.getLives() - 1))) {
            guild.removeLive();
        }
        
        if (guild.getLives() < 1) {
            conquer(attacker, guild, user);
        }
        else {
            for (User member : attacker.getMembers()) {
                Player memberPlayer = member.getPlayer();

                if (memberPlayer != null) {
                    WarUtils.message(memberPlayer, 3, guild);
                }
            }
            
            for (User member : guild.getMembers()) {
                Player memberPlayer = member.getPlayer();

                if (memberPlayer != null) {
                    WarUtils.message(memberPlayer, 4, attacker);
                }
            }
        }
    }

    public void conquer(Guild conqueror, Guild loser, User attacker) {
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser.addLive();
            return;
        }
        
        String message = WarUtils.getWinMessage(conqueror, loser);

        for (User user : conqueror.getMembers()) {
            Player player = user.getPlayer();

            if (player != null) {
                player.sendMessage(message);
            }
        }
        
        message = WarUtils.getLoseMessage(conqueror, loser);

        for (User user : loser.getMembers()) {
            Player player = user.getPlayer();

            if (player != null) {
                player.sendMessage(message);
            }
        }
        
        GuildUtils.deleteGuild(loser);
        conqueror.addLive();

        message = WarUtils.getBroadcastMessage(conqueror, loser);
        Bukkit.broadcastMessage(message);
    }
}
