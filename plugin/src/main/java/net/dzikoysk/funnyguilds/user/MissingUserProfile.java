package net.dzikoysk.funnyguilds.user;

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

}
