package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.admin.*;
import net.dzikoysk.funnyguilds.command.manager.MxcBase;
import net.dzikoysk.funnyguilds.command.manager.MxcPvP;
import net.dzikoysk.funnyguilds.command.util.ExecutorCaller;
import net.dzikoysk.funnyguilds.data.Settings;

public class Commands {

    private static Commands instance;

    public void register() {
        Settings s = Settings.getInstance();
        new ExecutorCaller(new ExcFunnyGuilds(), "funnyguilds", null, null);
        new ExecutorCaller(new ExcCreate(), s.excCreate, "funnyguilds.create", s.excCreateAliases);
        new ExecutorCaller(new ExcDelete(), s.excDelete, "funnyguilds.delete", s.excDeleteAliases);
        new ExecutorCaller(new ExcConfirm(), s.excConfirm, "funnyguilds.delete", s.excConfirmAliases);
        new ExecutorCaller(new ExcInvite(), s.excInvite, "funnyguilds.invite", s.excInviteAliases);
        new ExecutorCaller(new ExcJoin(), s.excJoin, "funnyguilds.join", s.excJoinAliases);
        new ExecutorCaller(new ExcLeave(), s.excLeave, "funnyguilds.leave", s.excLeaveAliases);
        new ExecutorCaller(new ExcKick(), s.excKick, "funnyguilds.kick", s.excKickAliases);
        new ExecutorCaller(new ExcBase(), s.excBase, "funnyguilds.base", s.excBaseAliases);
        new ExecutorCaller(new ExcEnlarge(), s.excEnlarge, "funnyguilds.enlarge", s.excEnlargeAliases);
        new ExecutorCaller(new ExcGuild(), s.excGuild, "funnyguilds.help", s.excGuildAliases);
        new ExecutorCaller(new ExcAlly(), s.excAlly, "funnyguilds.ally", s.excAllyAliases);
        new ExecutorCaller(new ExcBreak(), s.excBreak, "funnyguilds.break", s.excBreakAliases);
        new ExecutorCaller(new ExcPlayer(), s.excPlayer, "funnyguilds.player", s.excPlayerAliases);
        new ExecutorCaller(new ExcInfo(), s.excInfo, "funnyguilds.info", s.excInfoAliases);
        new ExecutorCaller(new ExcTop(), s.excTop, "funnyguilds.top", s.excTopAliases);
        new ExecutorCaller(new ExcValidity(), s.excValidity, "funnyguilds.validity", s.excValidityAliases);
        new ExecutorCaller(new ExcLeader(), s.excLeader, "funnyguilds.leader", s.excLeaderAliases);
        new ExecutorCaller(new ExcDeputy(), s.excDeputy, "funnyguilds.deputy", s.excDeputyAliases);
        new ExecutorCaller(new ExcRanking(), s.excRanking, "funnyguilds.ranking", s.excRankingAliases);

        new ExecutorCaller(new MxcPvP(), s.mxcPvP, "funnyguilds.manage", s.mxcPvPAliases);
        new ExecutorCaller(new MxcBase(), s.mxcBase, "funnyguilds.manage", s.mxcBaseAliases);

        new ExecutorCaller(new AxcMain(), s.axcMain, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcAdd(), s.axcAdd, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcDelete(), s.axcDelete, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcKick(), s.axcKick, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcTeleport(), s.axcTeleport, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcPoints(), s.axcPoints, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcKills(), s.axcKills, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcDeaths(), s.axcDeaths, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcBan(), s.axcBan, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcUnban(), s.axcUnban, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcLives(), s.axcLives, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcMove(), s.axcMove, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcValidity(), s.axcValidity, "funnyguilds.admin", null);
        new ExecutorCaller(new AxcName(), s.axcName, "funnyguilds.admin", null);
    }

    public static Commands getInstance() {
        if (instance == null) instance = new Commands();
        return instance;
    }

}
