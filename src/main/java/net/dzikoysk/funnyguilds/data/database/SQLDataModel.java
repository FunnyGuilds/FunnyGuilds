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
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.element.*;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.util.HashSet;
import java.util.Set;

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
        tabUsers.add("guild",     SQLType.VARCHAR, 100);
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

    public void load() {
        Database db = Database.getInstance();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        createTableIfNotExists(db, tabUsers);
        createTableIfNotExists(db, tabRegions);
        createTableIfNotExists(db, tabGuilds);

        loadUsers(config);
        loadRegions(config);
        loadGuilds(config);

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    public void loadUsers(PluginConfiguration config) {
        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.usersTableName + "`", usersResult -> {
            try {
                while (usersResult.next()) {
                    String userName = usersResult.getString("name");

                    if (!UserUtils.validateUsername(userName)) {
                        FunnyGuilds.getInstance().getPluginLogger().warning("Skipping loading of user '" + userName + "'. Name is invalid.");
                        continue;
                    }

                    User user = DatabaseUser.deserialize(usersResult);
                    if (user != null) {
                        user.wasChanged();
                    }
                }

                FunnyGuilds.getInstance().getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load users from database", ex);
            }
        });
    }

    public void loadRegions(PluginConfiguration config) {
        if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.regionsTableName + "`", regionsResult -> {
                try {
                    while (regionsResult.next()) {
                        Region region = DatabaseRegion.deserialize(regionsResult);
                        if (region != null) {
                            region.wasChanged();
                        }
                    }

                    FunnyGuilds.getInstance().getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
                } catch (Exception ex) {
                    FunnyGuilds.getInstance().getPluginLogger().error("Could not load regions from database", ex);
                }
            });

        } else {
            FunnyGuilds.getInstance().getPluginLogger().info("Regions are disabled and thus - not loaded");
        }
    }

    public void loadGuilds(PluginConfiguration config) {
        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.guildsTableName + "`", guildsResult -> {
            try {
                while (guildsResult.next()) {
                    Guild guild = DatabaseGuild.deserialize(guildsResult);
                    if (guild != null) {
                        guild.wasChanged();
                    }
                }

                FunnyGuilds.getInstance().getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size());
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load guilds from database", ex);
            }
        });

        Database.getInstance().executeQuery("SELECT `tag`, `allies`, `enemies` FROM `" + config.mysql.guildsTableName + "`", result -> {
            try {
                while (result.next()) {
                    Guild guild = GuildUtils.getByTag(result.getString("tag"));

                    if (guild == null) {
                        continue;
                    }

                    String alliesList = result.getString("allies");
                    String enemiesList = result.getString("enemies");
                    Set<Guild> allies = new HashSet<>();
                    Set<Guild> enemies = new HashSet<>();

                    if (alliesList != null && !alliesList.equals("")) {
                        allies = GuildUtils.getGuilds(ChatUtils.fromString(alliesList));
                    }

                    if (enemiesList != null && !enemiesList.equals("")) {
                        enemies = GuildUtils.getGuilds(ChatUtils.fromString(enemiesList));
                    }

                    guild.setAllies(allies);
                    guild.setEnemies(enemies);
                }
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not load allies from database", ex);
            }
        });

        // TODO
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        for (User user : UserUtils.getUsers()) {
            if (ignoreNotChanged) {
                if (! user.wasChanged()) {
                    continue;
                }
            }

            try {
                new DatabaseUser(user).save();
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not save user to database", ex);
            }
        }

        if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            for (Region region : RegionUtils.getRegions()) {
                if (ignoreNotChanged) {
                    if (! region.wasChanged()) {
                        continue;
                    }
                }

                try {
                    new DatabaseRegion(region).save();
                }
                catch (Exception ex) {
                    FunnyGuilds.getInstance().getPluginLogger().error("Could not save region to database", ex);
                }
            }
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged) {
                if (! guild.wasChanged()) {
                    continue;
                }
            }

            try {
                new DatabaseGuild(guild).save();
            }
            catch (Exception ex) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not save guild to database", ex);
            }
        }
    }

    public void createTableIfNotExists(Database db, SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("create table if not exists");
        sb.append(" `").append(table.getName()).append("` ");
        sb.append("(");

        for (SQLElement element : table.getSqlElements()) {
            sb.append("`").append(element.getKey()).append("` ");
            sb.append(element.getType());

            if (element.isNotNull()) {
                sb.append(" not null");
            }

            sb.append(",");
        }

        sb.append("primary key (");
        sb.append(table.getPrimaryKey().getKey());
        sb.append("));");

        db.executeUpdate(sb.toString());
        tableRepair(db, table);
    }

    public void tableRepair(Database db, SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("alter table");
        sb.append(" `").append(table.getName()).append("` ");
        sb.append("add column");

        int startChar = sb.length();

        for (int index = 0; index < table.getSqlElements().size(); index++) {
            SQLElement element = table.getSqlElements().get(index);

            sb.setLength(startChar);
            sb.append(" `").append(element.getKey()).append("` ");
            sb.append(element.getType());
            sb.append(index == 0 ? " first" : " after " + table.getSqlElements().get(index - 1).getKey());
            sb.append(";");

            db.executeUpdate(sb.toString(), true);
        }
    }
}
