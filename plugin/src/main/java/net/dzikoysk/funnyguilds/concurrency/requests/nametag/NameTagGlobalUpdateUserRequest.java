package net.dzikoysk.funnyguilds.concurrency.requests.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.IndividualNameTagManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;

public class NameTagGlobalUpdateUserRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final IndividualNameTagManager individualNameTagManager;
    private final User user;

    public NameTagGlobalUpdateUserRequest(FunnyGuilds plugin, User user) {
        this.plugin = plugin;
        this.individualNameTagManager = plugin.getIndividualNameTagManager();
        this.user = user;
    }

    @Override
    public void execute() throws Exception {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.individualNameTagManager.updatePlayer(this.user));
    }

}
