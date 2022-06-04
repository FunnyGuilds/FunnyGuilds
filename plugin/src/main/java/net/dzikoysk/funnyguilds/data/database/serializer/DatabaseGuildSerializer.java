package net.dzikoysk.funnyguilds.data.database.serializer;

import com.google.common.collect.Sets;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import panda.std.Option;

public final class DatabaseGuildSerializer {

    private DatabaseGuildSerializer() {
    }

    public static Option<Guild> deserialize(ResultSet resultSet) {
        if (resultSet == null) {
            return Option.none();
        }

        String id = null;
        String name = null;

        try {
            id = resultSet.getString("uuid");
            name = resultSet.getString("name");
            String tag = resultSet.getString("tag");
            String os = resultSet.getString("owner");
            String dp = resultSet.getString("deputy");
            String home = resultSet.getString("home");
            String regionName = resultSet.getString("region");
            String membersString = resultSet.getString("members");
            boolean pvp = resultSet.getBoolean("pvp");
            long born = resultSet.getLong("born");
            long validity = resultSet.getLong("validity");
            long attacked = resultSet.getLong("attacked");
            long ban = resultSet.getLong("ban");
            int lives = resultSet.getInt("lives");

            FunnyGuilds plugin = FunnyGuilds.getInstance();
            PluginConfiguration config = plugin.getPluginConfiguration();
            UserManager userManager = plugin.getUserManager();

            if (name == null || tag == null || os == null) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
                return Option.none();
            }

            UUID uuid = UUID.randomUUID();
            if (id != null && !id.isEmpty()) {
                uuid = UUID.fromString(id);
            }

            Option<User> ownerOption = userManager.findByName(os);
            if (ownerOption.isEmpty()) {
                FunnyGuilds.getPluginLogger().error("Cannot deserialize guild! Caused by: owner (user instance) doesn't exist");
                return Option.none();
            }

            Set<User> deputies = new HashSet<>();
            if (dp != null && !dp.isEmpty()) {
                deputies = userManager.findByNames(FunnyStringUtils.fromString(dp));
            }

            Set<User> members = new HashSet<>();
            if (membersString != null && !membersString.isEmpty()) {
                members = userManager.findByNames(FunnyStringUtils.fromString(membersString));
            }

            if (born == 0) {
                born = System.currentTimeMillis();
            }

            if (validity == 0) {
                validity = Instant.now().plus(config.validityStart).toEpochMilli();
            }

            if (lives == 0) {
                lives = config.warLives;
            }

            Object[] values = new Object[17];
            values[0] = uuid;
            values[1] = name;
            values[2] = tag;
            values[3] = ownerOption.get();
            values[4] = LocationUtils.parseLocation(home);
            values[5] = plugin.getRegionManager().findByName(regionName).orElseGet((Region) null);
            values[6] = members;
            values[7] = Sets.newHashSet();
            values[8] = Sets.newHashSet();
            values[9] = born;
            values[10] = validity;
            values[11] = attacked;
            values[12] = lives;
            values[13] = ban;
            values[14] = deputies;
            values[15] = pvp;

            return DeserializationUtils.deserializeGuild(plugin.getPluginConfiguration(), plugin.getGuildManager(), values);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not deserialize guild (id: " + id + ", name: " + name + ")", exception);
        }

        return Option.none();
    }

    public static void serialize(SQLDataModel dataModel, Guild guild) {
        SQLNamedStatement statement = SQLBasicUtils.getInsert(dataModel.getUsersTable());

        String members = FunnyStringUtils.join(Entity.names(guild.getMembers()));
        String deputies = FunnyStringUtils.join(Entity.names(guild.getDeputies()));
        String allies = FunnyStringUtils.join(Entity.names(guild.getAllies()));
        String enemies = FunnyStringUtils.join(Entity.names(guild.getEnemies()));

        statement.set("uuid", guild.getUUID().toString());
        statement.set("name", guild.getName());
        statement.set("tag", guild.getTag());
        statement.set("owner", guild.getOwner().getName());
        statement.set("home", LocationUtils.toString(guild.getHome()));
        statement.set("region", RegionUtils.toString(guild.getRegion()));
        statement.set("regions", "#abandoned");
        statement.set("members", members);
        statement.set("deputy", deputies);
        statement.set("allies", allies);
        statement.set("enemies", enemies);
        statement.set("points", guild.getRank().getAveragePoints());
        statement.set("lives", guild.getLives());
        statement.set("born", guild.getBorn());
        statement.set("validity", guild.getValidity());
        statement.set("attacked", guild.getProtection()); //TODO: [FG 5.0] attacked -> protection
        statement.set("ban", guild.getBan());
        statement.set("pvp", guild.hasPvPEnabled());
        statement.set("info", "");

        statement.executeUpdate();
    }

    public static void delete(SQLDataModel dataModel, Guild guild) {
        SQLNamedStatement statement = SQLBasicUtils.getDelete(dataModel.getGuildsTable());

        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
    }

    public static void updatePoints(SQLDataModel dataModel, Guild guild) {
        SQLTable table = dataModel.getGuildsTable();
        SQLNamedStatement statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points").orNull());

        statement.set("points", guild.getRank().getAveragePoints());
        statement.set("uuid", guild.getUUID().toString());
        statement.executeUpdate();
    }

}
