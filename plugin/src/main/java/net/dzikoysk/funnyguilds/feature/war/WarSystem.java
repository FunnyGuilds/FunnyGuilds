package net.dzikoysk.funnyguilds.feature.war;

import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;

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

        Guild attacker = user.getGuildOption().get();

        if (attacker.equals(guild)) {
            return;
        }

        if (attacker.getAllies().contains(guild)) {
            WarUtils.message(player, 1);
            return;
        }

        if (!config.warEnabled) {
            WarUtils.message(player, 5);
            return;
        }

        if (!guild.canBeAttacked()) {
            WarUtils.message(player, 2, guild.getProtection() - System.currentTimeMillis());
            return;
        }

        guild.setProtection(Instant.now().plus(config.warWait).toEpochMilli());

        if (SimpleEventHandler.handle(new GuildLivesChangeEvent(EventCause.SYSTEM, user, guild, guild.getLives() - 1))) {
            guild.updateLives(lives -> lives - 1);
        }

        if (guild.getLives() < 1) {
            conquer(attacker, guild, user);
        }
        else {
            for (User member : attacker.getMembers()) {
                member.getPlayer().peek(memberPlayer -> WarUtils.message(memberPlayer, 3, guild));
            }

            for (User member : guild.getMembers()) {
                member.getPlayer().peek(memberPlayer -> WarUtils.message(memberPlayer, 4, attacker));
            }
        }
    }

    public void conquer(Guild conqueror, Guild loser, User attacker) {
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser.updateLives(lives -> lives + 1);
            return;
        }

        String message = WarUtils.getWinMessage(conqueror, loser);

        for (User user : conqueror.getMembers()) {
            user.sendMessage(message);
        }

        message = WarUtils.getLoseMessage(conqueror, loser);

        for (User user : loser.getMembers()) {
            user.sendMessage(message);
        }

        FunnyGuilds.getInstance().getGuildManager().deleteGuild(FunnyGuilds.getInstance(), loser);
        conqueror.updateLives(lives -> lives + 1);

        message = WarUtils.getBroadcastMessage(conqueror, loser);
        Bukkit.broadcastMessage(message);
    }
}
