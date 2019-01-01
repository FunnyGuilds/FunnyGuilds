package net.dzikoysk.funnyguilds.element.tablist;

import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.notification.NotificationUtil;
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables;
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariablesParser;
import net.dzikoysk.funnyguilds.element.tablist.variable.VariableParsingResult;
import net.dzikoysk.funnyguilds.hook.MVdWPlaceholderAPIHook;
import net.dzikoysk.funnyguilds.hook.PlaceholderAPIHook;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.MapUtil;
import net.dzikoysk.funnyguilds.util.nms.PacketSender;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTablist {

    public final static int DEFAULT_CELLS_AMOUNT = 80;

    private static final Map<UUID, AbstractTablist> TABLIST_CACHE = new ConcurrentHashMap<>();

    protected final TablistVariablesParser variables = new TablistVariablesParser();
    protected final Map<Integer, String> tablistPattern;
    protected final String header;
    protected final String footer;
    protected final User user;
    protected final int cells;
    protected final int ping;
    protected boolean firstPacket = true;

    public AbstractTablist(Map<Integer, String> tablistPattern, String header, String footer, int ping, User user) {
        this.tablistPattern = tablistPattern;
        this.header = header;
        this.footer = footer;
        this.ping = ping;
        this.user = user;

        PluginConfig config = Settings.getConfig();

        if (!config.playerlistFillCells) {
            Entry<Integer, String> entry = MapUtil.findTheMaximumEntryByKey(config.playerList);

            if (entry != null) {
                this.cells = entry.getKey();
            }
            else {
                this.cells = DEFAULT_CELLS_AMOUNT;
            }
        }
        else {
            this.cells = DEFAULT_CELLS_AMOUNT;
        }

        DefaultTablistVariables.install(this.variables);
    }

    public static void wipeCache() {
        TABLIST_CACHE.clear();
        DefaultTablistVariables.clearFunnyVariables();
    }

    public static AbstractTablist createTablist(Map<Integer, String> pattern, String header, String footer, int ping, Player player) {
        return createTablist(pattern, header, footer, ping, User.get(player));
    }
    
    public static AbstractTablist createTablist(Map<Integer, String> pattern, String header, String footer, int ping, User user) {
        AbstractTablist tablist = TABLIST_CACHE.get(user.getUUID());

        if (tablist != null) {
            return tablist;
        }

        switch (Reflections.SERVER_VERSION) {
            case "v1_8_R1":
                tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_8_R1.TablistImpl(pattern, header, footer, ping, user);
                break;
            case "v1_8_R2":
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
                tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_8_R3.TablistImpl(pattern, header, footer, ping, user);
                break;
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
            case "v1_13_R1":
                tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_10_R1.TablistImpl(pattern, header, footer, ping, user);
                break;
            case "v1_13_R2":
                tablist = new net.dzikoysk.funnyguilds.element.tablist.impl.v1_13_R2.TablistImpl(pattern, header, footer, ping, user);
                break;
            default:
                throw new RuntimeException("Could not find tablist for given version.");
        }

        TABLIST_CACHE.put(user.getUUID(), tablist);

        return tablist;
    }

    public static AbstractTablist getTablist(Player player) {
        AbstractTablist tablist = TABLIST_CACHE.get(player.getUniqueId());
        if (tablist != null) {
            return tablist;
        }

        throw new IllegalStateException("Given player's tablist does not exist!");
    }

    public static void removeTablist(Player player) {
        TABLIST_CACHE.remove(player.getUniqueId());
    }

    public static boolean hasTablist(Player player) {
        return TABLIST_CACHE.containsKey(player.getUniqueId());
    }

    public abstract void send();

    protected void sendPackets(List<Object> packets) {
        Player target = user.getPlayer();

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

        if (user == null) {
            throw new IllegalStateException("Given player is null!");
        }

        VariableParsingResult result = this.variables.createResultFor(user);
        formatted = result.replaceInString(formatted);
        formatted = ChatUtils.colored(formatted);

        String temp = RankUtils.parseRank(formatted);
        if (temp != null) {
            formatted = temp;
        }

        if (PluginHook.isPresent(PluginHook.PLUGIN_PLACEHOLDERAPI)) {
            formatted = PlaceholderAPIHook.replacePlaceholders(user.getPlayer(), formatted);
        }
        
        if (PluginHook.isPresent(PluginHook.PLUGIN_MVDWPLACEHOLDERAPI)) {
            formatted = MVdWPlaceholderAPIHook.replacePlaceholders(user.getPlayer(), formatted);
        }
        
        return formatted;
    }

    @Deprecated
    protected String putHeaderFooterVars(String text) {
        String formatted = text;

        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        formatted = StringUtils.replace(formatted, "{HOUR}", ChatUtils.appendDigit(hour));
        formatted = StringUtils.replace(formatted, "{MINUTE}", ChatUtils.appendDigit(minute));
        formatted = StringUtils.replace(formatted, "{SECOND}", ChatUtils.appendDigit(second));
        formatted = ChatUtils.colored(formatted);

        return formatted;
    }

    protected boolean shouldUseHeaderAndFooter() {
        return !this.header.isEmpty() || !this.footer.isEmpty();
    }
    
}
