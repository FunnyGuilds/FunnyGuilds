package net.dzikoysk.funnyguilds.feature.war;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.WarConfiguration;
import net.dzikoysk.funnyguilds.config.sections.WarConfiguration.AntiFarmingConfiguration;
import net.dzikoysk.funnyguilds.config.sections.WarConfiguration.WarMode;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartLivesChangeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLifeGainEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildLifeGainEvent.GainReason;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildWarWinEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
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
        WarConfiguration warConfig = pluginConfiguration.war;
        MessageService messageService = plugin.getMessageService();
        Option<User> userOp = userManager.findByPlayer(player);

        if (!warConfig.enabled) {
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

        if (warConfig.requireDeclaration && !attacker.isAtWar(guild)) {
            messageService.getMessage(config -> config.warNotEnemy)
                    .receiver(player)
                    .send();
            return;
        }

        if (!guild.canBeAttacked()) {
            messageService.getMessage(config -> config.warWait)
                    .receiver(player)
                    .with("{TIME}", TimeUtils.formatTime(Duration.between(Instant.now(), guild.getProtection())))
                    .send();
            return;
        }

        int newHeartLives = guild.getHeartLives() - 1;

        if (newHeartLives > 0) {
            if (!SimpleEventHandler.handle(new GuildHeartLivesChangeEvent(EventCause.SYSTEM, user, guild, newHeartLives))) {
                return;
            }

            guild.setHeartLives(newHeartLives);

            if (warConfig.heartLives > 1) {
                messageService.getMessage(config -> config.warAttackerHeart)
                        .receiver(attacker)
                        .with("{ATTACKED}", guild.getName())
                        .with("{HEART-LIVES}", newHeartLives)
                        .with("{HEART-LIVES-MAX}", warConfig.heartLives)
                        .send();
                messageService.getMessage(config -> config.warAttackedHeart)
                        .receiver(guild)
                        .with("{ATTACKER}", attacker.getName())
                        .with("{HEART-LIVES}", newHeartLives)
                        .with("{HEART-LIVES-MAX}", warConfig.heartLives)
                        .send();
            }
            return;
        }

        guild.setProtection(Instant.now().plus(warConfig.wait));

        if (SimpleEventHandler.handle(new GuildLivesChangeEvent(EventCause.SYSTEM, user, guild, guild.getLives() - 1))) {
            guild.setHeartLives(warConfig.heartLives);
            guild.updateLives(lives -> lives - 1);
        }

        if (guild.getLives() < 1) {
            this.conquer(attacker, guild, user);
        } else {
            messageService.getMessage(config -> config.warAttacker)
                    .receiver(attacker)
                    .with("{ATTACKED}", guild.getName())
                    .send();
            messageService.getMessage(config -> config.warAttacked)
                    .receiver(guild)
                    .with("{ATTACKER}", attacker.getName())
                    .send();

            if (warConfig.mode == WarMode.MODERN && warConfig.conquerGainLife) {
                this.tryRewardConquerLife(player, attacker, guild, user);
            }
        }
    }

    private void tryRewardConquerLife(Player player, Guild attacker, Guild victim, User user) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        WarConfiguration warConfig = pluginConfiguration.war;
        AntiFarmingConfiguration antiFarming = warConfig.antiFarming;
        MessageService messageService = plugin.getMessageService();

        if (attacker.getLives() >= warConfig.maxLives) {
            return;
        }

        if (antiFarming.enabled) {
            if (victim.getMembers().size() < antiFarming.minVictimMembers) {
                messageService.getMessage(config -> config.warAntiFarmingBlocked)
                        .receiver(player)
                        .send();
                return;
            }

            if (Duration.between(victim.getBorn(), Instant.now()).compareTo(antiFarming.minVictimAge) < 0) {
                messageService.getMessage(config -> config.warAntiFarmingBlocked)
                        .receiver(player)
                        .send();
                return;
            }

            if (!attacker.canGainLifeOn(victim, antiFarming.victimCooldown)) {
                messageService.getMessage(config -> config.warAntiFarmingBlocked)
                        .receiver(player)
                        .send();
                return;
            }

            if (antiFarming.protectSameIp && player.getAddress() != null) {
                String attackerIp = player.getAddress().getAddress().getHostAddress();
                boolean sameIpFound = victim.getMembers().stream()
                        .map(User::getLastIP)
                        .filter(Objects::nonNull)
                        .anyMatch(ip -> ip.equals(attackerIp));

                if (sameIpFound) {
                    messageService.getMessage(config -> config.warAntiFarmingBlocked)
                            .receiver(player)
                            .send();
                    return;
                }
            }
        }

        int livesBefore = attacker.getLives();
        int livesAfter = livesBefore + 1;

        if (!SimpleEventHandler.handle(new GuildLifeGainEvent(EventCause.USER, user, attacker, GainReason.HEART_CONQUER, livesBefore, livesAfter))) {
            return;
        }

        attacker.updateLives(l -> l + 1);
        if (antiFarming.enabled) {
            attacker.setGainLifeCooldownOn(victim, antiFarming.victimCooldown);
        }

        messageService.getMessage(config -> config.warAttackerGainedLife)
                .receiver(attacker)
                .with("{ATTACKED}", victim.getName())
                .with("{LIVES}", attacker.getLives())
                .with("{MAX-LIVES}", warConfig.maxLives)
                .send();
    }

    public void conquer(Guild conqueror, Guild loser, User attacker) {
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser.updateLives(lives -> lives + 1);
            return;
        }

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        WarConfiguration warConfig = pluginConfiguration.war;
        MessageService messageService = plugin.getMessageService();

        conqueror.updateWarsWon(w -> w + 1);
        conqueror.updateWarStreak(s -> s + 1);
        conqueror.setLastWarGuildTag(loser.getTag());

        loser.updateWarsLost(l -> l + 1);
        loser.setWarStreak(0);
        loser.setLastWarGuildTag(conqueror.getTag());

        SimpleEventHandler.handle(new GuildWarWinEvent(EventCause.SYSTEM, attacker, conqueror, loser, conqueror.getWarStreak()));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{WINNER}", conqueror.getTag())
                .register("{LOSER}", loser.getTag());

        messageService.getMessage(config -> config.warWin)
                .receiver(conqueror)
                .with(formatter)
                .send();
        messageService.getMessage(config -> config.warLose)
                .receiver(loser)
                .with(formatter)
                .send();

        plugin.getGuildManager().deleteGuild(plugin, loser);

        if (warConfig.mode == WarMode.MODERN) {
            if (warConfig.winGainLife && conqueror.getLives() < warConfig.maxLives) {
                int livesBefore = conqueror.getLives();
                int livesAfter = livesBefore + 1;

                if (SimpleEventHandler.handle(new GuildLifeGainEvent(EventCause.SYSTEM, attacker, conqueror, GainReason.WAR_WIN, livesBefore, livesAfter))) {
                    conqueror.updateLives(l -> l + 1);
                }
            }

            if (warConfig.streak.enabled && warConfig.streak.broadcastMilestones.contains(conqueror.getWarStreak())) {
                messageService.getMessage(config -> config.warStreakBroadcast)
                        .all()
                        .with("{GUILD}", conqueror.getName())
                        .with("{TAG}", conqueror.getTag())
                        .with("{STREAK}", conqueror.getWarStreak())
                        .send();
            }
        } else {
            conqueror.updateLives(lives -> lives + 1);
        }

        messageService.getMessage(config -> config.broadcastWar)
                .all()
                .with(formatter)
                .send();
    }

}

