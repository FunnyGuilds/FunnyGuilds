package net.dzikoysk.funnyguilds.user.profile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.shared.Position;

public class FakeUserProfile implements UserProfile {

    private final Set<String> permissions = new HashSet<>();
    private final boolean online;
    private final boolean vanished;
    private final int ping;

    public FakeUserProfile(boolean online, boolean vanished, int ping) {
        this.online = online;
        this.vanished = vanished;
        this.ping = ping;
    }

    public FakeUserProfile(boolean online, boolean vanished, int ping, String... permissions) {
        this.online = online;
        this.vanished = vanished;
        this.ping = ping;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    @Override
    public Position getPosition() {
        return Position.ZERO.changeWorld("world");
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
    public int getPing() {
        return this.ping;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    public void clearPermissions() {
        this.permissions.clear();
    }

    public static FakeUserProfile online() {
        return new FakeUserProfile(true, false, 0);
    }

    public static FakeUserProfile online(int ping) {
        return new FakeUserProfile(true, false, ping);
    }

    public static FakeUserProfile offline() {
        return new FakeUserProfile(false, false, 0);
    }

    public static FakeUserProfile vanished() {
        return new FakeUserProfile(false, true, 0);
    }

}
