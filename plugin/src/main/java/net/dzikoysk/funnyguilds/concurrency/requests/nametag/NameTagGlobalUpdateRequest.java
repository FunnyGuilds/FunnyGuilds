package net.dzikoysk.funnyguilds.concurrency.requests.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import org.bukkit.Bukkit;

public class NameTagGlobalUpdateRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final IndividualNameTagManager individualNameTagManager;

    public NameTagGlobalUpdateRequest(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.individualNameTagManager = plugin.getIndividualNameTagManager();
    }

    @Override
    public void execute() throws Exception {
        Bukkit.getScheduler().runTask(this.plugin, this.individualNameTagManager::updatePlayers);
    }

}
