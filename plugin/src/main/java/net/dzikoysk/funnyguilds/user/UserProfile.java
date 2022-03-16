package net.dzikoysk.funnyguilds.user;

public interface UserProfile {

    UserProfile NONE = new MissingUserProfile();

    boolean isOnline();

    boolean isVanished();

    boolean hasPermission(String permission);

    int getPing();

    void sendMessage(String message);

    void kick(String reason);

}
