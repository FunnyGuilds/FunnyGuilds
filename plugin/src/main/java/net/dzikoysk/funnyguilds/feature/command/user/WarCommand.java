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
import panda.std.stream.PandaStream;

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
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        when(args.length < 1, this.messages.enemyCorrectUse);
        Guild enemyGuild = GuildValidation.requireGuildByTag(args[0]);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag())
                .register("{AMOUNT}", this.config.maxEnemiesBetweenGuilds);

        when(guild.equals(enemyGuild), this.messages.enemySame);
        when(guild.isAlly(enemyGuild), this.messages.enemyAlly);
        when(guild.isEnemy(enemyGuild), this.messages.enemyAlready);
        when(guild.getEnemies().size() >= this.config.maxEnemiesBetweenGuilds, formatter.format(this.messages.enemyMaxAmount));

        if (enemyGuild.getEnemies().size() >= this.config.maxEnemiesBetweenGuilds) {
            owner.sendMessage(formatter.format(this.messages.enemyMaxTargetAmount));
            return;
        }

        guild.addEnemy(enemyGuild);

        FunnyFormatter enemyFormatter = new FunnyFormatter()
                .register("{GUILD}", enemyGuild.getName())
                .register("{TAG}", enemyGuild.getTag());

        FunnyFormatter enemyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        owner.sendMessage(enemyFormatter.format(this.messages.enemyDone));
        enemyGuild.getOwner().sendMessage(enemyIFormatter.format(this.messages.enemyIDone));

        ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

        PandaStream.of(guild.getMembers()).forEach(member -> {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, enemyGuild));
        });

        PandaStream.of(enemyGuild.getMembers()).forEach(member -> {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
        });

        this.concurrencyManager.postTask(taskBuilder.build());
    }

}
