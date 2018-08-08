package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBasic {

    private static DatabaseBasic instance;

    public DatabaseBasic() {
        instance = this;
    }

    public static DatabaseBasic getInstance() {
        if (instance != null) {
            return instance;
        }
        
        return new DatabaseBasic();
    }

    public void load() {
        Database db = Database.getInstance();
        PluginConfig config = Settings.getConfig();

        usersTable(db);
        regionsTable(db);
        guildsTable(db);

        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.usersTableName + "`", usersResult -> {
            try {
                while (usersResult.next()) {
                    User user = DatabaseUser.deserialize(usersResult);
                    if (user != null) {
                        user.changed();
                    }
                }

                FunnyLogger.info("Loaded users: " + UserUtils.getUsers().size());
            } catch (Exception e) {
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        });


        if (Settings.getConfig().regionsEnabled) {
            Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.regionsTableName + "`", regionsResult -> {
                try {
                    while (regionsResult.next()) {
                        Region region = DatabaseRegion.deserialize(regionsResult);
                        if (region != null) {
                            region.changed();
                        }
                    }

                    FunnyLogger.info("Loaded regions: " + RegionUtils.getRegions().size());
                } catch (Exception e) {
                    if (FunnyLogger.exception(e.getCause())) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            FunnyLogger.info("Regions are disabled and thus - not loaded");
        }

        Database.getInstance().executeQuery("SELECT * FROM `" + config.mysql.guildsTableName + "`", guildsResult -> {
            try {
                while (guildsResult.next()) {
                    Guild guild = DatabaseGuild.deserialize(guildsResult);
                    if (guild != null) {
                        guild.changed();
                    }
                }

                FunnyLogger.info("Loaded guilds: " + GuildUtils.getGuilds().size());
            } catch (Exception e) {
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
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

                    List<Guild> allies = new ArrayList<>();

                    if (alliesList != null && !alliesList.equals("")) {
                        allies = GuildUtils.getGuilds(ChatUtils.fromString(alliesList));
                    }

                    List<Guild> enemies = new ArrayList<>();

                    if (enemiesList != null && !enemiesList.equals("")) {
                        enemies = GuildUtils.getGuilds(ChatUtils.fromString(enemiesList));
                    }

                    guild.setAllies(allies);
                    guild.setEnemies(enemies);
                }
            }
            catch (Exception ex) {
                if (FunnyLogger.exception(ex.getCause())) {
                    ex.printStackTrace();
                }
            }
        });

        // TODO
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalUpdateRequest());
    }

    public void save(boolean b) throws ClassNotFoundException, SQLException {
        Database db = Database.getInstance();
        for (User user : UserUtils.getUsers()) {
            if (!b) {
                if (!user.changed()) {
                    continue;
                }
            }
            
            try {
                new DatabaseUser(user).save(db);
            } catch (Exception e) {
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
        
        if (Settings.getConfig().regionsEnabled) {
            for (Region region : RegionUtils.getRegions()) {
                if (!b) {
                    if (!region.changed()) {
                        continue;
                    }
                }
                
                try {
                    new DatabaseRegion(region).save(db);
                } catch (Exception e) {
                    if (FunnyLogger.exception(e.getCause())) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        for (Guild guild : GuildUtils.getGuilds()) {
            if (!b) {
                if (!guild.changed()) {
                    continue;
                }
            }
            
            try {
                new DatabaseGuild(guild).save(db);
            } catch (Exception e) {
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void guildsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(Settings.getConfig().mysql.guildsTableName);
        sb.append("`(`uuid` varchar(100) not null,");
        sb.append("`name` text not null,");
        sb.append("`tag` text not null,");
        sb.append("`owner` text not null,");
        sb.append("`home` text not null,");
        sb.append("`region` text not null,");
        sb.append("`members` text not null,");
        sb.append("`regions` text not null,");
        sb.append("`points` int not null,");
        sb.append("`lives` int not null,");
        sb.append("`ban` bigint not null,");
        sb.append("`born` bigint not null,");
        sb.append("`validity` bigint not null,");
        sb.append("`pvp` boolean not null,");
        sb.append("`attacked` bigint,");
        sb.append("`allies` text,");
        sb.append("`enemies` text,");
        sb.append("`info` text,");
        sb.append("`deputy` text,");
        sb.append("primary key (uuid));");
        
        db.executeUpdate(sb.toString());
    }

    public void regionsTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(Settings.getConfig().mysql.regionsTableName);
        sb.append("`(`name` varchar(100) not null,");
        sb.append("`center` text not null,");
        sb.append("`size` int not null,");
        sb.append("`enlarge` int not null,");
        sb.append("primary key (name));");
        
        db.executeUpdate(sb.toString());
    }

    public void usersTable(Database db) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("create table if not exists `");
        sb.append(Settings.getConfig().mysql.usersTableName);
        sb.append("`(`uuid` varchar(36) not null,");
        sb.append("`name` text not null,");
        sb.append("`points` int not null,");
        sb.append("`kills` int not null,");
        sb.append("`deaths` int not null,");
        sb.append("`guild` varchar(100),");
        sb.append("`ban` bigint,");
        sb.append("`reason` text,");
        sb.append("primary key (uuid));");
        
        db.executeUpdate(sb.toString());
    }
    
}
