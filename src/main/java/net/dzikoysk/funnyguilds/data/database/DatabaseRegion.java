package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.sql.ResultSet;

public class DatabaseRegion {

    private final Region region;

    public DatabaseRegion(Region region) {
        this.region = region;
    }

    public static Region deserialize(ResultSet rs) {
        if (rs == null) {
            return null;
        }
        
        try {
            String name = rs.getString("name");
            String center = rs.getString("center");
            int size = rs.getInt("size");
            int enlarge = rs.getInt("enlarge");
            Location location = LocationUtils.parseLocation(center);

            if (name == null) {
                FunnyGuildsLogger.error("Cannot deserialize region! Caused by: name == null");
                return null;
            } else if (location == null) {
                FunnyGuildsLogger.error("Cannot deserialize region (" + name + ") ! Caused by: loc == null");
                return null;
            }

            Object[] values = new Object[4];
            
            values[0] = name;
            values[1] = location;
            values[2] = size;
            values[3] = enlarge;
            
            return DeserializationUtils.deserializeRegion(values);
        } catch (Exception e) {
            if (FunnyGuildsLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    public void save(Database db) {
        String update = getInsert();
        if (update != null) {
            db.executeUpdate(update);
        }
    }

    public void delete() {
        Database db = Database.getInstance();
        StringBuilder update = new StringBuilder();
        
        update.append("DELETE FROM `");
        update.append(Settings.getConfig().mysql.regionsTableName);
        update.append("` WHERE `name`='");
        update.append(region.getName());
        update.append("';");
        
        db.executeUpdate(update.toString());
    }

    public String getInsert() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("INSERT INTO `");
        sb.append(Settings.getConfig().mysql.regionsTableName);
        sb.append("` (`name`, `center`, `size`, `enlarge`) VALUES (");
        sb.append("'" + region.getName() + "',");
        sb.append("'" + LocationUtils.toString(region.getCenter()) + "',");
        sb.append("'" + region.getSize() + "',");
        sb.append("'" + region.getEnlarge() + "'");
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append("`center`='" + LocationUtils.toString(region.getCenter()) + "',");
        sb.append("`size`='" + region.getSize() + "',");
        sb.append("`enlarge`='" + region.getEnlarge() + "';");
        
        return sb.toString();
    }
    
}
