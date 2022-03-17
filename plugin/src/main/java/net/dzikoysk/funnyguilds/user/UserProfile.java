package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.shared.Position;

public interface UserProfile {

    UserProfile NONE = new MissingUserProfile();

    boolean isOnline();

    boolean isVanished();

    boolean hasPermission(String permission);

    default int getPing() {
        return - 1;
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
