package net.dzikoysk.funnyguilds.concurrency.requests.war;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.feature.war.WarSystem;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import org.bukkit.entity.Player;

import java.util.Map;

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

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            WarSystem.getInstance().attack(player, entry.getKey());
            return;
        }
    }
}
