package net.dzikoysk.funnyguilds.data.util;

import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserBan;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.Location;

import java.util.Set;
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
        guild.setTag(Settings.getConfig().guildTagKeepCase ? (String) values[2] : (Settings.getConfig().guildTagUppercase ? ((String) values[2]).toUpperCase() : ((String) values[2]).toLowerCase()));
        guild.setOwner((User) values[3]);
        guild.setHome((Location) values[4]);
        guild.setRegion((Region) values[5]);
        guild.setMembers((Set<User>) values[6]);
        guild.setAllies((Set<Guild>) values[8]);
        guild.setEnemies((Set<Guild>) values[9]);
        guild.setBorn((long) values[10]);
        guild.setValidity((long) values[11]);
        guild.setAttacked((long) values[12]);
        guild.setLives((int) values[13]);
        guild.setBan((long) values[14]);
        guild.setDeputies((Set<User>) values[15]);
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
        User user = User.get(UUID.fromString((String) values[0]), (String) values[1]);
        
        user.getRank().setPoints((int) values[2]);
        user.getRank().setKills((int) values[3]);
        user.getRank().setDeaths((int) values[4]);

        long banTime = (long) values[5];

        if (banTime > 0) {
            user.setBan(new UserBan((String) values[6], banTime));
        }
        
        return user;
    }

}
