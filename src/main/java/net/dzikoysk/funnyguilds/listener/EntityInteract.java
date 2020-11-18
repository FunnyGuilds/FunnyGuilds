package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.ExcPlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.concurrent.TimeUnit;

public class EntityInteract implements Listener {

    private final ExcPlayer playerExecutor = new ExcPlayer();
    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Player eventCaller = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (clickedEntity instanceof Player) {
            Player clickedPlayer = (Player) clickedEntity;

            if (!config.infoPlayerEnabled
                    || (config.infoPlayerSneaking && !eventCaller.isSneaking())
                    || informationMessageCooldowns.cooldown(eventCaller, TimeUnit.SECONDS, config.infoPlayerCooldown)) {

                return;
            }

            if (config.infoPlayerCommand) {
                playerExecutor.execute(eventCaller, new String[]{clickedPlayer.getName()});
            }
            else {
                playerExecutor.sendInfoMessage(messages.playerRightClickInfo, User.get(clickedPlayer), eventCaller);
            }
        }
    }

}
