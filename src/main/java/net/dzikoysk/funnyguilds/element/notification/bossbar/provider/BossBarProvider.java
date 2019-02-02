package net.dzikoysk.funnyguilds.element.notification.bossbar.provider;

import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.v1_8.BossBarProviderImpl;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.jetbrains.annotations.Nullable;

public interface BossBarProvider {

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
