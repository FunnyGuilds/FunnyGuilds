package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.user.UserManager;

public class PrefixGlobalAddPlayerRequest extends DefaultConcurrencyRequest {

    private final UserManager userManager;

    private final String player;

    public PrefixGlobalAddPlayerRequest(UserManager userManager, String player) {
        this.userManager = userManager;
        this.player = player;
    }

    @Override
    public void execute() throws Exception {
        IndividualPrefixManager.addPlayer(userManager, player);
    }

}
