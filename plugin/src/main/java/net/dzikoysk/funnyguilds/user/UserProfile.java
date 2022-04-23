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

    default void sendMessage(String message) {
    }

    default void kick(String reason) {
    }

    default void teleport(Position position) {
    }

    default Position getPosition() {
        return Position.ZERO;
    }

}
