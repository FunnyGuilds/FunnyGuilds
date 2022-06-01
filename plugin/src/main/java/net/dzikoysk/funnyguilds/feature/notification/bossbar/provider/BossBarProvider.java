package net.dzikoysk.funnyguilds.feature.notification.bossbar.provider;

import java.time.Duration;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.user.User;
import org.jetbrains.annotations.Nullable;

public interface BossBarProvider {

    default void sendNotification(String text, @Nullable BossBarOptions options, Duration timeout) {
        int timeoutSeconds = Math.toIntExact(timeout.getSeconds());
        this.sendNotification(text, options, (timeoutSeconds > 0) ? timeoutSeconds : 1);
    }

    void sendNotification(String text, @Nullable BossBarOptions options, int timeout);

    void removeNotification();

    static BossBarProvider getBossBar(User user) {
        if (Reflections.SERVER_VERSION.equalsIgnoreCase("v1_8_R3")) {
            return new LegacyBossBarProviderImpl(user);
        }

        return new BossBarProviderImpl(user);
    }
}
