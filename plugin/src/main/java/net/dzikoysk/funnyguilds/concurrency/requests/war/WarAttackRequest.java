package net.dzikoysk.funnyguilds.concurrency.requests.war;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartAttackEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildHeartInteractEvent.Click;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class WarAttackRequest extends DefaultConcurrencyRequest {

    private final FunnyGuilds plugin;
    private final GuildEntityHelper guildEntityHelper;

    private final User user;
    private final int entityId;

    public WarAttackRequest(FunnyGuilds plugin, GuildEntityHelper guildEntityHelper, User user, final int entityId) {
        this.plugin = plugin;
        this.guildEntityHelper = guildEntityHelper;

        this.user = user;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        PandaStream.of(this.guildEntityHelper.getGuildEntities().entrySet())
                .find(entry -> entry.getValue().getId() == this.entityId)
                .peek(entry -> {
                    Option<Player> playerOption = plugin.getFunnyServer().getPlayer(user.getUUID());
                    if (playerOption.isEmpty()) {
                        return;
                    }
                    Player player = playerOption.get();

                    Guild guild = entry.getKey();

                    GuildHeartInteractEvent interactEvent = new GuildHeartInteractEvent(EventCause.USER, user, guild, Click.LEFT, SecuritySystem.onHitCrystal(player, guild));
                    SimpleEventHandler.handle(interactEvent);

                    if (interactEvent.isCancelled() || !interactEvent.isSecurityCheckPassed()) {
                        return;
                    }

                    if (!SimpleEventHandler.handle(new GuildHeartAttackEvent(EventCause.SYSTEM, user, guild))) {
                        return;
                    }

                    WarSystem.getInstance().attack(player, entry.getKey());
                });
    }
}
