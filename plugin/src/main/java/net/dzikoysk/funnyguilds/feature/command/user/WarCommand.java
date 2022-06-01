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
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

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

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag())
                .register("{AMOUNT}", config.maxEnemiesBetweenGuilds);

        when(guild.equals(enemyGuild), messages.enemySame);
        when(guild.isAlly(enemyGuild), messages.enemyAlly);
        when(guild.isEnemy(enemyGuild), messages.enemyAlready);
        when(guild.getEnemies().size() >= config.maxEnemiesBetweenGuilds, formatter.format(messages.enemyMaxAmount));

        if (enemyGuild.getEnemies().size() >= config.maxEnemiesBetweenGuilds) {
            user.sendMessage(formatter.format(messages.enemyMaxTargetAmount));
            return;
        }

        guild.addEnemy(enemyGuild);

        FunnyFormatter enemyFormatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag());

        FunnyFormatter enemyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        user.sendMessage(enemyFormatter.format(messages.enemyDone));
        enemyGuild.getOwner().sendMessage(enemyIFormatter.format(messages.enemyIDone));

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
