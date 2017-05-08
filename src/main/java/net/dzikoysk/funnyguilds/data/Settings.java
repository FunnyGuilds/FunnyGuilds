package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.ConfigUtils;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;
import org.diorite.cfg.system.deserializers.TemplateDeserializer;
import org.diorite.cfg.system.deserializers.TemplateDeserializers;
import org.diorite.cfg.system.elements.TemplateElements;

import java.io.File;

public class Settings {

    private static final File SETTINGS = new File(FunnyGuilds.getInstance().getDataFolder(), "config.yml");
    private static PluginConfig settings;

    public Settings() {
        settings = ConfigUtils.loadConfig(SETTINGS, PluginConfig.class);
        settings.reload();
    }

      /*

        // Commands Section
        this.excCreate = pc.getString("commands.create.name");
        this.excDelete = pc.getString("commands.delete.name");
        this.excConfirm = pc.getString("commands.confirm.name");
        this.excInvite = pc.getString("commands.invite.name");
        this.excJoin = pc.getString("commands.join.name");
        this.excLeave = pc.getString("commands.leave.name");
        this.excKick = pc.getString("commands.kick.name");
        this.excBase = pc.getString("commands.base.name");
        this.excEnlarge = pc.getString("commands.enlarge.name");
        this.excGuild = pc.getString("commands.guild.name");
        this.excAlly = pc.getString("commands.ally.name");
        this.excBreak = pc.getString("commands.break.name");
        this.excInfo = pc.getString("commands.info.name");
        this.excPlayer = pc.getString("commands.player.name");
        this.excTop = pc.getString("commands.top.name");
        this.excValidity = pc.getString("commands.validity.name");
        this.excLeader = pc.getString("commands.leader.name");
        this.excDeputy = pc.getString("commands.deputy.name");
        this.excRanking = pc.getString("commands.ranking.name");

        this.mxcPvP = pc.getString("commands.pvp.name");
        this.mxcBase = pc.getString("commands.setbase.name");

        this.excCreateAliases = pc.getStringList("commands.create.aliases");
        this.excDeleteAliases = pc.getStringList("commands.delete.aliases");
        this.excConfirmAliases = pc.getStringList("commands.confirm.aliases");
        this.excInviteAliases = pc.getStringList("commands.invite.aliases");
        this.excJoinAliases = pc.getStringList("commands.join.aliases");
        this.excLeaveAliases = pc.getStringList("commands.leave.aliases");
        this.excKickAliases = pc.getStringList("commands.kick.aliases");
        this.excBaseAliases = pc.getStringList("commands.base.aliases");
        this.excEnlargeAliases = pc.getStringList("commands.enlarge.aliases");
        this.excGuildAliases = pc.getStringList("commands.guild.aliases");
        this.excAllyAliases = pc.getStringList("commands.ally.aliases");
        this.excBreakAliases = pc.getStringList("commands.break.aliases");
        this.excInfoAliases = pc.getStringList("commands.info.aliases");
        this.excPlayerAliases = pc.getStringList("commands.player.aliases");
        this.excTopAliases = pc.getStringList("commands.top.aliases");
        this.excValidityAliases = pc.getStringList("commands.validity.aliases");
        this.excLeaderAliases = pc.getStringList("commands.leader.aliases");
        this.excDeputyAliases = pc.getStringList("commands.deputy.aliases");
        this.excRankingAliases = pc.getStringList("commands.ranking.aliases");

        this.mxcPvPAliases = pc.getStringList("commands.pvp.aliases");
        this.mxcBaseAliases = pc.getStringList("commands.setbase.aliases");

        this.axcMain = pc.getString("commands.admin.main");
        this.axcAdd = pc.getString("commands.admin.add");
        this.axcDelete = pc.getString("commands.admin.delete");
        this.axcKick = pc.getString("commands.admin.kick");
        this.axcTeleport = pc.getString("commands.admin.teleport");
        this.axcPoints = pc.getString("commands.admin.points");
        this.axcKills = pc.getString("commands.admin.kills");
        this.axcDeaths = pc.getString("commands.admin.deaths");
        this.axcBan = pc.getString("commands.admin.ban");
        this.axcUnban = pc.getString("commands.admin.unban");
        this.axcLives = pc.getString("commands.admin.lives");
        this.axcMove = pc.getString("commands.admin.move");
        this.axcValidity = pc.getString("commands.admin.validity");
        this.axcName = pc.getString("commands.admin.name");


        // Data Section
        this.dataInterval = pc.getInt("data-interval");
        this.flat = pc.getBoolean("data-type.flat");
        this.mysql = pc.getBoolean("data-type.mysql");
        if (this.mysql) {
            this.mysqlHostname = pc.getString("mysql.hostname");
            this.mysqlPort = pc.getString("mysql.port");
            this.mysqlDatabase = pc.getString("mysql.database");
            this.mysqlUser = pc.getString("mysql.user");
            this.mysqlPassword = pc.getString("mysql.password");
            this.poolSize = pc.getInt("mysql.poolSize");
            if (this.poolSize <= 1) {
                this.poolSize = 16;
            }
        }
        return null;
    */

    public static PluginConfig getConfig() {
        if(settings == null)
        {
            new Settings();
        }
        return settings;
    }

}
