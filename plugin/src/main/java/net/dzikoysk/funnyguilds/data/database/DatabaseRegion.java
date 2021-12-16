package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;

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
                FunnyGuilds.getPluginLogger().error("Cannot deserialize region! Caused by: name == null");
                return null;
            }
            else if (location == null) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize region (" + name + ") ! Caused by: loc == null");
                return null;
            }

            Object[] values = new Object[4];

            values[0] = name;
            values[1] = location;
            values[2] = size;
            values[3] = enlarge;

            return DeserializationUtils.deserializeRegion(FunnyGuilds.getInstance().getRegionManager(), values);
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize region", ex);
        }

        return null;
    }

    public static void save(Region region) {
        SQLNamedStatement statement = SQLBasicUtils.getInsert(SQLDataModel.tabRegions);

        statement.set("name", region.getName());
        statement.set("center", LocationUtils.toString(region.getCenter()));
        statement.set("size", region.getSize());
        statement.set("enlarge", region.getEnlarge());
        statement.executeUpdate();
    }

    public static void delete(Region region) {
        SQLNamedStatement statement = SQLBasicUtils.getDelete(SQLDataModel.tabRegions);

        statement.set("name", region.getName());
        statement.executeUpdate();
    }

    private DatabaseRegion() {
    }

}
