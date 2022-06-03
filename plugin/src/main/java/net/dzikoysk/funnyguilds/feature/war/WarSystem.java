package net.dzikoysk.funnyguilds.feature.war;

import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.feature.war.WarUtils.Message;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.stream.PandaStream;

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
            user.sendMessage(WarUtils.getMessage(Message.NO_HAS_GUILD));
            return;
        }

        Guild attacker = user.getGuild().get();

        if (attacker.equals(guild)) {
            return;
        }

        if (attacker.isAlly(guild)) {
            user.sendMessage(WarUtils.getMessage(Message.ALLY));
            return;
        }

        if (!config.warEnabled) {
            user.sendMessage(WarUtils.getMessage(Message.DISABLED));
            return;
        }

        if (!guild.canBeAttacked()) {
            user.sendMessage(WarUtils.getMessage(Message.WAIT, guild.getProtection() - System.currentTimeMillis()));
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
            String messageForAttacker = WarUtils.getMessage(Message.ATTACKER, guild);
            String messageForAttacked = WarUtils.getMessage(Message.ATTACKED, attacker);

            for (User member : attacker.getMembers()) {
                member.sendMessage(messageForAttacker);
            }

            for (User member : guild.getMembers()) {
                member.sendMessage(messageForAttacked);
            }
        }
    }

    public static void conquer(Guild conqueror, Guild loser, User attacker) {
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser.updateLives(lives -> lives + 1);
            return;
        }

        String winMessage = WarUtils.getWinMessage(conqueror, loser);
        String loseMessage = WarUtils.getLoseMessage(conqueror, loser);

        PandaStream.of(conqueror.getMembers()).forEach(member -> member.sendMessage(winMessage));
        PandaStream.of(loser.getMembers()).forEach(member -> member.sendMessage(loseMessage));

        FunnyGuilds.getInstance().getGuildManager().deleteGuild(FunnyGuilds.getInstance(), loser);
        conqueror.updateLives(lives -> lives + 1);

        Bukkit.broadcastMessage(WarUtils.getBroadcastMessage(conqueror, loser));
    }
}
