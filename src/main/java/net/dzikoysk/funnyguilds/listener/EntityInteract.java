package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserManager;
import net.dzikoysk.funnyguilds.command.user.PlayerInfoCommand;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.concurrent.TimeUnit;

public class EntityInteract implements Listener {

    private final PlayerInfoCommand playerExecutor;
    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();
    private final FunnyGuilds plugin;

    public EntityInteract(FunnyGuilds plugin) {
        this.playerExecutor = new PlayerInfoCommand();
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        UserManager userManager = plugin.getUserManager();
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
                    playerExecutor.execute(plugin, config, messages, eventCaller, new String[]{clickedPlayer.getName()});
                } catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(eventCaller::sendMessage);
                }
            }
            else {
                playerExecutor.sendInfoMessage(messages.playerRightClickInfo, userManager.getUser(clickedPlayer), eventCaller);
            }
        }

        if (config.regionExplodeBlockInteractions && clickedEntity instanceof InventoryHolder) {
            User user = userManager.getUser(eventCaller);

            if (user.hasGuild() && !user.getGuild().canBuild()) {
                event.setCancelled(true);
            }
        }
    }

}
