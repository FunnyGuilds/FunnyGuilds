package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

public class GuildLivesChangeEvent extends GuildEvent {

    private final int newLives;
    
    public GuildLivesChangeEvent(EventCause eventCause, User doer, Guild guild, int newLives) {
        super(eventCause, doer, guild);
        
        this.newLives = newLives;
    }

    public int getNewLives() {
        return this.newLives;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild lives change has been cancelled by the server!";
    }
    
}