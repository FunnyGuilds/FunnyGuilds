package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvitationPersistenceHandler {

    private final FunnyGuilds plugin;

    private final GuildManager guildManager;

    private final File invitationsFile;
    private volatile BukkitTask invitationPersistenceHandlerTask;

    public InvitationPersistenceHandler(FunnyGuilds plugin) {
        this.plugin = plugin;

        this.guildManager = plugin.getGuildManager();

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
            List<InvitationList.Invitation> invitationList = InvitationList.getInvitationsFrom(guild);

            for (InvitationList.Invitation invitation : invitationList) {
                List<String> allyInvitations = new ArrayList<>();
                List<String> playerInvitations = new ArrayList<>();

                if (invitation.isToGuild()) {
                    playerInvitations.add(invitation.getFor().toString());
                } else if (invitation.isToAlly()) {
                    allyInvitations.add(invitation.getFor().toString());
                }

                wrapper.set(invitation.getFrom().toString() + ".guilds", allyInvitations);
                wrapper.set(invitation.getFrom().toString() + ".players", playerInvitations);
            }
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
                            .peek(allyGuild -> InvitationList.createInvitation(guild, allyGuild));
                }

                for (String player : playerInvitations) {
                    InvitationList.createInvitation(guild, UUID.fromString(player));
                }
            });
        }
    }

}
