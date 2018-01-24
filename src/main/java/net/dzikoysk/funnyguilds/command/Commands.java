package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.admin.AxcAdd;
import net.dzikoysk.funnyguilds.command.admin.AxcBan;
import net.dzikoysk.funnyguilds.command.admin.AxcDeaths;
import net.dzikoysk.funnyguilds.command.admin.AxcDelete;
import net.dzikoysk.funnyguilds.command.admin.AxcDeputy;
import net.dzikoysk.funnyguilds.command.admin.AxcEnabled;
import net.dzikoysk.funnyguilds.command.admin.AxcKick;
import net.dzikoysk.funnyguilds.command.admin.AxcKills;
import net.dzikoysk.funnyguilds.command.admin.AxcLeader;
import net.dzikoysk.funnyguilds.command.admin.AxcLives;
import net.dzikoysk.funnyguilds.command.admin.AxcMain;
import net.dzikoysk.funnyguilds.command.admin.AxcMove;
import net.dzikoysk.funnyguilds.command.admin.AxcName;
import net.dzikoysk.funnyguilds.command.admin.AxcPoints;
import net.dzikoysk.funnyguilds.command.admin.AxcSpy;
import net.dzikoysk.funnyguilds.command.admin.AxcTeleport;
import net.dzikoysk.funnyguilds.command.admin.AxcUnban;
import net.dzikoysk.funnyguilds.command.admin.AxcValidity;
import net.dzikoysk.funnyguilds.command.manager.MxcBase;
import net.dzikoysk.funnyguilds.command.manager.MxcPvP;
import net.dzikoysk.funnyguilds.command.util.ExecutorCaller;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

public class Commands {

    private static Commands instance;

    public Commands() {
        instance = this;
    }

    public static Commands getInstance() {
        if (instance == null) {
            return new Commands();
        }
        
        return instance;
    }

    public void register() {
        PluginConfig.Commands commands = Settings.getConfig().commands;
        
        new ExecutorCaller(new ExcFunnyGuilds(), null, commands.funnyguilds, false);
        new ExecutorCaller(new ExcCreate(), "funnyguilds.create", commands.create, true);
        new ExecutorCaller(new ExcDelete(), "funnyguilds.delete", commands.delete, true);
        new ExecutorCaller(new ExcConfirm(), "funnyguilds.delete", commands.confirm, true);
        new ExecutorCaller(new ExcInvite(), "funnyguilds.invite", commands.invite, true);
        new ExecutorCaller(new ExcJoin(), "funnyguilds.join", commands.join, true);
        new ExecutorCaller(new ExcLeave(), "funnyguilds.leave", commands.leave, true);
        new ExecutorCaller(new ExcKick(), "funnyguilds.kick", commands.kick, true);
        new ExecutorCaller(new ExcBase(), "funnyguilds.base", commands.base, true);
        new ExecutorCaller(new ExcEnlarge(), "funnyguilds.enlarge", commands.enlarge, true);
        new ExecutorCaller(new ExcGuild(), "funnyguilds.help", commands.guild, false);
        new ExecutorCaller(new ExcAlly(), "funnyguilds.ally", commands.ally, true);
        new ExecutorCaller(new ExcBreak(), "funnyguilds.break", commands.break_, true);
        new ExecutorCaller(new ExcPlayer(), "funnyguilds.player", commands.player, false);
        new ExecutorCaller(new ExcInfo(), "funnyguilds.info", commands.info, false);
        new ExecutorCaller(new ExcTop(), "funnyguilds.top", commands.top, false);
        new ExecutorCaller(new ExcValidity(), "funnyguilds.validity", commands.validity, true);
        new ExecutorCaller(new ExcLeader(), "funnyguilds.leader", commands.leader, true);
        new ExecutorCaller(new ExcDeputy(), "funnyguilds.deputy", commands.deputy, true);
        new ExecutorCaller(new ExcRanking(), "funnyguilds.ranking", commands.ranking, false);
        new ExecutorCaller(new ExcItems(), "funnyguilds.items", commands.items, true);
        new ExecutorCaller(new ExcEscape(), "funnyguilds.escape", commands.escape, true);
        new ExecutorCaller(new ExcRankReset(), "funnyguilds.rankreset", commands.rankReset, true);

        new ExecutorCaller(new MxcPvP(), "funnyguilds.manage", commands.pvp, true);
        new ExecutorCaller(new MxcBase(), "funnyguilds.manage", commands.setbase, true);

        new ExecutorCaller(new AxcMain(), commands.admin.main, null, true, false);
        new ExecutorCaller(new AxcAdd(), commands.admin.add, null, true, false);
        new ExecutorCaller(new AxcDelete(), commands.admin.delete, null, true, false);
        new ExecutorCaller(new AxcKick(), commands.admin.kick, null, true, false);
        new ExecutorCaller(new AxcTeleport(), commands.admin.teleport, null, true, true);
        new ExecutorCaller(new AxcPoints(), commands.admin.points, null, true, false);
        new ExecutorCaller(new AxcKills(), commands.admin.kills, null, true, false);
        new ExecutorCaller(new AxcDeaths(), commands.admin.deaths, null, true, false);
        new ExecutorCaller(new AxcBan(), commands.admin.ban, null, true, false);
        new ExecutorCaller(new AxcUnban(), commands.admin.unban, null, true, false);
        new ExecutorCaller(new AxcLives(), commands.admin.lives, null, true, false);
        new ExecutorCaller(new AxcMove(), commands.admin.move, null, true, true);
        new ExecutorCaller(new AxcValidity(), commands.admin.validity, null, true, false);
        new ExecutorCaller(new AxcName(), commands.admin.name, null, true, false);
        new ExecutorCaller(new AxcSpy(), commands.admin.spy, null, true, true);
        new ExecutorCaller(new AxcEnabled(), commands.admin.enabled, null, true, false);
        new ExecutorCaller(new AxcLeader(), commands.admin.leader, null, true, false);
        new ExecutorCaller(new AxcDeputy(), commands.admin.deputy, null, true, false);
    }
}
