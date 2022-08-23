package net.dzikoysk.funnyguilds.feature.notification.bossbar;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.user.User;

public class BossBarService {

    private final Map<UUID, BossBarProvider> providers = new ConcurrentHashMap<>();

    public BossBarProvider getBossBarProvider(FunnyServer funnyServer, User user) {
        return this.providers.computeIfAbsent(user.getUUID(), uuid -> BossBarProvider.getBossBar(funnyServer, user));
    }

}
