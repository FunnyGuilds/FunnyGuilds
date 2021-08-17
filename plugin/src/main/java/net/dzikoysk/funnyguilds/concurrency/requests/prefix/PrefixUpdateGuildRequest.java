package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefix;

public class PrefixUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final User user;
    private final Guild guild;

    public PrefixUpdateGuildRequest(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
    }

    @Override
    public void execute() {
        IndividualPrefix prefix = user.getCache().getIndividualPrefix();

        if (prefix == null) {
            return;
        }

        prefix.addGuild(guild);
    }

}
