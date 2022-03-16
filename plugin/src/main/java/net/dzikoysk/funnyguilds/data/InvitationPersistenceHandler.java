package net.dzikoysk.funnyguilds.data;

import java.io.File;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.feature.invitation.Invitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import panda.std.stream.PandaStream;

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

        this.invitationPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveInvitations, interval, interval);
    }

    public void stopHandler() {
        if (this.invitationPersistenceHandlerTask == null) {
            return;
        }

        this.invitationPersistenceHandlerTask.cancel();
        this.invitationPersistenceHandlerTask = null;
    }

    public void saveInvitations() {
        this.invitationsFile.delete();
        YamlWrapper wrapper = new YamlWrapper(this.invitationsFile);

        for (Guild guild : this.guildManager.getGuilds()) {
            List<String> guildInvitations = PandaStream.of(this.guildInvitationList.getInvitationsFrom(guild))
                    .map(Invitation::getTo)
                    .map(UUID::toString)
                    .toList();
            wrapper.set(guild.getUUID().toString() + ".players", guildInvitations);

            List<String> allyInvitations = PandaStream.of(this.allyInvitationList.getInvitationsFrom(guild))
                    .map(Invitation::getTo)
                    .map(UUID::toString)
                    .toList();
            wrapper.set(guild.getUUID().toString() + ".guilds", allyInvitations);
        }

        wrapper.save();
    }

    public void loadInvitations() {
        if (!this.invitationsFile.exists()) {
            return;
        }

        YamlWrapper pc = new YamlWrapper(this.invitationsFile);

        for (String key : pc.getKeys(false)) {
            this.guildManager.findByUuid(UUID.fromString(key)).peek(guild -> {
                List<String> allyInvitations = pc.getStringList(key + ".guilds");
                List<String> playerInvitations = pc.getStringList(key + ".players");

                for (String ally : allyInvitations) {
                    this.guildManager.findByUuid(UUID.fromString(ally))
                            .peek(allyGuild -> this.allyInvitationList.createInvitation(guild, allyGuild));
                }

                for (String player : playerInvitations) {
                    this.guildInvitationList.createInvitation(guild.getUUID(), UUID.fromString(player));
                }
            });
        }
    }

}
