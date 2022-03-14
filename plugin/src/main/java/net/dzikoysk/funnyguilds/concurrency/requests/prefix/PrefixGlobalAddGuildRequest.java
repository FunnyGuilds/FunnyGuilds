package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.UserManager;

public class PrefixGlobalAddGuildRequest extends DefaultConcurrencyRequest {

    private final UserManager userManager;

    private final Guild guild;

    public PrefixGlobalAddGuildRequest(UserManager userManager, Guild guild) {
        this.userManager = userManager;
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.addGuild(userManager, guild);
    }

}
