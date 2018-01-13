package net.dzikoysk.funnyguilds.util.element.tablist;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.element.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.TablistVariablesParser;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.VariableParsingResult;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTablist {

    private static final Set<AbstractTablist> TABLIST_CACHE = new HashSet<>();

    protected final TablistVariablesParser variables = new TablistVariablesParser();
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

        DefaultTablistVariables.install(this.variables);
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
        if ("v1_8_R2".equals(Reflections.getFixedVersion()) || "v1_8_R3".equals(Reflections.getFixedVersion()) || "v1_9_R1".equals(Reflections.getFixedVersion()) || "v1_9_R2".equals(Reflections.getFixedVersion())) {
            final AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_8_R3.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        } else if ("v1_10_R1".equals(Reflections.getFixedVersion()) || "v1_11_R1".equals(Reflections.getFixedVersion()) || "v1_12_R1".equals(Reflections.getFixedVersion())) {
            final AbstractTablist tablist = new net.dzikoysk.funnyguilds.util.element.tablist.impl.v1_10_R1.TablistImpl(pattern, header, footer, ping, player);
            TABLIST_CACHE.add(tablist);

            return tablist;
        } else {
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

        VariableParsingResult result = this.variables.createResultFor(user);
        formatted = result.replaceInString(formatted);

        formatted = StringUtils.colored(formatted);

        String temp = Parser.parseRank(formatted); // TODO
        if (temp != null) {
            formatted = temp;
        }

        return formatted;
    }

    @Deprecated
    protected String putHeaderFooterVars(String text) {
        String formatted = text;

        final Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        if (hour < 10) {
            formatted = StringUtils.replace(formatted, "{HOUR}", "0" + String.valueOf(hour));
        } else {
            formatted = StringUtils.replace(formatted, "{HOUR}", String.valueOf(hour));
        }
        if (minute < 10) {
            formatted = StringUtils.replace(formatted, "{MINUTE}", "0" + String.valueOf(minute));
        } else {
            formatted = StringUtils.replace(formatted, "{MINUTE}", String.valueOf(minute));
        }
        if (second < 10) {
            formatted = StringUtils.replace(formatted, "{SECOND}", "0" + String.valueOf(second));
        } else {
            formatted = StringUtils.replace(formatted, "{SECOND}", String.valueOf(second));
        }

        formatted = StringUtils.colored(formatted);

        return formatted;
    }

    protected boolean shouldUseHeaderAndFooter() {
        return !this.header.isEmpty() || !this.footer.isEmpty();
    }
}
