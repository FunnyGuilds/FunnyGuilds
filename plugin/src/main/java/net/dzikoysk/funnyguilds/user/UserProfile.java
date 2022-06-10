package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.shared.Position;

public interface UserProfile {

    default boolean isOnline() {
        return false;
    }

    default boolean isVanished() {
        return false;
    }

    default boolean hasPermission(String permission) {
        return false;
    }

    default int getPing() {
        return -1;
    }

    void sendMessage(String message);

    void kick(String reason);

    void teleport(Position position);

    void refresh();

    default Position getPosition() {
        return Position.ZERO;
    }

}
