package net.dzikoysk.funnyguilds.concurrency.requests.war;

import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;

public class WarInfoRequest extends DefaultConcurrencyRequest {

    private InfoCommand infoExecutor;

    private final User user;
    private final int entityId;

    public WarInfoRequest(FunnyGuilds plugin, User user, int entityId) {
        try {
            this.infoExecutor = plugin.getInjector().newInstanceWithFields(InfoCommand.class);
        }
        catch (Throwable throwable) {
            FunnyGuilds.getPluginLogger().error("An error occurred while creating war info request", throwable);
        }

        this.user = user;
        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        for (Map.Entry<Guild, FakeEntity> entry : GuildEntityHelper.getGuildEntities().entrySet()) {
            if (entry.getValue().getId() != entityId) {
                continue;
            }

            Guild guild = entry.getKey();

            Option<Player> playerOption = this.user.getPlayer();
            if (playerOption.isEmpty()) {
                return;
            }

            Player player = playerOption.get();

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
                infoExecutor.execute(player, new String[] {entry.getKey().getTag()});
                return;
            }
            catch (ValidationException validatorException) {
                validatorException.getValidationMessage().peek(player::sendMessage);
            }
        }
    }
}
