package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

public class PlayerInteract extends AbstractFunnyListener {

    private final InfoCommand infoExecutor;

    public PlayerInteract(FunnyGuilds plugin) throws Throwable {
        this.infoExecutor = plugin.getInjector().newInstanceWithFields(InfoCommand.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action eventAction = event.getAction();
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (eventAction != Action.RIGHT_CLICK_BLOCK && eventAction != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (clicked == null) {
            return;
        }

        Option<Region> regionOption = this.regionManager.findRegionAtLocation(clicked.getLocation());
        if (regionOption.isEmpty()) {
            return;
        }

        Region region = regionOption.get();
        boolean returnMethod = region.getHeartBlock()
                .filter(heart -> heart.equals(clicked))
                .peek(heart -> this.handleHeartClick(player, eventAction, region.getGuild(), event))
                .isPresent();

        if (returnMethod) {
            return;
        }

        if (eventAction == Action.RIGHT_CLICK_BLOCK) {
            Guild guild = region.getGuild();
            
            // Check for TNT ignition (right-click TNT with flint and steel or fire charge)
            if (clicked.getType() == Material.TNT && isTntIgnitionItem(player)) {
                boolean isProtected = ProtectionSystem.isProtected(
                                player,
                                clicked.getLocation(),
                                event,
                                GuildProtectionPermission.BLOCK_IGNITE,
                                false
                        )
                        .peek(ProtectionSystem::defaultResponse)
                        .isPresent();
                
                if (isProtected) {
                    event.setCancelled(true);
                    return;
                }
            }

            this.userManager.findByPlayer(player).peek(user -> {
                boolean blocked = this.config.blockedInteract.contains(clicked.getType());

                if (guild.isMember(user)) {
                    event.setCancelled(blocked && this.config.regionExplodeBlockInteractions && !guild.canBuild());
                }
                else {
                    event.setCancelled(blocked && !player.hasPermission("funnyguilds.admin.interact"));
                }
            });
        }
    }
    
    /**
     * Check if the player is holding an item that can ignite TNT.
     */
    private boolean isTntIgnitionItem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        
        return isIgnitionMaterial(mainHand) || isIgnitionMaterial(offHand);
    }
    
    /**
     * Check if the item can ignite TNT.
     */
    private boolean isIgnitionMaterial(ItemStack item) {
        if (item == null) {
            return false;
        }
        Material material = item.getType();
        return material == Material.FLINT_AND_STEEL || material == Material.FIRE_CHARGE;
    }

    private void handleHeartClick(Player player, Action eventAction, Guild guild, Cancellable event) {
        event.setCancelled(true);

        Option<User> userOption = this.userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }
        User user = userOption.get();

        GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(EventCause.USER, user, guild,
                eventAction == Action.LEFT_CLICK_BLOCK ? Click.LEFT : Click.RIGHT, !SecuritySystem.onHitCrystal(player, guild));
        SimpleEventHandler.handle(interactEvent);

        if (interactEvent.isCancelled() || !interactEvent.isSecurityCheckPassed()) {
            return;
        }

        if (eventAction == Action.LEFT_CLICK_BLOCK) {
            WarSystem.getInstance().attack(player, guild);
            return;
        }

        if (this.config.informationMessageCooldowns.cooldown(player.getUniqueId(), this.config.infoPlayerCooldown)) {
            return;
        }

        try {
            this.infoExecutor.execute(player, new String[] {guild.getTag()});
        }
        catch (InternalValidationException validatorException) {
            this.messageService.getMessage(validatorException.getMessageSupplier())
                    .with(validatorException.getReplacements())
                    .receiver(player)
                    .send();
        }
    }

}
