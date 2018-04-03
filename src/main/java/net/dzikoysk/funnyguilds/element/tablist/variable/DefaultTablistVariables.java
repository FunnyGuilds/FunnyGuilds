package net.dzikoysk.funnyguilds.element.tablist.variable;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.GuildDependentTablistVariable;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.SimpleTablistVariable;
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.TimeFormattedVariable;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.RandomUtils;
import net.dzikoysk.funnyguilds.util.Ticker;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

public final class DefaultTablistVariables {

    private static final Collection<Consumer<TablistVariablesParser>> installers = new ArrayList<>();

    private DefaultTablistVariables() {}

    public static void install(TablistVariablesParser parser) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();

        parser.add(new TimeFormattedVariable("HOUR", user -> Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        parser.add(new TimeFormattedVariable("MINUTE", user -> Calendar.getInstance().get(Calendar.MINUTE)));
        parser.add(new TimeFormattedVariable("SECOND", user -> Calendar.getInstance().get(Calendar.SECOND)));

        parser.add(new SimpleTablistVariable("PLAYER", User::getName));
        
        parser.add(new SimpleTablistVariable("GUILDS", user -> String.valueOf(GuildUtils.getGuilds().size())));
        parser.add(new SimpleTablistVariable("USERS", user -> String.valueOf(UserUtils.getUsers().size())));
        
        parser.add(new SimpleTablistVariable("PING-FORMAT", user -> IntegerRange.inRange(user.getPing(), config.pingFormat).replace("{PING}", String.valueOf(user.getPing()))));
        parser.add(new SimpleTablistVariable("PING", user -> String.valueOf(user.getPing())));
        parser.add(new SimpleTablistVariable("POINTS-FORMAT", user -> IntegerRange.inRange(user.getRank().getPoints(), config.pointsFormat).replace("{POINTS}", String.valueOf(user.getRank().getPoints()))));
        parser.add(new SimpleTablistVariable("POINTS", user -> String.valueOf(user.getRank().getPoints())));
        parser.add(new SimpleTablistVariable("POSITION", user -> String.valueOf(user.getRank().getPosition())));
        parser.add(new SimpleTablistVariable("KILLS", user -> String.valueOf(user.getRank().getKills())));
        parser.add(new SimpleTablistVariable("DEATHS", user -> String.valueOf(user.getRank().getDeaths())));
        parser.add(new SimpleTablistVariable("KDR", user -> String.format(Locale.US, "%.2f", user.getRank().getKDR())));
        parser.add(new SimpleTablistVariable("ONLINE", user -> user.getPlayer() == null ? "" : String.valueOf(Bukkit.getOnlinePlayers().stream().filter(p -> p != null && user.getPlayer().canSee(p)).count())));
        parser.add(new SimpleTablistVariable("TPS", user -> Ticker.getRecentTPS(0)));

        parser.add(new GuildDependentTablistVariable("G-NAME", user -> user.getGuild().getName(), user -> messages.gNameNoValue));
        parser.add(new GuildDependentTablistVariable("G-TAG", user -> user.getGuild().getTag(), user -> messages.gTagNoValue));
        parser.add(new GuildDependentTablistVariable("G-OWNER", user -> user.getGuild().getOwner().getName(), user -> messages.gOwnerNoValue));
        parser.add(new GuildDependentTablistVariable("G-DEPUTIES", user -> user.getGuild().getDeputies().isEmpty() ? messages.gDeputiesNoValue : StringUtils.toString(UserUtils.getNames(user.getGuild().getDeputies()), false), user -> messages.gDeputiesNoValue));
        parser.add(new GuildDependentTablistVariable("G-DEPUTY", user -> user.getGuild().getDeputies().isEmpty() ? messages.gDeputyNoValue : user.getGuild().getDeputies().get(RandomUtils.RANDOM_INSTANCE.nextInt(user.getGuild().getDeputies().size())).getName(), user -> messages.gDeputyNoValue));
        parser.add(new GuildDependentTablistVariable("G-LIVES", user -> String.valueOf(user.getGuild().getLives()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-ALLIES", user -> String.valueOf(user.getGuild().getAllies().size()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-POINTS-FORMAT", user -> IntegerRange.inRange(user.getGuild().getRank().getPoints(), config.pointsFormat).replace("{POINTS}", String.valueOf(user.getGuild().getRank().getPoints())), user -> IntegerRange.inRange(0, config.pointsFormat).replace("{POINTS}", "0")));
        parser.add(new GuildDependentTablistVariable("G-POINTS", user -> String.valueOf(user.getGuild().getRank().getPoints()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-KILLS", user -> String.valueOf(user.getGuild().getRank().getKills()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-DEATHS", user -> String.valueOf(user.getGuild().getRank().getDeaths()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-KDR", user -> String.format(Locale.US, "%.2f", user.getGuild().getRank().getKDR()), user -> "0.00"));
        parser.add(new GuildDependentTablistVariable("G-MEMBERS-ONLINE", user -> String.valueOf(user.getGuild().getOnlineMembers().size()), user -> "0"));
        parser.add(new GuildDependentTablistVariable("G-MEMBERS-ALL", user -> String.valueOf(user.getGuild().getMembers().size()), user -> "0"));

        parser.add(new GuildDependentTablistVariable("G-POSITION", user -> user.getGuild().getMembers().size() >= Settings.getConfig().minMembersToInclude ? String.valueOf(user.getGuild().getRank().getPosition()) : messages.minMembersToIncludeNoValue, user -> messages.minMembersToIncludeNoValue));
        parser.add(new GuildDependentTablistVariable("G-VALIDITY", user -> Settings.getConfig().dateFormat.format(user.getGuild().getValidityDate()), user -> messages.gValidityNoValue));
        parser.add(new GuildDependentTablistVariable("G-REGION-SIZE", user -> Settings.getConfig().regionsEnabled ? String.valueOf(user.getGuild().getRegionData().getSize()) : messages.gRegionSizeNoValue, user -> messages.gRegionSizeNoValue));

        for (Consumer<TablistVariablesParser> installer : installers) {
            installer.accept(parser);
        }
    }

    public static void registerInstaller(Consumer<TablistVariablesParser> parser) {
        installers.add(parser);
    }
    
}
