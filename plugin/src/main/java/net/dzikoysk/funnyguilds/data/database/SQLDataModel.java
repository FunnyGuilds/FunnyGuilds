package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLElement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLType;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import panda.std.Option;

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
        tabUsers.add("assists",   SQLType.INT,     true);
        tabUsers.add("logouts",   SQLType.INT,     true);
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
        tabGuilds.add("attacked", SQLType.BIGINT); //TODO: [FG 5.0] attacked -> protection
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
        GuildManager guildManager = FunnyGuilds.getInstance().getGuildManager();

        ResultSet resultAll = SQLBasicUtils.getSelectAll(SQLDataModel.tabGuilds).executeQuery();

        while (resultAll.next()) {
            Guild guild = DatabaseGuild.deserialize(resultAll);

            if (guild != null) {
                guild.wasChanged();
            }
        }

        ResultSet result = SQLBasicUtils.getSelect(SQLDataModel.tabGuilds, "tag", "allies", "enemies").executeQuery();

        while (result.next()) {
            Option<Guild> guildOption = guildManager.getGuildByTag(result.getString("tag"));
            if (guildOption.isEmpty()) {
                continue;
            }

            Guild guild = guildOption.get();

            String alliesList = result.getString("allies");
            String enemiesList = result.getString("enemies");

            if (alliesList != null && !alliesList.equals("")) {
                guild.setAllies(guildManager.findByNames(ChatUtils.fromString(alliesList)));
            }

            if (enemiesList != null && !enemiesList.equals("")) {
                guild.setEnemies(guildManager.findByNames(ChatUtils.fromString(enemiesList)));
            }
        }

        for (Guild guild : guildManager.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            guildManager.deleteGuild(guild);
        }

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guildManager.countGuilds());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        for (User user : FunnyGuilds.getInstance().getUserManager().getUsers()) {
            if (ignoreNotChanged && !user.wasChanged()) {
                continue;
            }

            DatabaseUser.save(user);
        }

        for (Guild guild : FunnyGuilds.getInstance().getGuildManager().getGuilds()) {
            if (ignoreNotChanged && !guild.wasChanged()) {
                continue;
            }

            DatabaseGuild.save(guild);
        }

        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
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
