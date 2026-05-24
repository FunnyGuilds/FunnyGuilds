package net.dzikoysk.funnyguilds.data.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLElement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLType;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import panda.std.Option;

public class SQLDataModel implements DataModel {

    private final FunnyGuilds plugin;
    private final PluginConfiguration pluginConfiguration;

    private final SQLTable usersTable;
    private final SQLTable guildsTable;
    private final SQLTable regionsTable;


    public SQLDataModel(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();

        this.usersTable = new SQLTable(this.pluginConfiguration.mysql.usersTableName);
        this.guildsTable = new SQLTable(this.pluginConfiguration.mysql.guildsTableName);
        this.regionsTable = new SQLTable(this.pluginConfiguration.mysql.regionsTableName);

        this.prepareTables();
    }

    public void prepareTables() {
        this.usersTable.add("uuid", SQLType.VARCHAR, 36, true);
        this.usersTable.add("name", SQLType.TEXT, true);
        this.usersTable.add("points", SQLType.INT, true);
        this.usersTable.add("kills", SQLType.INT, true);
        this.usersTable.add("deaths", SQLType.INT, true);
        this.usersTable.add("assists", SQLType.INT, true);
        this.usersTable.add("logouts", SQLType.INT, true);
        this.usersTable.add("ban", SQLType.BIGINT);
        this.usersTable.add("reason", SQLType.TEXT);
        this.usersTable.add("last_ip", SQLType.VARCHAR, 45);
        this.usersTable.setPrimaryKey("uuid");

        this.guildsTable.add("uuid", SQLType.VARCHAR, 100, true);
        this.guildsTable.add("name", SQLType.TEXT, true);
        this.guildsTable.add("tag", SQLType.TEXT, true);
        this.guildsTable.add("owner", SQLType.TEXT, true);
        this.guildsTable.add("home", SQLType.TEXT, true);
        this.guildsTable.add("region", SQLType.TEXT, true);
        this.guildsTable.add("regions", SQLType.TEXT, true);
        this.guildsTable.add("members", SQLType.TEXT, true);
        this.guildsTable.add("points", SQLType.INT, true);
        this.guildsTable.add("lives", SQLType.INT, true);
        this.guildsTable.add("heart_lives", SQLType.INT, true);
        this.guildsTable.add("ban", SQLType.BIGINT, true);
        this.guildsTable.add("born", SQLType.BIGINT, true);
        this.guildsTable.add("validity", SQLType.BIGINT, true);
        this.guildsTable.add("pvp", SQLType.BOOLEAN, true);
        this.guildsTable.add("protection", SQLType.BIGINT);
        this.guildsTable.add("allies", SQLType.TEXT);
        this.guildsTable.add("enemies", SQLType.TEXT);
        this.guildsTable.add("info", SQLType.TEXT);
        this.guildsTable.add("deputy", SQLType.TEXT);
        this.guildsTable.setPrimaryKey("uuid");

        this.regionsTable.add("name", SQLType.VARCHAR, 100, true);
        this.regionsTable.add("center", SQLType.TEXT, true);
        this.regionsTable.add("size", SQLType.INT, true);
        this.regionsTable.add("enlarge", SQLType.INT, true);
        this.regionsTable.setPrimaryKey("name");
    }

    public void load() throws SQLException {
        Option<Database> database = this.plugin.getDatabase();
        if (database.isEmpty()) {
            throw new SQLException("Database is not initialized");
        }

        try (Connection connection = database.get().getConnection()) {
            migrateSchema(connection, this.usersTable);
            migrateSchema(connection, this.regionsTable);
            migrateSchema(connection, this.guildsTable);
        }

        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        this.plugin.getIndividualNameTagManager().map(ScoreboardGlobalUpdateSyncTask::new).peek(this.plugin::scheduleFunnyTasks);
    }

    public void loadUsers() {
        SQLBasicUtils.getSelectAll(this.usersTable).executeQuery(result -> {
            while (result.next()) {
                String userName = result.getString("name");

                if (FunnyValidator.validateUsername(this.pluginConfiguration, userName) != NameResult.VALID) {
                    FunnyGuilds.getPluginLogger().warning("Skipping loading of user '" + userName + "' - name is invalid");
                    continue;
                }

                DatabaseUserSerializer.deserialize(result);
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded users: " + this.plugin.getUserManager().countUsers());
    }

    public void loadGuilds() {
        GuildManager guildManager = this.plugin.getGuildManager();

        SQLBasicUtils.getSelectAll(this.guildsTable).executeQuery(resultAll -> {
            while (resultAll.next()) {
                DatabaseGuildSerializer.deserialize(resultAll);
            }
        });

        SQLBasicUtils.getSelect(this.guildsTable, "tag", "allies", "enemies").executeQuery(result -> {
            while (result.next()) {
                Option<Guild> guildOption = guildManager.findByTag(result.getString("tag"));
                if (guildOption.isEmpty()) {
                    continue;
                }

                Guild guild = guildOption.get();
                String alliesList = result.getString("allies");
                String enemiesList = result.getString("enemies");

                if (alliesList != null && !alliesList.isEmpty()) {
                    guild.setAllies(guildManager.findByNames(FunnyStringUtils.fromString(alliesList)));
                }

                if (enemiesList != null && !enemiesList.isEmpty()) {
                    guild.setEnemies(guildManager.findByNames(FunnyStringUtils.fromString(enemiesList)));
                }
            }
        });

        guildManager.getGuilds().stream()
                .filter(guild -> guild.getOwner() == null)
                .forEach(guild -> guildManager.deleteGuild(FunnyGuilds.getInstance(), guild));

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guildManager.countGuilds());
    }

    public void loadRegions() {
        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        RegionManager regionManager = this.plugin.getRegionManager();

        SQLBasicUtils.getSelectAll(this.regionsTable).executeQuery(result -> {
            while (result.next()) {
                DatabaseRegionSerializer.deserialize(result).peek(regionManager::addRegion);
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + regionManager.countRegions());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.plugin.getUserManager().getUsers().stream()
                .filter(user -> !ignoreNotChanged || user.wasChanged())
                .forEach(DatabaseUserSerializer::serialize);

        this.plugin.getGuildManager().getGuilds().stream()
                .filter(guild -> !ignoreNotChanged || guild.wasChanged())
                .forEach(DatabaseGuildSerializer::serialize);

        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            return;
        }

        this.plugin.getRegionManager().getRegions().stream()
                .filter(region -> !ignoreNotChanged || region.wasChanged())
                .forEach(DatabaseRegionSerializer::serialize);
    }

    public SQLTable getUsersTable() {
        return this.usersTable;
    }

    public SQLTable getGuildsTable() {
        return this.guildsTable;
    }

    public SQLTable getRegionsTable() {
        return this.regionsTable;
    }

    static void migrateSchema(Connection connection, SQLTable table) throws SQLException {
        SQLBasicUtils.getCreate(table).executeUpdate(connection);

        Set<String> existingColumns = fetchExistingColumns(connection, table.getName());
        for (SQLElement sqlElement : table.getSqlElements()) {
            if (existingColumns.contains(sqlElement.getKey().toLowerCase(Locale.ROOT))) {
                continue;
            }
            SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(connection);
        }
    }

    static Set<String> fetchExistingColumns(Connection connection, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, null)) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
            }
        }
        return columns;
    }

}
