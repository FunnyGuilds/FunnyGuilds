package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.element.*;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import org.apache.commons.lang.StringUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLDataModel implements DataModel {

    private static SQLDataModel instance;

    private final Map<BasicType, SQLTable<?>> tables = new HashMap<>();

    public final SQLTable<User> tabUsers;
    public final SQLTable<Region> tabRegions;
    public final SQLTable<Guild> tabGuilds;

    public SQLDataModel() {
        instance = this;

        FunnyGuilds plugin = FunnyGuilds.getInstance();
        PluginConfiguration config = plugin.getPluginConfiguration();

        tabUsers = new SQLTable<>(config.mysql.usersTableName);
        tabRegions = new SQLTable<>(config.mysql.regionsTableName);
        tabGuilds = new SQLTable<>(config.mysql.guildsTableName);

        tables.put(BasicType.USER, tabUsers);
        tables.put(BasicType.REGION, tabRegions);
        tables.put(BasicType.GUILD, tabGuilds);

        tabUsers.add("uuid",      SQLType.VARCHAR, 36,  true,   user -> user.getUUID().toString());
        tabUsers.add("name",      SQLType.TEXT,             true,   User::getName);
        tabUsers.add("points",    SQLType.INT,              true,   user -> user.getRank().getPoints());
        tabUsers.add("kills",     SQLType.INT,              true,   user -> user.getRank().getKills());
        tabUsers.add("deaths",    SQLType.INT,              true,   user -> user.getRank().getDeaths());
        tabUsers.add("assists",   SQLType.INT,              true,   user -> user.getRank().getAssists());
        tabUsers.add("logouts",   SQLType.INT,              true,   user -> user.getRank().getLogouts());
        tabUsers.add("ban",       SQLType.BIGINT,                          user -> user.isBanned() ? user.getBan().getBanTime() : 0);
        tabUsers.add("reason",    SQLType.TEXT,                            user -> user.isBanned() ? user.getBan().getReason() : null);
        tabUsers.setPrimaryKey("uuid");
        tabUsers.setGetAll(() -> plugin.getUserManager().getUsers());

        tabRegions.add("name",    SQLType.VARCHAR, 100, true,  Region::getName);
        tabRegions.add("center",  SQLType.TEXT,             true,   region -> LocationUtils.toString(region.getCenter()));
        tabRegions.add("size",    SQLType.INT,              true,   Region::getSize);
        tabRegions.add("enlarge", SQLType.INT,              true,   Region::getEnlarge);
        tabRegions.setPrimaryKey("name");
        tabRegions.setGetAll(RegionUtils::getRegions);

        tabGuilds.add("uuid",     SQLType.VARCHAR, 100, true,  guild -> guild.getUUID().toString());
        tabGuilds.add("name",     SQLType.TEXT,             true,   Guild::getName);
        tabGuilds.add("tag",      SQLType.TEXT,             true,   Guild::getTag);
        tabGuilds.add("owner",    SQLType.TEXT,             true,   guild -> guild.getOwner().getName());
        tabGuilds.add("home",     SQLType.TEXT,             true,   guild -> LocationUtils.toString(guild.getHome()));
        tabGuilds.add("region",   SQLType.TEXT,             true,   guild -> RegionUtils.toString(guild.getRegion()));
        tabGuilds.add("regions",  SQLType.TEXT,             true,   guild -> "#abandoned");
        tabGuilds.add("members",  SQLType.TEXT,             true,   guild -> UserUtils.getNamesToSave(guild.getMembers()));
        tabGuilds.add("points",   SQLType.INT,              true,   guild -> guild.getRank().getPoints());
        tabGuilds.add("lives",    SQLType.INT,              true,   Guild::getLives);
        tabGuilds.add("ban",      SQLType.BIGINT,           true,   Guild::getBan);
        tabGuilds.add("born",     SQLType.BIGINT,           true,   Guild::getBorn);
        tabGuilds.add("validity", SQLType.BIGINT,           true,   Guild::getValidity);
        tabGuilds.add("pvp",      SQLType.BOOLEAN,          true,   Guild::getPvP);
        tabGuilds.add("attacked", SQLType.BIGINT,                          Guild::getProtection);
        tabGuilds.add("allies",   SQLType.TEXT,                            guild -> GuildUtils.getNamesToSave(guild.getAllies()));
        tabGuilds.add("enemies",  SQLType.TEXT,                            guild -> GuildUtils.getNamesToSave(guild.getEnemies()));
        tabGuilds.add("info",     SQLType.TEXT,                            guild -> StringUtils.EMPTY);
        tabGuilds.add("deputy",   SQLType.TEXT,                            guild -> UserUtils.getNamesToSave(guild.getDeputies()));
        tabGuilds.setPrimaryKey("uuid");
        tabGuilds.setGetAll(GuildUtils::getGuilds);
    }

    public void load() throws SQLException {
        for (SQLTable<?> table : tables.values()) {
            createTableIfNotExists(table);
        }

        loadUsers();
        loadRegions();
        loadGuilds();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    @Deprecated
    public void loadUsers() throws SQLException {
        ResultSet result = SQLBasicUtils.getSelectAll(tabUsers).executeQuery();

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

    @Deprecated
    public void loadRegions() throws SQLException {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        ResultSet result = SQLBasicUtils.getSelectAll(tabRegions).executeQuery();

        while (result.next()) {
            Region region = DatabaseRegion.deserialize(result);

            if (region != null) {
                region.wasChanged();
                RegionUtils.addRegion(region);
            }
        }

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    @Deprecated
    public void loadGuilds() throws SQLException {
        ResultSet resultAll = SQLBasicUtils.getSelectAll(tabGuilds).executeQuery();

        while (resultAll.next()) {
            Guild guild = DatabaseGuild.deserialize(resultAll);

            if (guild != null) {
                guild.wasChanged();
            }
        }

        ResultSet result = SQLBasicUtils.getSelect(tabGuilds, "tag", "allies", "enemies").executeQuery();

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
        for (SQLTable<?> table : tables.values()) {
            table.saveTable(ignoreNotChanged);
        }
    }

    @Override
    public <T extends Basic> void saveBasic(T data) {
        Option<SQLTable<T>> table = getTable(data);

        if (table.isEmpty()) {
            return;
        }

        table.get().saveRecord(data);
    }

    @Override
    public <T extends Basic> void deleteBasic(T data) {
        Option<SQLTable<T>> table = getTable(data);

        if (table.isEmpty()) {
            return;
        }

        table.get().deleteRecord(data);
    }

    public <T extends Basic> void updateBasic(String toUpdate, T data) {
        Option<SQLTable<T>> table = getTable(data);

        if (table.isEmpty()) {
            return;
        }

        table.get().updateRecord(toUpdate, data);
    }

    public <T extends Basic> void createTableIfNotExists(SQLTable<T> table) {
        SQLBasicUtils.getCreate(table).executeUpdate();

        for (SQLElement<T> sqlElement : table.getSqlElements()) {
            SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true);
        }
    }

    public <T extends Basic> Option<SQLTable<T>> getTable(T data) {
        for (Map.Entry<BasicType, SQLTable<?>> entry : tables.entrySet()) {
            if (entry.getKey() != data.getType()) {
                continue;
            }

            return Option.of((SQLTable<T>) entry.getValue());
        }

        return Option.none();
    }

    public static SQLDataModel getInstance() {
        if (instance != null) {
            return instance;
        }

        return new SQLDataModel();
    }

}
