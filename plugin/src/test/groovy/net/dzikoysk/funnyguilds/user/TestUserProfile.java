package net.dzikoysk.funnyguilds.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestUserProfile extends MissingUserProfile implements UserProfile {

    public static final TestUserProfile ONLINE = new TestUserProfile(true, false);

    public static final TestUserProfile VANISHED = new TestUserProfile(false, true);

    private final Set<String> permissions = new HashSet<>();
    private final boolean online;
    private final boolean vanished;

    public TestUserProfile(boolean online, boolean vanished, String... permissions) {
        this.online = online;
        this.vanished = vanished;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void addPermission(String permission) {
        permissions.add(permission);
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
