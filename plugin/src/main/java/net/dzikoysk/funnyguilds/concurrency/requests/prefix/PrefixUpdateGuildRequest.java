package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

public class PrefixUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final User user;
    private final Guild guild;

    public PrefixUpdateGuildRequest(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
    }

    @Override
    public void execute() {
        this.user.getCache().getIndividualPrefix().peek(prefix -> prefix.addGuild(this.guild));
    }

}
