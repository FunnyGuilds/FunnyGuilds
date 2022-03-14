package net.dzikoysk.funnyguilds.user;

class UserProfileNone implements UserProfile {

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

}
