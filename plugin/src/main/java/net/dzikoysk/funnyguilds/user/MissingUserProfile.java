package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.shared.Position;

class MissingUserProfile implements UserProfile {

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean isVanished() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void kick(String reason) {
    }

    @Override
    public void teleport(Position position) {
    }

    @Override
    public Position getPosition() {
        return Position.ZERO;
    }

}
