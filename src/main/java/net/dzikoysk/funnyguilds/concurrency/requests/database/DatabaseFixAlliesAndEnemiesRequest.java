package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DatabaseFixAlliesAndEnemiesRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() {
        for (Guild guild : GuildUtils.getGuilds()) {
            fixAllies(guild);
        }
    }

    private void fixAllies(Guild guild) {
        for (Guild ally : guild.getAllies()) {
            fixAlly(guild, ally);
        }

        for (Guild enemy : guild.getEnemies()) {
            fixEnemy(guild, enemy);
        }
    }

    private void fixAlly(Guild guild, Guild ally) {
        if (!ally.getAllies().contains(guild)) {
            ally.addAlly(guild);
        }
    }

    private void fixEnemy(Guild guild, Guild enemy) {
        if (!enemy.getEnemies().contains(guild)) {
            enemy.addEnemy(guild);
        }
    }

}
