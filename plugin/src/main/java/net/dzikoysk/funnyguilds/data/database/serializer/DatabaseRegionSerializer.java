package net.dzikoysk.funnyguilds.data.database.serializer;

import java.sql.ResultSet;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Location;
import panda.std.Option;

public final class DatabaseRegionSerializer {

    private DatabaseRegionSerializer() {
    }

    public static Option<Region> deserialize(ResultSet resultSet) {
        if (resultSet == null) {
            return Option.none();
        }

        try {
            String name = resultSet.getString("name");
            Location center = LocationUtils.parseLocation(resultSet.getString("center"));
            int size = resultSet.getInt("size");
            int enlarge = resultSet.getInt("enlarge");

            if (name == null) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize region, caused by: name == null");
                return Option.none();
            }
            else if (center == null) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize region (" + name + "), caused by: center == null");
                return Option.none();
            }

            Object[] values = new Object[4];
            values[0] = name;
            values[1] = center;
            values[2] = size;
            values[3] = enlarge;

            return DeserializationUtils.deserializeRegion(FunnyGuilds.getInstance().getRegionManager(), values);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize region", exception);
        }

        return Option.none();
    }

    public static void serialize(Region region) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLNamedStatement statement = SQLBasicUtils.getInsert(dataModel.getRegionsTable());

        statement.set("name", region.getName());
        statement.set("center", LocationUtils.toString(region.getCenter()));
        statement.set("size", region.getSize());
        statement.set("enlarge", region.getEnlargementLevel());

        statement.executeUpdate();
        region.markUnchanged();
    }

    public static void delete(Region region) {
        SQLDataModel dataModel = (SQLDataModel) FunnyGuilds.getInstance().getDataModel();
        SQLNamedStatement statement = SQLBasicUtils.getDelete(dataModel.getRegionsTable());

        statement.set("name", region.getName());
        statement.executeUpdate();
    }

}
