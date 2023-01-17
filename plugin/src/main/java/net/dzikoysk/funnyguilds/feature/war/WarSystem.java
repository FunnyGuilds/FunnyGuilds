package net.dzikoysk.funnyguilds.feature.war;

import java.time.Duration;
import java.time.Instant;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
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
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageService messageService = plugin.getMessageService();
        Option<User> userOp = userManager.findByPlayer(player);

        if (!pluginConfiguration.warEnabled) {
            messageService.getMessage(config -> config.warDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        if (userOp.isEmpty()) {
            return;
        }
        User user = userOp.get();

        if (!user.hasGuild()) {
            messageService.getMessage(config -> config.warHasNotGuild)
                    .receiver(player)
                    .send();
            return;
        }

        Guild attacker = user.getGuild().get();
        if (attacker.equals(guild)) {
            return;
        }

        if (attacker.isAlly(guild)) {
            messageService.getMessage(config -> config.warAlly)
                    .receiver(player)
                    .send();
            return;
        }

        if (!guild.canBeAttacked()) {
            messageService.getMessage(config -> config.warWait)
                    .with("{TIME}", TimeUtils.formatTime(Duration.between(guild.getProtection(), Instant.now())))
                    .receiver(player)
                    .send();
            return;
        }

        guild.setProtection(Instant.now().plus(pluginConfiguration.warWait));

        if (SimpleEventHandler.handle(new GuildLivesChangeEvent(EventCause.SYSTEM, user, guild, guild.getLives() - 1))) {
            guild.updateLives(lives -> lives - 1);
        }

        if (guild.getLives() < 1) {
            this.conquer(attacker, guild, user);
        } else {
            messageService.getMessage(config -> config.warAttacker)
                    .with("{GUILD}", guild.getName())
                    .receiver(attacker)
                    .send();
            messageService.getMessage(config -> config.warAttacked)
                    .with("{GUILD}", attacker.getName())
                    .receiver(guild)
                    .send();
        }
    }

    public void conquer(Guild conqueror, Guild loser, User attacker) {
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser.updateLives(lives -> lives + 1);
            return;
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageService messageService = plugin.getMessageService();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{WINNER}", conqueror.getTag())
                .register("{LOSER}", loser.getTag());

        messageService.getMessage(config -> config.warWin)
                .with(formatter)
                .receiver(conqueror)
                .send();
        messageService.getMessage(config -> config.warLose)
                .with(formatter)
                .receiver(loser)
                .send();

        plugin.getGuildManager().deleteGuild(plugin, loser);
        conqueror.updateLives(lives -> lives + 1);

        messageService.getMessage(config -> config.broadcastWar)
                .with(formatter)
                .broadcast()
                .send();
    }

}
