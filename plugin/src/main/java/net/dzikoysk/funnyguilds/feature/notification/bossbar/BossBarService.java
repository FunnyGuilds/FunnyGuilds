package net.dzikoysk.funnyguilds.feature.notification.bossbar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider;
import net.dzikoysk.funnyguilds.user.User;

public class BossBarService {

    private final Map<UUID, BossBarProvider> providers = new HashMap<>();

    public BossBarProvider getBossBarProvider(User user) {
        BossBarProvider provider = this.providers.get(user.getUUID());
        if (provider == null) {
            provider = BossBarProvider.getBossBar(user);
            this.providers.put(user.getUUID(), provider);
        }
        return this.providers.get(user.getUUID());
    }

}
