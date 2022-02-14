package net.dzikoysk.funnyguilds.listener.region;

import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartAttackEvent;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
                .peek(heart -> {
                    if (heart.getType() == Material.DRAGON_EGG) {
                        event.setCancelled(true);
                    }

                    Guild guild = region.getGuild();

                    Option<User> userOption = this.userManager.findByPlayer(player);
                    if (userOption.isEmpty()) {
                        return;
                    }

                    User user = userOption.get();

                    if (!SimpleEventHandler.handle(new GuildHeartAttackEvent(EventCause.USER, user, guild))) {
                        return;
                    }

                    if (SecuritySystem.onHitCrystal(player, guild)) {
                        return;
                    }

                    event.setCancelled(true);

                    if (eventAction == Action.LEFT_CLICK_BLOCK) {
                        WarSystem.getInstance().attack(player, guild);
                        return;
                    }

                    if (!config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                        try {
                            infoExecutor.execute(player, new String[] {guild.getTag()});
                        }
                        catch (ValidationException validatorException) {
                            validatorException.getValidationMessage().peek(player::sendMessage);
                        }
                    }
                })
                .isPresent();

        if (returnMethod) {
            return;
        }

        if (eventAction == Action.RIGHT_CLICK_BLOCK) {
            Guild guild = region.getGuild();

            this.userManager.findByPlayer(player).peek(user -> {
                boolean blocked = config.blockedInteract.contains(clicked.getType());

                if (guild.getMembers().contains(user)) {
                    event.setCancelled(blocked && config.regionExplodeBlockInteractions && !guild.canBuild());
                }
                else {
                    event.setCancelled(blocked && !player.hasPermission("funnyguilds.admin.interact"));
                }
            });
        }

    }

}
