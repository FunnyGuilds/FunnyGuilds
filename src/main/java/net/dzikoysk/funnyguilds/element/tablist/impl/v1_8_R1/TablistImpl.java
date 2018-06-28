package net.dzikoysk.funnyguilds.element.tablist.impl.v1_8_R1;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TablistImpl extends AbstractTablist {

    private static final Class<?> PLAYER_INFO_CLASS;
    private static final Class<?> PLAYER_LIST_HEADER_FOOTER_CLASS;
    private static final Class<?> PLAYER_INFO_DATA_CLASS;
    private static final Class<?> GAME_PROFILE_CLASS;
    private static final Class<?> ENUM_GAMEMODE_CLASS;
    private static final Class<?> BASE_COMPONENT_CLASS;

    private static final Field ACTION_ENUM_FIELD;
    private static final Field LIST_FIELD;
    private static final Field HEADER_FIELD;
    private static final Field FOOTER_FIELD;

    private static final Enum<?> ADD_PLAYER;
    private static final Enum<?> UPDATE_PLAYER;
    private static final String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
    private static final String TOKEN = "!@#$^*";
    private static Constructor<?> playerInfoDataConstructor;
    private static Constructor<?> gameProfileConstructor;

    static {
        PLAYER_INFO_CLASS = Reflections.getNMSClass("PacketPlayOutPlayerInfo");
        PLAYER_LIST_HEADER_FOOTER_CLASS = Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter");
        PLAYER_INFO_DATA_CLASS = Reflections.getNMSClass("PlayerInfoData");
        GAME_PROFILE_CLASS = Reflections.getClass("com.mojang.authlib.GameProfile");
        ENUM_GAMEMODE_CLASS = Reflections.getNMSClass("EnumGamemode");
        BASE_COMPONENT_CLASS = Reflections.getNMSClass("IChatBaseComponent");

        ACTION_ENUM_FIELD = Reflections.getField(PLAYER_INFO_CLASS, "a");
        LIST_FIELD = Reflections.getField(PLAYER_INFO_CLASS, "b");
        HEADER_FIELD = Reflections.getField(PLAYER_LIST_HEADER_FOOTER_CLASS, "a");
        FOOTER_FIELD = Reflections.getField(PLAYER_LIST_HEADER_FOOTER_CLASS, "b");

        ADD_PLAYER = (Enum<?>) Reflections.getNMSClass("EnumPlayerInfoAction").getEnumConstants()[0];
        UPDATE_PLAYER = (Enum<?>) Reflections.getNMSClass("EnumPlayerInfoAction").getEnumConstants()[3];

        try {
            playerInfoDataConstructor = PLAYER_INFO_DATA_CLASS.getConstructor(
                    PLAYER_INFO_CLASS,
                    GAME_PROFILE_CLASS,
                    int.class,
                    ENUM_GAMEMODE_CLASS,
                    BASE_COMPONENT_CLASS
            );
            
            gameProfileConstructor = GAME_PROFILE_CLASS.getConstructor(
                    UUID.class,
                    String.class
            );
        } catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
        }

    }

    private final Object[] profileCache = new Object[80];

    public TablistImpl(final Map<Integer, String> tablistPattern, final String header, final String footer, final int ping, final UUID player) {
        super(tablistPattern, header, footer, ping, player);
    }

    @Override
    public void send() {
        final List<Object> packets = Lists.newArrayList();
        final List<Object> addPlayerList = Lists.newArrayList();
        final List<Object> updatePlayerList = Lists.newArrayList();

        try {
            final Object addPlayerPacket = PLAYER_INFO_CLASS.newInstance();
            final Object updatePlayerPacket = PLAYER_INFO_CLASS.newInstance();

            for (int i = 0; i < cells; i++) {
                if (profileCache[i] == null) {
                    profileCache[i] = gameProfileConstructor.newInstance(UUID.fromString(String.format(UUID_PATTERN, ChatUtils.appendDigit(i))), TOKEN + ChatUtils.appendDigit(i));
                }

                String text = this.putVars(tablistPattern.getOrDefault(i + 1, ""));
                Object gameProfile = profileCache[i];
                Object gameMode = ENUM_GAMEMODE_CLASS.getEnumConstants()[1];
                Object component = this.createBaseComponent(text, false);

                Object playerInfoData = playerInfoDataConstructor.newInstance(
                        null,
                        gameProfile,
                        ping,
                        gameMode,
                        component
                );

                if (firstPacket) {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (firstPacket) {
                firstPacket = false;
            }

            packets.add(addPlayerPacket);
            packets.add(updatePlayerPacket);

            ACTION_ENUM_FIELD.setAccessible(true);
            LIST_FIELD.setAccessible(true);
            HEADER_FIELD.setAccessible(true);
            FOOTER_FIELD.setAccessible(true);

            ACTION_ENUM_FIELD.set(addPlayerPacket, ADD_PLAYER);
            LIST_FIELD.set(addPlayerPacket, addPlayerList);
            ACTION_ENUM_FIELD.set(updatePlayerPacket, UPDATE_PLAYER);
            LIST_FIELD.set(updatePlayerPacket, updatePlayerList);

            Object header = this.createBaseComponent(this.putVars(super.header), true);
            Object footer = this.createBaseComponent(this.putVars(super.footer), true);

            if (this.shouldUseHeaderAndFooter()) {
                final Object headerFooterPacket = PLAYER_LIST_HEADER_FOOTER_CLASS.newInstance();
                HEADER_FIELD.set(headerFooterPacket, header);
                FOOTER_FIELD.set(headerFooterPacket, footer);
                packets.add(headerFooterPacket);
            }

            this.sendPackets(packets);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
