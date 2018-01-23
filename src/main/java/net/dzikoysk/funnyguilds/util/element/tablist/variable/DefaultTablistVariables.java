package net.dzikoysk.funnyguilds.util.element.tablist.variable;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.impl.GuildDependentTablistVariable;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.impl.SimpleTablistVariable;
import net.dzikoysk.funnyguilds.util.element.tablist.variable.impl.TimeFormattedVariable;
import net.dzikoysk.funnyguilds.util.runnable.Ticker;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

public final class DefaultTablistVariables {

    private static final Collection<Consumer<TablistVariablesParser>> installers = new ArrayList<>();

    private DefaultTablistVariables() {
    }

    public static void install(TablistVariablesParser parser) {

        parser.add(new TimeFormattedVariable("HOUR", (user) -> Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        parser.add(new TimeFormattedVariable("MINUTE", (user) -> Calendar.getInstance().get(Calendar.MINUTE)));
        parser.add(new TimeFormattedVariable("SECOND", (user) -> Calendar.getInstance().get(Calendar.SECOND)));

        parser.add(new SimpleTablistVariable("PLAYER", User::getName));
        parser.add(new SimpleTablistVariable("PING", (user) -> String.valueOf(user.getPing())));
        parser.add(new SimpleTablistVariable("POINTS", (user) -> String.valueOf(user.getRank().getPoints())));
        parser.add(new SimpleTablistVariable("POSITION", (user) -> String.valueOf(user.getRank().getPosition())));
        parser.add(new SimpleTablistVariable("KILLS", (user) -> String.valueOf(user.getRank().getKills())));
        parser.add(new SimpleTablistVariable("DEATHS", (user) -> String.valueOf(user.getRank().getDeaths())));
        parser.add(new SimpleTablistVariable("KDR", (user) -> String.format(Locale.US, "%.2f", user.getRank().getKDR())));
        parser.add(new SimpleTablistVariable("ONLINE", (user) -> String.valueOf(Bukkit.getOnlinePlayers().size())));
        parser.add(new SimpleTablistVariable("TPS", (user) -> Ticker.getRecentTPS(0)));

        parser.add(new GuildDependentTablistVariable("G-NAME", (user) -> user.getGuild().getName(), (user) -> "Brak"));
        parser.add(new GuildDependentTablistVariable("G-TAG", (user) -> user.getGuild().getTag(), (user) -> "Brak"));
        parser.add(new GuildDependentTablistVariable("G-OWNER", (user) -> user.getGuild().getOwner().getName(), (user) -> "Brak"));
        parser.add(new GuildDependentTablistVariable("G-LIVES", (user) -> String.valueOf(user.getGuild().getLives()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-ALLIES", (user) -> String.valueOf(user.getGuild().getAllies().size()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-POINTS", (user) -> String.valueOf(user.getGuild().getRank().getPoints()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-KILLS", (user) -> String.valueOf(user.getGuild().getRank().getKills()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-DEATHS", (user) -> String.valueOf(user.getGuild().getRank().getDeaths()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-KDR", (user) -> String.format(Locale.US, "%.2f", user.getGuild().getRank().getKDR()), (user) -> "0.0"));
        parser.add(new GuildDependentTablistVariable("G-MEMBERS-ONLINE", (user) -> String.valueOf(user.getGuild().getOnlineMembers().size()), (user) -> "0"));
        parser.add(new GuildDependentTablistVariable("G-MEMBERS-ALL", (user) -> String.valueOf(user.getGuild().getMembers().size()), (user) -> "0"));

        parser.add(new GuildDependentTablistVariable("G-POSITION", (user) -> user.getGuild().getMembers().size() >= Settings.getConfig().minMembersToInclude ? String.valueOf(user.getGuild().getRank().getPosition()) : Settings.getConfig().minMembersPositionString, (user) -> Settings.getConfig().minMembersPositionString));

        for (Consumer<TablistVariablesParser> installer : installers) {
            installer.accept(parser);
        }
    }

    public static void registerInstaller(Consumer<TablistVariablesParser> parser) {
        installers.add(parser);
    }
}
