package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class PrefixGlobalRemoveGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;
    private final FunnyGuilds plugin;

    public PrefixGlobalRemoveGuildRequest(Guild guild, FunnyGuilds plugin) {
        this.guild = guild;
        this.plugin = plugin;
    }

    @Override
    public void execute() {
        plugin.getSystemManager().getIndividualPrefixManager().removeGuild(guild);
    }

}
