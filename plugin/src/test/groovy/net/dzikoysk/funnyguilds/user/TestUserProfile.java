package net.dzikoysk.funnyguilds.user;

import java.util.HashSet;
import java.util.Set;

public class TestUserProfile extends UserProfileNone implements UserProfile {

    public static final TestUserProfile ONLINE = new TestUserProfile()
            .online(true);

    public static final TestUserProfile VANISHED = new TestUserProfile()
            .vanished(true);

    private final Set<String> permissions = new HashSet<>();
    private boolean online = false;
    private boolean vanished = false;

    public TestUserProfile online(boolean online) {
        this.online = online;
        return this;
    }

    public TestUserProfile vanished(boolean vanished) {
        this.vanished = vanished;
        return this;
    }

    public TestUserProfile permission(String permission) {
        permissions.add(permission);
        return this;
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    @Override
    public boolean isVanished() {
        return this.vanished;
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

}
