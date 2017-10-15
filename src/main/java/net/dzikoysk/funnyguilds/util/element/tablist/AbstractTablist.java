package net.dzikoysk.funnyguilds.util.element.tablist;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.NotificationUtil;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import net.dzikoysk.funnyguilds.util.runnable.Ticker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTablist {
    private static final Set<AbstractTablist> TABLIST_CACHE = new HashSet<>();

    private static final Method getHandle;
    private static final Method sendPacket;
    private static final Method createBaseComponent;

    private static final Field playerConnection;

    static {
        getHandle = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftPlayer"), "getHandle");
        sendPacket = Reflections.getMethod(Reflections.getCraftClass("PlayerConnection"), "sendPacket");
        createBaseComponent = Reflections.getMethod(Reflections.getBukkitClass("util.CraftChatMessage"), "fromString", String.class, boolean.class);

        playerConnection = Reflections.getField(Reflections.getCraftClass("EntityPlayer"), "playerConnection");
    }

    protected final Map<Integer, String> tablistPattern;
    protected final String header;
    protected final String footer;
    protected final UUID player;
    protected final int ping;
    protected boolean firstPacket = true;


    public AbstractTablist(final Map<Integer, String> tablistPattern, final String header, final String footer, final int ping, final Player player) {
        this.tablistPattern = tablistPattern;
        this.header = header;
        this.footer = footer;
        this.ping = ping;
        this.player = player.getUniqueId();
    }

    public static void wipeCache() {
        TABLIST_CACHE.clear();
    }

    public static AbstractTablist createTablist(final Map<Integer, String> pattern, final String header, final String footer, final int ping, final Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        if ("v1_8_R1".equals(Reflections.getFixedVersion())) {
            final AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R1.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        }
        if ("v1_8_R2".equals(Reflections.getFixedVersion()) || "v1_8_R3".equals(Reflections.getFixedVersion()) ||
                "v1_9_R1".equals(Reflections.getFixedVersion()) || "v1_9_R2".equals(Reflections.getFixedVersion())) {
            final AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R3.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        }
        else if ("v1_10_R1".equals(Reflections.getFixedVersion()) || "v1_11_R1".equals(Reflections.getFixedVersion()) || "v1_12_R1".equals(Reflections.getFixedVersion())) {
            final AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_10_R1.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        }
        else {
            throw new RuntimeException("Could not find tablist for given version.");
        }
    }

    public static AbstractTablist getTablist(final Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        throw new IllegalStateException("Given player's tablist does not exist!");
    }

    public static void removeTablist(final Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                TABLIST_CACHE.remove(tablist);
                break;
            }
        }
    }

    public static boolean hasTablist(final Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public abstract void send();

    protected void sendPackets(final List<Object> packets) {
        final Player target = Bukkit.getPlayer(player);

        if (target == null) {
            return;
        }

        PacketSender.sendPacket(target, packets);
    }

    protected Object createBaseComponent(String text, boolean keepNewLines) {
        return NotificationUtil.createBaseComponent(text, keepNewLines);
    }

    protected String putVars(String cell) {
        String formatted = cell;
        final User user = User.get(player);

        if (user == null) {
            throw new IllegalStateException("Given player is null!");
        }
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);
        if (hour < 10) {
            formatted = StringUtils.replace(formatted, "{HOUR}", "0" + String.valueOf(hour));
        }
        else {
            formatted = StringUtils.replace(formatted, "{HOUR}", String.valueOf(hour));
        }
        if (minute < 10) {
            formatted = StringUtils.replace(formatted, "{MINUTE}", "0" + String.valueOf(minute));
        }
        else {
            formatted = StringUtils.replace(formatted, "{MINUTE}", String.valueOf(minute));
        }
        if (second < 10) {
            formatted = StringUtils.replace(formatted, "{SECOND}", "0" + String.valueOf(second));
        }
        else {
            formatted = StringUtils.replace(formatted, "{SECOND}", String.valueOf(second));
        }

        if (user.hasGuild()) {
            formatted = StringUtils.replace(formatted, "{GUILD}", user.getGuild().getName());
            formatted = StringUtils.replace(formatted, "{TAG}", user.getGuild().getTag());
            formatted = StringUtils.replace(formatted, "{G-OWNER}", user.getGuild().getOwner().getName());
            formatted = StringUtils.replace(formatted, "{G-LIVES}", String.valueOf(user.getGuild().getLives()));
            formatted = StringUtils.replace(formatted, "{G-ALLIES}", String.valueOf(
                    (user.getGuild()).getAllies().size()));
            formatted = StringUtils.replace(formatted, "{G-POINTS}", String.valueOf(
                    user.getGuild().getRank().getPoints()));
            formatted = StringUtils.replace(formatted, "{G-KILLS}", String.valueOf(
                    user.getGuild().getRank().getKills()));
            formatted = StringUtils.replace(formatted, "{G-DEATHS}", String.valueOf(
                    user.getGuild().getRank().getDeaths()));
        }
        else {
            formatted = StringUtils.replace(formatted, "{GUILD}", "Brak");
            formatted = StringUtils.replace(formatted, "{TAG}", "Brak");
            formatted = StringUtils.replace(formatted, "{G-OWNER}", "Brak");
            formatted = StringUtils.replace(formatted, "{G-LIVES}", "0");
            formatted = StringUtils.replace(formatted, "{G-ALLIES}", "0");
            formatted = StringUtils.replace(formatted, "{G-POINTS}", "0");
            formatted = StringUtils.replace(formatted, "{G-KILLS}", "0");
            formatted = StringUtils.replace(formatted, "{G-DEATHS}", "0");
        }

        formatted = StringUtils.replace(formatted, "{PLAYER}", user.getName());
        formatted = StringUtils.replace(formatted, "{PING}", String.valueOf((double) user.getPing()));
        formatted = StringUtils.replace(formatted, "{POINTS}", String.valueOf(user.getRank().getPoints()));
        formatted = StringUtils.replace(formatted, "{KILLS}", String.valueOf(user.getRank().getKills()));
        formatted = StringUtils.replace(formatted, "{DEATHS}", String.valueOf(user.getRank().getDeaths()));

        formatted = StringUtils.replace(formatted, "{ONLINE}", String.valueOf(
                Bukkit.getOnlinePlayers().size()));
        formatted = StringUtils.replace(formatted, "{TPS}", Ticker.getRecentTPS(0));

        formatted = StringUtils.colored(formatted);

        String temp = Parser.parseRank(formatted);
        if (temp != null) {
            formatted = temp;
        }

        return formatted;
    }

    protected String putHeaderFooterVars(String text) {
        String formatted = text;

        final Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        if (hour < 10) {
            formatted = StringUtils.replace(formatted, "{HOUR}", "0" + String.valueOf(hour));
        }
        else {
            formatted = StringUtils.replace(formatted, "{HOUR}", String.valueOf(hour));
        }
        if (minute < 10) {
            formatted = StringUtils.replace(formatted, "{MINUTE}", "0" + String.valueOf(minute));
        }
        else {
            formatted = StringUtils.replace(formatted, "{MINUTE}", String.valueOf(minute));
        }
        if (second < 10) {
            formatted = StringUtils.replace(formatted, "{SECOND}", "0" + String.valueOf(second));
        }
        else {
            formatted = StringUtils.replace(formatted, "{SECOND}", String.valueOf(second));
        }

        formatted = StringUtils.colored(formatted);

        return formatted;
    }

    protected boolean shouldUseHeaderAndFooter() {
        return !this.header.isEmpty() || !this.footer.isEmpty();
    }
}
