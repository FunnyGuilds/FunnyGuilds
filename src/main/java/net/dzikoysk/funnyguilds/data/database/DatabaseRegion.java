package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.bukkit.Location;

import java.sql.ResultSet;

public class DatabaseRegion {

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
                FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region! Caused by: name == null");
                return null;
            } else if (location == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Cannot deserialize region (" + name + ") ! Caused by: loc == null");
                return null;
            }

            Object[] values = new Object[4];
            
            values[0] = name;
            values[1] = location;
            values[2] = size;
            values[3] = enlarge;
            
            return DeserializationUtils.deserializeRegion(values);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not deserialize region", ex);
        }
        
        return null;
    }

    public static void save(Region region) {
        SQLNamedStatement namedPS = SQLBasicUtils.getInsert(SQLDataModel.tabRegions);

        namedPS.set("name", region.getName());
        namedPS.set("center", LocationUtils.toString(region.getCenter()));
        namedPS.set("size", region.getSize());
        namedPS.set("enlarge", region.getEnlarge());
        namedPS.executeUpdate();
    }

    public static void delete(Region region) {
        SQLNamedStatement namedPS = SQLBasicUtils.getDelete(SQLDataModel.tabRegions);

        namedPS.set("name", region.getName());
        namedPS.executeUpdate();
    }

    private DatabaseRegion() {}

}
