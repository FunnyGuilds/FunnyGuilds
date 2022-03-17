package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.guild.Guild;

public class PrefixGlobalRemoveGuildRequest extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;

    private final Guild guild;

    public PrefixGlobalRemoveGuildRequest(IndividualPrefixManager individualPrefixManager, Guild guild) {
        this.individualPrefixManager = individualPrefixManager;
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        this.individualPrefixManager.removeGuild(guild);
    }

}
