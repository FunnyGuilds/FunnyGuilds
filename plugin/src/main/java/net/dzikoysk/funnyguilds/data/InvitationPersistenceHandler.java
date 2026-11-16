package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.FunnyStreams;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class InvitationPersistenceHandler {

    private final FunnyGuilds plugin;
    private final GuildManager guildManager;
    private final GuildInvitationList guildInvitationList;
    private final AllyInvitationList allyInvitationList;
    private final File invitationsFile;
    private volatile BukkitTask invitationPersistenceHandlerTask;

    public InvitationPersistenceHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.guildManager = plugin.getGuildManager();
        this.guildInvitationList = plugin.getGuildInvitationList();
        this.allyInvitationList = plugin.getAllyInvitationList();
        this.invitationsFile = new File(plugin.getPluginDataFolder(), "invitations.yml");
    }

    public void startHandler() {
        long interval = this.plugin.getPluginConfiguration().dataInterval * 60L * 20L;

        if (this.invitationPersistenceHandlerTask != null) {
            this.invitationPersistenceHandlerTask.cancel();
        }

        this.invitationPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin,
                this::saveInvitations, interval, interval);
    }

    public void stopHandler() {
        if (this.invitationPersistenceHandlerTask == null) {
            return;
        }

        this.invitationPersistenceHandlerTask.cancel();
        this.invitationPersistenceHandlerTask = null;
    }

    public void saveInvitations() {
        FunnyIOUtils.deleteFile(this.invitationsFile);

        YamlWrapper yaml = new YamlWrapper(this.invitationsFile);
        this.guildManager.getGuilds().forEach(guild -> {
            List<String> guildInvitations = this.guildInvitationList.getInvitationsFrom(guild).stream()
                    .map(GuildInvitation::getToUUID)
                    .map(UUID::toString)
                    .toList();

            List<String> allyInvitations = this.allyInvitationList.getInvitationsFrom(guild).stream()
                    .map(AllyInvitation::getToUUID)
                    .map(UUID::toString)
                    .toList();

            yaml.set(guild.getUUID().toString() + ".players", guildInvitations);
            yaml.set(guild.getUUID().toString() + ".guilds", allyInvitations);
        });

        yaml.save();
    }

    public void loadInvitations() {
        if (!this.invitationsFile.exists()) {
            return;
        }

        YamlWrapper yaml = new YamlWrapper(this.invitationsFile);
        FunnyStreams.of(yaml.getKeys(false))
                .map(UUID::fromString)
                .flatMap(uuid -> this.guildManager.findByUuid(uuid).stream())
                .forEach(guild -> {
                    this.loadGuildInvitations(yaml, guild);
                    this.loadAllyInvitations(yaml, guild);
                });
    }

    private void loadGuildInvitations(YamlWrapper yaml, Guild guild) {
        yaml.getStringList(guild.getUUID().toString() + ".players").stream()
                .map(UUID::fromString)
                .forEach(userUuid -> this.guildInvitationList.createInvitation(guild.getUUID(), userUuid));
    }

    private void loadAllyInvitations(YamlWrapper yaml, Guild guild) {
        yaml.getStringList(guild.getUUID().toString() + ".guilds").stream()
                .map(UUID::fromString)
                .flatMap(uuid -> this.guildManager.findByUuid(uuid).stream())
                .forEach(allyGuild -> this.allyInvitationList.createInvitation(guild, allyGuild));
    }

}
