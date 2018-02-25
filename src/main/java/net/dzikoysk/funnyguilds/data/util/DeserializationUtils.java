package net.dzikoysk.funnyguilds.data.util;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.FunnyLogger;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class DeserializationUtils {

    @SuppressWarnings("unchecked")
    public static Guild deserializeGuild(Object[] values) {
        if (values == null) {
            FunnyLogger.error("[Deserialize] Cannot deserialize guild! Caused by: null");
            return null;
        }
        
        Guild guild = Guild.getOrCreate((String) values[1]);
        
        guild.setUUID((UUID) values[0]);
        guild.setTag(Settings.getConfig().guildTagUppercase ? ((String) values[2]).toUpperCase() : ((String) values[2]).toLowerCase());
        guild.setOwner((User) values[3]);
        guild.setHome((Location) values[4]);
        guild.setRegion((String) values[5]);
        guild.setMembers((List<User>) values[6]);
        guild.setRegions((List<String>) values[7]);
        guild.setAllies((List<Guild>) values[8]);
        guild.setEnemies((List<Guild>) values[9]);
        guild.setBorn((long) values[10]);
        guild.setValidity((long) values[11]);
        guild.setAttacked((long) values[12]);
        guild.setLives((int) values[13]);
        guild.setBan((long) values[14]);
        guild.setDeputy((User) values[15]);
        guild.deserializationUpdate();
        
        return guild;
    }

    public static Region deserializeRegion(Object[] values) {
        if (values == null) {
            FunnyLogger.error("Cannot deserialize region! Caused by: null");
            return null;
        }
        
        Region region = Region.get((String) values[0]);
        
        region.setCenter((Location) values[1]);
        region.setSize((int) values[2]);
        region.setEnlarge((int) values[3]);
        region.update();
        
        return region;
    }

    public static User deserializeUser(Object[] values) {
        User user = User.get(UUID.fromString((String) values[0]));
        
        user.setName((String) values[1]);
        user.getRank().setPoints((int) values[2]);
        user.getRank().setKills((int) values[3]);
        user.getRank().setDeaths((int) values[4]);
        user.setBan((long) values[5]);
        user.setReason((String) values[6]);
        
        return user;
    }

}
