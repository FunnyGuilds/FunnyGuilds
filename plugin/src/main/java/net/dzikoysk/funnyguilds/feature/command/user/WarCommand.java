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
        when (args.length < 1, this.messageConfiguration.enemyCorrectUse);

        Guild enemyGuild = GuildValidation.requireGuildByTag(args[0]);

        when (guild.equals(enemyGuild), this.messageConfiguration.enemySame);
        when (guild.getAllies().contains(enemyGuild), this.messageConfiguration.enemyAlly);
        when (guild.getEnemies().contains(enemyGuild), this.messageConfiguration.enemyAlready);
        when (guild.getEnemies().size() >= this.pluginConfiguration.maxEnemiesBetweenGuilds, () -> this.messageConfiguration.enemyMaxAmount.replace("{AMOUNT}", Integer.toString(this.pluginConfiguration.maxEnemiesBetweenGuilds)));

        if (enemyGuild.getEnemies().size() >= this.pluginConfiguration.maxEnemiesBetweenGuilds) {
            Formatter formatter = new Formatter()
                    .register("{GUILD}", enemyGuild.getName())
                    .register("{TAG}", enemyGuild.getTag())
                    .register("{AMOUNT}", this.pluginConfiguration.maxEnemiesBetweenGuilds);

            player.sendMessage(formatter.format(this.messageConfiguration.enemyMaxTargetAmount));
            return;
        }

        Player enemyOwner = enemyGuild.getOwner().getPlayer();

        guild.addEnemy(enemyGuild);

        String allyDoneMessage = this.messageConfiguration.enemyDone;
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", enemyGuild.getName());
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", enemyGuild.getTag());
        player.sendMessage(allyDoneMessage);

        if (enemyOwner != null) {
            String allyIDoneMessage = this.messageConfiguration.enemyIDone;
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
