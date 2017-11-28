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
        final PluginConfig.Commands commands = Settings.getConfig().commands;
        new ExecutorCaller(new ExcFunnyGuilds(), commands.funnyguilds.name, null, commands.funnyguilds.aliases, false);
        new ExecutorCaller(new ExcCreate(), commands.create.name, "funnyguilds.create", commands.create.aliases);
        new ExecutorCaller(new ExcDelete(), commands.delete.name, "funnyguilds.delete", commands.delete.aliases);
        new ExecutorCaller(new ExcConfirm(), commands.confirm.name, "funnyguilds.delete", commands.confirm.aliases);
        new ExecutorCaller(new ExcInvite(), commands.invite.name, "funnyguilds.invite", commands.invite.aliases);
        new ExecutorCaller(new ExcJoin(), commands.join.name, "funnyguilds.join", commands.join.aliases);
        new ExecutorCaller(new ExcLeave(), commands.leave.name, "funnyguilds.leave", commands.leave.aliases);
        new ExecutorCaller(new ExcKick(), commands.kick.name, "funnyguilds.kick", commands.kick.aliases);
        new ExecutorCaller(new ExcBase(), commands.base.name, "funnyguilds.base", commands.base.aliases);
        new ExecutorCaller(new ExcEnlarge(), commands.enlarge.name, "funnyguilds.enlarge", commands.enlarge.aliases);
        new ExecutorCaller(new ExcGuild(), commands.guild.name, "funnyguilds.help", commands.guild.aliases);
        new ExecutorCaller(new ExcAlly(), commands.ally.name, "funnyguilds.ally", commands.ally.aliases);
        new ExecutorCaller(new ExcBreak(), commands.break_.name, "funnyguilds.break", commands.break_.aliases);
        new ExecutorCaller(new ExcPlayer(), commands.player.name, "funnyguilds.player", commands.player.aliases);
        new ExecutorCaller(new ExcInfo(), commands.info.name, "funnyguilds.info", commands.info.aliases);
        new ExecutorCaller(new ExcTop(), commands.top.name, "funnyguilds.top", commands.top.aliases);
        new ExecutorCaller(new ExcValidity(), commands.validity.name, "funnyguilds.validity", commands.validity.aliases);
        new ExecutorCaller(new ExcLeader(), commands.leader.name, "funnyguilds.leader", commands.leader.aliases);
        new ExecutorCaller(new ExcDeputy(), commands.deputy.name, "funnyguilds.deputy", commands.deputy.aliases);
        new ExecutorCaller(new ExcRanking(), commands.ranking.name, "funnyguilds.ranking", commands.ranking.aliases);
        new ExecutorCaller(new ExcItems(), commands.items.name, "funnyguilds.items", commands.items.aliases);

        new ExecutorCaller(new MxcPvP(), commands.pvp.name, "funnyguilds.manage", commands.pvp.aliases);
        new ExecutorCaller(new MxcBase(), commands.setbase.name, "funnyguilds.manage", commands.setbase.aliases);

        new ExecutorCaller(new AxcMain(), commands.admin.main, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcAdd(), commands.admin.add, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcDelete(), commands.admin.delete, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcKick(), commands.admin.kick, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcTeleport(), commands.admin.teleport, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcPoints(), commands.admin.points, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcKills(), commands.admin.kills, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcDeaths(), commands.admin.deaths, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcBan(), commands.admin.ban, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcUnban(), commands.admin.unban, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcLives(), commands.admin.lives, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcMove(), commands.admin.move, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcValidity(), commands.admin.validity, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcName(), commands.admin.name, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcSpy(), commands.admin.spy, "funnyguilds.admin", null);
    }
}
