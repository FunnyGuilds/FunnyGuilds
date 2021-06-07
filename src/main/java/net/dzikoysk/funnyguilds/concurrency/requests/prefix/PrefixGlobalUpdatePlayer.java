package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;
import org.bukkit.entity.Player;

public class PrefixGlobalUpdatePlayer extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;
    private final Player player;

    public PrefixGlobalUpdatePlayer(FunnyGuilds plugin, Player player) {
        this.individualPrefixManager = plugin.getSystemManager().getIndividualPrefixManager();
        this.player = player;
    }

    @Override
    public void execute() {
        individualPrefixManager.updatePlayer(player);
    }

}
