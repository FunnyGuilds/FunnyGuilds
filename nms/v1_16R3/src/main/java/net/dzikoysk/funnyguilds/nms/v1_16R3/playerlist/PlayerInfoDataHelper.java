package net.dzikoysk.funnyguilds.nms.v1_16R3.playerlist;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PlayerInfoDataHelper {

    private final Constructor<?> playerInfoDataConstructor;
    private final Object gameMode;

    public PlayerInfoDataHelper(Class<?> packetPlayOutPlayerInfoClass, Object gameMode) {
        Class<?> playerInfoDataClass = findPlayerInfoDataClass(packetPlayOutPlayerInfoClass);
        this.playerInfoDataConstructor = playerInfoDataClass.getDeclaredConstructors()[0];
        this.gameMode = gameMode;
    }

    public Object createPlayerInfoData(Object packetPlayOutPlayerInfo, GameProfile gameProfile, int ping, Object displayName) {
        try {
            return this.playerInfoDataConstructor.newInstance(packetPlayOutPlayerInfo, gameProfile, ping, this.gameMode, displayName);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException("Failed to create PlayerInfoData instance", exception);
        }
    }

    private static Class<?> findPlayerInfoDataClass(Class<?> packetPlayOutPlayerInfoClass) {
        for (Class<?> candidate : packetPlayOutPlayerInfoClass.getDeclaredClasses()) {
            if (candidate.getSimpleName().equals("PlayerInfoData")) {
                return candidate;
            }
        }

        throw new IllegalStateException("Can't find PlayerInfoData in PacketPlayOutPlayerInfo");
    }

}
