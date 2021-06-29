package net.dzikoysk.funnyguilds.system.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        User user = User.get(player);

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
            WarUtils.message(player, 2, (guild.getProtection() + config.warWait) - System.currentTimeMillis());
            return;
        }
        
        guild.setProtection(System.currentTimeMillis() + config.warWait);
        
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
