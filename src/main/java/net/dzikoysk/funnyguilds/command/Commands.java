package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.admin.*;
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
        
        new ExecutorCaller(new ExcFunnyGuilds(), commands.funnyguilds.name, null, commands.funnyguilds.aliases, false);
        new ExecutorCaller(new ExcCreate(), commands.create.name, "funnyguilds.create", commands.create.aliases, true);
        new ExecutorCaller(new ExcDelete(), commands.delete.name, "funnyguilds.delete", commands.delete.aliases, true);
        new ExecutorCaller(new ExcConfirm(), commands.confirm.name, "funnyguilds.delete", commands.confirm.aliases, true);
        new ExecutorCaller(new ExcInvite(), commands.invite.name, "funnyguilds.invite", commands.invite.aliases, true);
        new ExecutorCaller(new ExcJoin(), commands.join.name, "funnyguilds.join", commands.join.aliases, true);
        new ExecutorCaller(new ExcLeave(), commands.leave.name, "funnyguilds.leave", commands.leave.aliases, true);
        new ExecutorCaller(new ExcKick(), commands.kick.name, "funnyguilds.kick", commands.kick.aliases, true);
        new ExecutorCaller(new ExcBase(), commands.base.name, "funnyguilds.base", commands.base.aliases, true);
        new ExecutorCaller(new ExcEnlarge(), commands.enlarge.name, "funnyguilds.enlarge", commands.enlarge.aliases, true);
        new ExecutorCaller(new ExcGuild(), commands.guild.name, "funnyguilds.help", commands.guild.aliases, false);
        new ExecutorCaller(new ExcAlly(), commands.ally.name, "funnyguilds.ally", commands.ally.aliases, true);
        new ExecutorCaller(new ExcBreak(), commands.break_.name, "funnyguilds.break", commands.break_.aliases, true);
        new ExecutorCaller(new ExcPlayer(), commands.player.name, "funnyguilds.player", commands.player.aliases, false);
        new ExecutorCaller(new ExcInfo(), commands.info.name, "funnyguilds.info", commands.info.aliases, false);
        new ExecutorCaller(new ExcTop(), commands.top.name, "funnyguilds.top", commands.top.aliases, false);
        new ExecutorCaller(new ExcValidity(), commands.validity.name, "funnyguilds.validity", commands.validity.aliases, true);
        new ExecutorCaller(new ExcLeader(), commands.leader.name, "funnyguilds.leader", commands.leader.aliases, true);
        new ExecutorCaller(new ExcDeputy(), commands.deputy.name, "funnyguilds.deputy", commands.deputy.aliases, true);
        new ExecutorCaller(new ExcRanking(), commands.ranking.name, "funnyguilds.ranking", commands.ranking.aliases, false);
        new ExecutorCaller(new ExcItems(), commands.items.name, "funnyguilds.items", commands.items.aliases, true);
        new ExecutorCaller(new ExcEscape(), commands.escape.name, "funnyguilds.escape", commands.escape.aliases, true);

        new ExecutorCaller(new MxcPvP(), commands.pvp.name, "funnyguilds.manage", commands.pvp.aliases, true);
        new ExecutorCaller(new MxcBase(), commands.setbase.name, "funnyguilds.manage", commands.setbase.aliases, true);

        new ExecutorCaller(new AxcMain(), commands.admin.main, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcAdd(), commands.admin.add, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcDelete(), commands.admin.delete, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcKick(), commands.admin.kick, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcTeleport(), commands.admin.teleport, "funnyguilds.admin", null, true);
        new ExecutorCaller(new AxcPoints(), commands.admin.points, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcKills(), commands.admin.kills, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcDeaths(), commands.admin.deaths, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcBan(), commands.admin.ban, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcUnban(), commands.admin.unban, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcLives(), commands.admin.lives, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcMove(), commands.admin.move, "funnyguilds.admin", null, true);
        new ExecutorCaller(new AxcValidity(), commands.admin.validity, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcName(), commands.admin.name, "funnyguilds.admin", null, false);
        new ExecutorCaller(new AxcSpy(), commands.admin.spy, "funnyguilds.admin", null, true);
    }
}
