package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class WarCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.war.name}",
            description = "${user.war.description}",
            aliases = "${user.war.aliases}",
            permission = "funnyguilds.war",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild, String[] args) {
        when(args.length < 1, messages.enemyCorrectUse);

        Guild enemyGuild = GuildValidation.requireGuildByTag(args[0]);

        when(guild.equals(enemyGuild), messages.enemySame);
        when(guild.getAllies().contains(enemyGuild), messages.enemyAlly);
        when(guild.getEnemies().contains(enemyGuild), messages.enemyAlready);
        when(guild.getEnemies().size() >= config.maxEnemiesBetweenGuilds, () -> messages.enemyMaxAmount.replace("{AMOUNT}", Integer.toString(config.maxEnemiesBetweenGuilds)));

        if (enemyGuild.getEnemies().size() >= config.maxEnemiesBetweenGuilds) {
            Formatter formatter = new Formatter()
                    .register("{GUILD}", enemyGuild.getName())
                    .register("{TAG}", enemyGuild.getTag())
                    .register("{AMOUNT}", config.maxEnemiesBetweenGuilds);

            player.sendMessage(formatter.format(messages.enemyMaxTargetAmount));
            return;
        }

        Player enemyOwner = enemyGuild.getOwner().getPlayer();

        guild.addEnemy(enemyGuild);

        String allyDoneMessage = messages.enemyDone;
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", enemyGuild.getName());
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", enemyGuild.getTag());
        player.sendMessage(allyDoneMessage);

        if (enemyOwner != null) {
            String allyIDoneMessage = messages.enemyIDone;
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
            enemyOwner.sendMessage(allyIDoneMessage);
        }

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        for (User member : guild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, enemyGuild));
        }

        for (User member : enemyGuild.getMembers()) {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
        }

        this.concurrencyManager.postTask(taskBuilder.build());
    }

}
