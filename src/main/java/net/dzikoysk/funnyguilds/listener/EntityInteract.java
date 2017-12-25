package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.command.ExcPlayer;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.concurrent.TimeUnit;

public class EntityInteract implements Listener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        PluginConfig config = Settings.getConfig();
        Player eventCaller = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (clickedEntity instanceof Player) {
            Player clickedPlayer = (Player) clickedEntity;

            if (!config.infoPlayerEnabled || (config.infoPlayerSneaking && !eventCaller.isSneaking()) || informationMessageCooldowns.cooldown(eventCaller, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                return;
            }

            new ExcPlayer().execute(eventCaller, new String[]{clickedPlayer.getName()});
        }
    }

}
