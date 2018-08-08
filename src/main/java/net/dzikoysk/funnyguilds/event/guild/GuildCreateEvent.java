package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Location;

public class GuildCreateEvent extends GuildEvent {

    private final String name;
    private final String tag;
    private final Location guildLocation;

    public GuildCreateEvent(EventCause eventCause, User doer, String name, String tag, Location guildLocation) {
        super(eventCause, doer, null);

        this.name = name;
        this.tag = tag;
        this.guildLocation = guildLocation;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Location getGuildLocation() {
        return guildLocation;
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild creation has been cancelled by the server!";
    }

}
