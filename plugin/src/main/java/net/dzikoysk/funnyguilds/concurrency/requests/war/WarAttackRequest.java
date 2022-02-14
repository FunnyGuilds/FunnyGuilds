package net.dzikoysk.funnyguilds.concurrency.requests.war;

import java.util.Map;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildConquerEvent;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;

public class WarAttackRequest extends DefaultConcurrencyRequest {

    private final Player player;
    private final int entityId;

    public WarAttackRequest(final Player player, final int entityId) {
        this.player = player;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        for (Map.Entry<Guild, FakeEntity> entry : GuildEntityHelper.getGuildEntities().entrySet()) {
            if (entry.getValue().getId() != entityId) {
                continue;
            }

            Guild guild = entry.getKey();

            Option<User> userOption = FunnyGuilds.getInstance().getUserManager().findByPlayer(player);
            if (userOption.isEmpty()) {
                return;
            }

            User user = userOption.get();

            if (!SimpleEventHandler.handle(new GuildConquerEvent(EventCause.SYSTEM, user, guild))) {
                return;
            }

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            WarSystem.getInstance().attack(player, entry.getKey());
            return;
        }
    }
}
