package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.guild.Guild;

public class PrefixGlobalRemoveGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public PrefixGlobalRemoveGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.removeGuild(guild);
    }

}
