package net.dzikoysk.funnyguilds.concurrency.requests.war;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
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

            FunnyGuilds plugin = FunnyGuilds.getInstance();
            PluginConfiguration config = plugin.getPluginConfiguration();
            MessageConfiguration messages = plugin.getMessageConfiguration();

            if (config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                return;
            }

            try {
                INFO_EXECUTOR.execute(player, new String[]{ entry.getKey().getTag() });
                return;
            } catch (ValidationException validatorException) {
                validatorException.getValidationMessage().peek(player::sendMessage);
            }
        }
    }
}
