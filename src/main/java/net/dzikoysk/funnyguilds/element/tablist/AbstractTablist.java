package net.dzikoysk.funnyguilds.element.tablist;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import net.dzikoysk.funnyguilds.element.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariablesParser;
import net.dzikoysk.funnyguilds.element.tablist.variable.VariableParsingResult;
import net.dzikoysk.funnyguilds.util.reflect.PacketSender;
import net.dzikoysk.funnyguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTablist {

    private static final Set<AbstractTablist> TABLIST_CACHE = ConcurrentHashMap.newKeySet();

    protected final TablistVariablesParser variables = new TablistVariablesParser();
    protected final Map<Integer, String> tablistPattern;
    protected final String header;
    protected final String footer;
    protected final UUID player;
    protected final int ping;
    protected boolean firstPacket = true;

    public AbstractTablist(Map<Integer, String> tablistPattern, String header, String footer, int ping, Player player) {
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

    public static AbstractTablist createTablist(Map<Integer, String> pattern, String header, String footer, int ping, Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        switch (Reflections.getFixedVersion()) {
            case "v1_8_R1": {
                AbstractTablist tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_8_R1.TablistImpl(pattern, header, footer, ping, player);
                TABLIST_CACHE.add(tablist);

                return tablist;
            }
            case "v1_8_R2":
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2": {
                AbstractTablist tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_8_R3.TablistImpl(pattern, header, footer, ping, player);
                TABLIST_CACHE.add(tablist);

                return tablist;
            }
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1": {
                AbstractTablist tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_10_R1.TablistImpl(pattern, header, footer, ping, player);
                TABLIST_CACHE.add(tablist);

                return tablist;
            }
            default:
                throw new RuntimeException("Could not find tablist for given version.");
        }
    }

    public static AbstractTablist getTablist(Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return tablist;
            }
        }

        throw new IllegalStateException("Given player's tablist does not exist!");
    }

    public static void removeTablist(Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                TABLIST_CACHE.remove(tablist);
                break;
            }
        }
    }

    public static boolean hasTablist(Player player) {
        for (AbstractTablist tablist : TABLIST_CACHE) {
            if (tablist.player.equals(player.getUniqueId())) {
                return true;
            }
        }
        
        return false;
    }

    public abstract void send();

    protected void sendPackets(List<Object> packets) {
        Player target = Bukkit.getPlayer(player);
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
        User user = User.get(player);

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

        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int second = time.get(Calendar.SECOND);

        formatted = StringUtils.replace(formatted, "{HOUR}", (hour < 10 ? "0" : "") + hour);
        formatted = StringUtils.replace(formatted, "{MINUTE}", (minute < 10 ? "0" : "") + minute);
        formatted = StringUtils.replace(formatted, "{SECOND}", (second < 10 ? "0" : "") + second);
        formatted = StringUtils.colored(formatted);

        return formatted;
    }

    protected boolean shouldUseHeaderAndFooter() {
        return !this.header.isEmpty() || !this.footer.isEmpty();
    }
    
}
