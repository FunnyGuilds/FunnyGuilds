package net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

public class TablistImpl extends AbstractTablist
{
    private static final Class<?> packetPlayOutPlayerInfoClass;
    private static final Class<?> packetPlayOutPlayerListHeaderFooterClass;
    private static final Class<?> playerInfoDataClass;
    private static final Class<?> gameProfileClass;
    private static final Class<?> enumGamemodeClass;
    private static final Class<?> iChatBaseComponentClass;

    private static final Field actionEnum;
    private static final Field listField;
    private static final Field headerField;
    private static final Field footerField;

    private static final Enum<?> addPlayer;
    private static final Enum<?> updatePlayer;

    private static Constructor<?> playerInfoDataConstructor;
    private static Constructor<?> gameProfileConstructor;

    private static final String uuid = "00000000-0000-%s-0000-000000000000";
    private static final String token = "!@#$^*";

    private final Object[] profileCache = new Object[80];

    static
    {
        packetPlayOutPlayerInfoClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
        packetPlayOutPlayerListHeaderFooterClass = Reflections.getCraftClass("PacketPlayOutPlayerListHeaderFooter");
        playerInfoDataClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
        gameProfileClass = Reflections.getClass("com.mojang.authlib.GameProfile");
        enumGamemodeClass = Reflections.getCraftClass("WorldSettings$EnumGamemode");
        iChatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");

        actionEnum = Reflections.getField(packetPlayOutPlayerInfoClass, "a");
        listField = Reflections.getField(packetPlayOutPlayerInfoClass, "b");
        headerField = Reflections.getField(packetPlayOutPlayerListHeaderFooterClass, "a");
        footerField = Reflections.getField(packetPlayOutPlayerListHeaderFooterClass, "b");

        addPlayer = (Enum<?>) Reflections.getCraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumConstants()[0];
        updatePlayer = (Enum<?>) Reflections.getCraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumConstants()[3];

        try
        {
            playerInfoDataConstructor = playerInfoDataClass.getConstructor(
                    packetPlayOutPlayerInfoClass,
                    gameProfileClass,
                    int.class,
                    enumGamemodeClass,
                    iChatBaseComponentClass
            );
            gameProfileConstructor = gameProfileClass.getConstructor(
                    UUID.class,
                    String.class
            );
        }
        catch (final NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }

    }

    public TablistImpl(final List<String> tablistPattern, final String header, final String footer, final Player player)
    {
        super(tablistPattern, header, footer, player);
    }

    @Override
    public void send()
    {
        final List<Object> packets = Lists.newArrayList();
        final List<Object> addPlayerList = Lists.newArrayList();
        final List<Object> updatePlayerList = Lists.newArrayList();

        try
        {
            final Object addPlayerPacket = packetPlayOutPlayerInfoClass.newInstance();
            final Object updatePlayerPacket = packetPlayOutPlayerInfoClass.newInstance();
            final Object headerFooterPacket = packetPlayOutPlayerListHeaderFooterClass.newInstance();

            for (int i = 0; i < 80; i++)
            {
                if (profileCache[i] == null)
                {
                    profileCache[i] = gameProfileConstructor.newInstance(UUID.fromString(String.format(uuid, StringUtils.appendDigit(i))), token + StringUtils.appendDigit(i));
                }

                String text = i < tablistPattern.size() ? this.putVars(tablistPattern.get(i)) : "";
                Object gameProfile = profileCache[i];
                Object gameMode = enumGamemodeClass.getEnumConstants()[1];
                Object component = this.createBaseComponent(text);

                Object playerInfoData = playerInfoDataConstructor.newInstance(
                        null,
                        gameProfile,
                        0,
                        gameMode,
                        component
                );

                if (firstPacket)
                {
                    addPlayerList.add(playerInfoData);
                }

                updatePlayerList.add(playerInfoData);
            }

            if (firstPacket)
            {
                firstPacket = false;
            }

            packets.add(addPlayerPacket);
            packets.add(updatePlayerPacket);
            packets.add(headerFooterPacket);

            actionEnum.setAccessible(true);
            listField.setAccessible(true);
            headerField.setAccessible(true);
            footerField.setAccessible(true);

            actionEnum.set(addPlayerPacket, addPlayer);
            listField.set(addPlayerPacket, addPlayerList);
            actionEnum.set(updatePlayerPacket, updatePlayer);
            listField.set(updatePlayerPacket, updatePlayerList);

            Object header = this.createBaseComponent(this.putHeaderFooterVars(super.header));
            Object footer = this.createBaseComponent(this.putHeaderFooterVars(super.footer));

            headerField.set(headerFooterPacket, header);
            footerField.set(headerFooterPacket, footer);

            this.sendPackets(packets);
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException ex)
        {
            ex.printStackTrace();
        }
    }
}
