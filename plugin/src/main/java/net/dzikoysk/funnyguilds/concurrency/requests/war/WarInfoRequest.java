package net.dzikoysk.funnyguilds.concurrency.requests.war;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WarInfoRequest extends DefaultConcurrencyRequest {

    private static final InfoCommand INFO_EXECUTOR = new InfoCommand();

    private final Player player;
    private final int entityId;

    public WarInfoRequest(final Player player, final int entityId) {
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

            PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

            if (config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                return;
            }

            try {
                INFO_EXECUTOR.execute(config, FunnyGuilds.getInstance().getMessageConfiguration(), player, new String[]{ entry.getKey().getTag() });
                return;
            } catch (ValidationException validatorException) {
                validatorException.getValidationMessage().peek(player::sendMessage);
            }
        }
    }
}
