package net.dzikoysk.funnyguilds.concurrency.requests.war;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.feature.security.SecuritySystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.std.Option;

public class WarInfoRequest extends DefaultConcurrencyRequest {

    private InfoCommand infoExecutor;

    private final GuildEntityHelper guildEntityHelper;

    private final FunnyGuilds plugin;
    private final User user;
    private final int entityId;

    public WarInfoRequest(FunnyGuilds plugin, GuildEntityHelper guildEntityHelper, User user, int entityId) {
        this.user = user;
        this.plugin = plugin;
        try {
            this.infoExecutor = plugin.getInjector().newInstanceWithFields(InfoCommand.class);
        }
        catch (Throwable throwable) {
            FunnyGuilds.getPluginLogger().error("An error occurred while creating war info request", throwable);
        }
        this.guildEntityHelper = guildEntityHelper;

        this.entityId = entityId;
    }

    @Override
    public void execute() throws Exception {
        for (Map.Entry<Guild, FakeEntity> entry : this.guildEntityHelper.getGuildEntities().entrySet()) {
            if (entry.getValue().getId() != entityId) {
                continue;
            }

            Option<Player> playerOption = plugin.getFunnyServer().getPlayer(user.getUUID());

            if (playerOption.isEmpty()) {
                return;
            }

            Player player = playerOption.get();
            Guild guild = entry.getKey();

            if (SecuritySystem.onHitCrystal(player, guild)) {
                return;
            }

            FunnyGuilds plugin = FunnyGuilds.getInstance();
            PluginConfiguration config = plugin.getPluginConfiguration();

            if (config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                return;
            }

            try {
                infoExecutor.execute(player, new String[]{entry.getKey().getTag()});
                return;
            }
            catch (ValidationException validatorException) {
                validatorException.getValidationMessage().peek(message -> ChatUtils.sendMessage(player, message));
            }
        }
    }
}
