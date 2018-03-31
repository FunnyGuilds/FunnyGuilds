package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class PrefixUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final User user;
    private final Guild guild;

    public PrefixUpdateGuildRequest(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        user.getIndividualPrefix().addGuild(guild);
    }

}
