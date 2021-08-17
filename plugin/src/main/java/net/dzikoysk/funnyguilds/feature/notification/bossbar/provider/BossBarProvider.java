package net.dzikoysk.funnyguilds.feature.notification.bossbar.provider;

import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.v1_8.BossBarProviderImpl;
import net.dzikoysk.funnyguilds.nms.Reflections;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public interface BossBarProvider {

    default void sendNotification(String text, @Nullable BossBarOptions options, Duration timeout) {
        int timeoutSeconds = Math.toIntExact(timeout.getSeconds());
        this.sendNotification(text, options, (timeoutSeconds > 0) ? timeoutSeconds : 1);
    }

    void sendNotification(String text, @Nullable BossBarOptions options, int timeout);

    void removeNotification();

    static BossBarProvider getBossBar(User user) {
        switch (Reflections.SERVER_VERSION) {
            case "v1_8_R1":
            case "v1_8_R3":
                return new BossBarProviderImpl(user);
            default:
                return new DefaultBossBarProvider(user);
        }
    }
}