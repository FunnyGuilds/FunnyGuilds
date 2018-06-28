package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.util.Yamler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Data {

    private static final File DATA = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data");
    private static Data instance;
    private static File folder;

    public Data() {
        folder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data");
        instance = this;
        invitations(DO.LOAD);
    }

    public static File getPlayerListFile() {
        return new File(folder, "playerlist.yml");
    }

    public static File getDataFolder() {
        return DATA;
    }

    public static Data getInstance() {
        if (instance != null) {
            return instance;
        }
        return new Data();
    }

    public void save() {
        invitations(DO.SAVE);
    }

    private void invitations(DO todo) {
        File file = new File(folder, "invitations.yml");
        if (todo == DO.SAVE) {
            file.delete();
            Yamler pc = new Yamler(file);
            
            for (Guild guild : GuildUtils.getGuilds()) {
                List<InvitationList.Invitation> invitationList = InvitationList.getInvitationsFrom(guild);
                
                for(InvitationList.Invitation invitation : invitationList) {
                    List<String> allyInvitations = new ArrayList<>();
                    List<String> playerInvitations = new ArrayList<>();

                    if (invitation.isToGuild()) {
                        playerInvitations.add(invitation.getFor().toString());
                    } else if (invitation.isToAlly()) {
                        allyInvitations.add(invitation.getFor().toString());
                    }

                    pc.set(invitation.getFrom().toString() + ".guilds", allyInvitations);
                    pc.set(invitation.getFrom().toString() + ".players", playerInvitations);
                }
            }

            pc.save();
        } else if (todo == DO.LOAD) {
            if (!file.exists()) {
                return;
            }
            
            Yamler pc = new Yamler(file);
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

    private enum DO {
        SAVE,
        LOAD
    }

}
