package net.dzikoysk.funnyguilds.listener;

import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.command.user.PlayerInfoCommand;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

public class EntityInteract extends AbstractFunnyListener {

    private final PlayerInfoCommand playerExecutor;
    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    public EntityInteract(FunnyGuilds plugin) throws Throwable {
        this.playerExecutor = plugin.getInjector().newInstanceWithFields(PlayerInfoCommand.class);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player eventCaller = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (clickedEntity instanceof Player) {
            Player clickedPlayer = (Player) clickedEntity;

            if (!config.infoPlayerEnabled ||
                    (config.infoPlayerSneaking && !eventCaller.isSneaking()) ||
                    informationMessageCooldowns.cooldown(eventCaller, TimeUnit.SECONDS, config.infoPlayerCooldown)) {

                return;
            }

            if (config.infoPlayerCommand) {
                try {
                    playerExecutor.execute(eventCaller, new String[] {clickedPlayer.getName()});
                }
                catch (ValidationException validatorException) {
                    validatorException.getValidationMessage().peek(eventCaller::sendMessage);
                }
            }
            else {
                this.userManager.findByPlayer(clickedPlayer)
                        .peek(user -> playerExecutor.sendInfoMessage(messages.playerRightClickInfo, user, eventCaller));
            }
        }

        if (config.regionExplodeBlockInteractions && clickedEntity instanceof InventoryHolder) {
            this.userManager.findByPlayer(eventCaller)
                    .filter(user -> user.hasGuild() && !user.getGuild().canBuild())
                    .peek(user -> event.setCancelled(true));
        }
    }

}
