package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvitationPersistenceHandler {

    private final    FunnyGuilds funnyGuilds;
    private final    File        invitationsFile;
    private volatile BukkitTask  invitationPersistenceHandlerTask;

    public InvitationPersistenceHandler(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;
        this.invitationsFile = new File(funnyGuilds.getPluginDataFolder(), "invitations.yml");
    }

    public void startHandler() {
        long interval = this.funnyGuilds.getPluginConfiguration().dataInterval * 60L * 20L;

        if (this.invitationPersistenceHandlerTask != null) {
            this.invitationPersistenceHandlerTask.cancel();
        }

        this.invitationPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(funnyGuilds, this::saveInvitations, interval, interval);
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

        for (Guild guild : GuildUtils.getGuilds()) {
            List<InvitationList.Invitation> invitationList = InvitationList.getInvitationsFrom(guild);

            for (InvitationList.Invitation invitation : invitationList) {
                List<String> allyInvitations = new ArrayList<>();
                List<String> playerInvitations = new ArrayList<>();

                if (invitation.isToGuild()) {
                    playerInvitations.add(invitation.getFor().toString());
                }
                else if (invitation.isToAlly()) {
                    allyInvitations.add(invitation.getFor().toString());
                }

                wrapper.set(invitation.getFrom().toString() + ".guilds", allyInvitations);
                wrapper.set(invitation.getFrom().toString() + ".players", playerInvitations);
            }
        }

        wrapper.save();
    }

    public void loadInvitations() {
        if (! this.invitationsFile.exists()) {
            return;
        }

        YamlWrapper pc = new YamlWrapper(this.invitationsFile);

        for (String key : pc.getKeys(false)) {
            Guild guild = GuildUtils.getByUUID(UUID.fromString(key));

            if (guild != null) {
                List<String> allyInvitations = pc.getStringList(key + ".guilds");
                List<String> playerInvitations = pc.getStringList(key + ".players");

                for (String ally : allyInvitations) {
                    Guild allyGuild = GuildUtils.getByUUID(UUID.fromString(ally));

                    if (allyGuild != null) {
                        InvitationList.createInvitation(guild, allyGuild);
                    }
                }

                for (String player : playerInvitations) {
                    InvitationList.createInvitation(guild, UUID.fromString(player));
                }
            }
        }
    }

}
