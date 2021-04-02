package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.*;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDataModel implements DataModel {

    private static SQLDataModel instance;
    public static SQLTable tabUsers;
    public static SQLTable tabRegions;
    public static SQLTable tabGuilds;

    public SQLDataModel() {
        instance = this;
        loadModels();
    }

    public static SQLDataModel getInstance() {
        if (instance != null) {
            return instance;
        }

        return new SQLDataModel();
    }

    public static void loadModels() {
        tabUsers = new SQLTable(FunnyGuilds.getInstance().getPluginConfiguration().mysql.usersTableName);
        tabRegions = new SQLTable(FunnyGuilds.getInstance().getPluginConfiguration().mysql.regionsTableName);
        tabGuilds = new SQLTable(FunnyGuilds.getInstance().getPluginConfiguration().mysql.guildsTableName);

        tabUsers.add("uuid",      SQLType.VARCHAR, 36, true);
        tabUsers.add("name",      SQLType.TEXT,    true);
        tabUsers.add("points",    SQLType.INT,     true);
        tabUsers.add("kills",     SQLType.INT,     true);
        tabUsers.add("deaths",    SQLType.INT,     true);
        tabUsers.add("ban",       SQLType.BIGINT);
        tabUsers.add("reason",    SQLType.TEXT);
        tabUsers.setPrimaryKey("uuid");

        tabRegions.add("name",    SQLType.VARCHAR, 100, true);
        tabRegions.add("center",  SQLType.TEXT,    true);
        tabRegions.add("size",    SQLType.INT,     true);
        tabRegions.add("enlarge", SQLType.INT,     true);
        tabRegions.setPrimaryKey("name");

        tabGuilds.add("uuid",     SQLType.VARCHAR, 100, true);
        tabGuilds.add("name",     SQLType.TEXT,    true);
        tabGuilds.add("tag",      SQLType.TEXT,    true);
        tabGuilds.add("owner",    SQLType.TEXT,    true);
        tabGuilds.add("home",     SQLType.TEXT,    true);
        tabGuilds.add("region",   SQLType.TEXT,    true);
        tabGuilds.add("regions",  SQLType.TEXT,    true);
        tabGuilds.add("members",  SQLType.TEXT,    true);
        tabGuilds.add("points",   SQLType.INT,     true);
        tabGuilds.add("lives",    SQLType.INT,     true);
        tabGuilds.add("ban",      SQLType.BIGINT,  true);
        tabGuilds.add("born",     SQLType.BIGINT,  true);
        tabGuilds.add("validity", SQLType.BIGINT,  true);
        tabGuilds.add("pvp",      SQLType.BOOLEAN, true);
        tabGuilds.add("attacked", SQLType.BIGINT);
        tabGuilds.add("allies",   SQLType.TEXT);
        tabGuilds.add("enemies",  SQLType.TEXT);
        tabGuilds.add("info",     SQLType.TEXT);
        tabGuilds.add("deputy",   SQLType.TEXT);
        tabGuilds.setPrimaryKey("uuid");
    }

    public void load() throws SQLException {
        createTableIfNotExists(tabUsers);
        createTableIfNotExists(tabRegions);
        createTableIfNotExists(tabGuilds);

        loadUsers();
        loadRegions();
        loadGuilds();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    public void loadUsers() throws SQLException {
        ResultSet result = SQLBasicUtils.getSelectAll(SQLDataModel.tabUsers).executeQuery();

        while (result.next()) {
            String userName = result.getString("name");

            if (!UserUtils.validateUsername(userName)) {
                FunnyGuilds.getPluginLogger().warning("Skipping loading of user '" + userName + "'. Name is invalid.");
                continue;
            }

            User user = DatabaseUser.deserialize(result);

            if (user != null) {
                user.wasChanged();
            }
        }

        FunnyGuilds.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
    }

    public void loadRegions() throws SQLException {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        ResultSet result = SQLBasicUtils.getSelectAll(SQLDataModel.tabRegions).executeQuery();

        while (result.next()) {
            Region region = DatabaseRegion.deserialize(result);

            if (region != null) {
                region.wasChanged();
                RegionUtils.addRegion(region);
            }
        }

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    public void loadGuilds() throws SQLException {
        ResultSet resultAll = SQLBasicUtils.getSelectAll(SQLDataModel.tabGuilds).executeQuery();

        while (resultAll.next()) {
            Guild guild = DatabaseGuild.deserialize(resultAll);

            if (guild != null) {
                guild.wasChanged();
            }
        }

        ResultSet result = SQLBasicUtils.getSelect(SQLDataModel.tabGuilds, "tag", "allies", "enemies").executeQuery();

        while (result.next()) {
            Guild guild = GuildUtils.getByTag(result.getString("tag"));

            if (guild == null) {
                continue;
            }

            String alliesList = result.getString("allies");
            String enemiesList = result.getString("enemies");

            if (alliesList != null && !alliesList.equals("")) {
                guild.setAllies(GuildUtils.getGuilds(ChatUtils.fromString(alliesList)));
            }

            if (enemiesList != null && !enemiesList.equals("")) {
                guild.setEnemies(GuildUtils.getGuilds(ChatUtils.fromString(enemiesList)));
            }
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        for (User user : UserUtils.getUsers()) {
            if (ignoreNotChanged && !user.wasChanged()) {
                continue;
            }

            DatabaseUser.save(user);
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged && !guild.wasChanged()) {
                continue;
            }

            DatabaseGuild.save(guild);
        }

        if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        for (Region region : RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region.wasChanged()) {
                continue;
            }

            DatabaseRegion.save(region);
        }
    }

    public void createTableIfNotExists(SQLTable table) {
        SQLBasicUtils.getCreate(table).executeUpdate();

        for (SQLElement sqlElement : table.getSqlElements()) {
            SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true);
        }
    }
}
