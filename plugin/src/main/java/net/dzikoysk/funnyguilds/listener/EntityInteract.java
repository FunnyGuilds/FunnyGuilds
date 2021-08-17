package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.command.user.PlayerInfoCommand;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.concurrent.TimeUnit;

public class EntityInteract implements Listener {

    private final PlayerInfoCommand playerExecutor = new PlayerInfoCommand();
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
                try {
                    playerExecutor.execute(config, messages, eventCaller, new String[]{clickedPlayer.getName()});
                } catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(eventCaller::sendMessage);
                }
            }
            else {
                playerExecutor.sendInfoMessage(messages.playerRightClickInfo, User.get(clickedPlayer), eventCaller);
            }
        }

        if (config.regionExplodeBlockInteractions && clickedEntity instanceof InventoryHolder) {
            User user = User.get(eventCaller);

            if (user.hasGuild() && !user.getGuild().canBuild()) {
                event.setCancelled(true);
            }
        }
    }

}
