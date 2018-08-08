package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.BasicUtils;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.Data;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;

import java.io.File;

public class Flat {

    public static final File GUILDS = new File(Data.getDataFolder() + File.separator + "guilds");
    public static final File REGIONS = new File(Data.getDataFolder() + File.separator + "regions");
    public static final File USERS = new File(Data.getDataFolder() + File.separator + "users");
    private static Flat instance;

    public Flat() {
        instance = this;
        new FlatPatcher().patch();
    }

    public static Flat getInstance() {
        if (instance == null) {
            new Flat();
        }
        return instance;
    }

    public static File loadCustomFile(BasicType type, String name) {
        switch (type) {
            case GUILD: {
                File file = new File(GUILDS + File.separator + name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case REGION: {
                File file = new File(REGIONS + File.separator + name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case USER: {
                File file = new File(USERS + File.separator + name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            default:
                return null;
        }
    }

    public static File getUserFile(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getName());
        sb.append(".yml");
        return new File(USERS + File.separator + sb.toString());
    }

    public static File getRegionFile(Region region) {
        StringBuilder sb = new StringBuilder();
        sb.append(region.getName());
        sb.append(".yml");
        return new File(REGIONS, sb.toString());
    }

    public static File getGuildFile(Guild guild) {
        StringBuilder sb = new StringBuilder();
        sb.append(guild.getName());
        sb.append(".yml");
        return new File(GUILDS, sb.toString());
    }

    public void load() {
        loadUsers();
        loadRegions();
        loadGuilds();
        BasicUtils.checkObjects();
    }

    public void save(boolean b) {
        saveUsers(b);
        saveRegions(b);
        saveGuilds(b);
    }

    private void saveUsers(boolean b) {
        if (UserUtils.getUsers().isEmpty()) {
            return;
        }

        for (User user : UserUtils.getUsers()) {
            if (user.getUUID() != null && user.getName() != null) {
                if (!b) {
                    if (!user.changed()) {
                        continue;
                    }
                }

                new FlatUser(user).serialize();
            }
        }
    }

    private void loadUsers() {
        int i = 0;
        File[] path = USERS.listFiles();

        if (path != null) {
            for (File file : path) {
                if (file.isDirectory() || file.length() == 0) {
                    file.delete();
                    i++;
                    continue;
                }

                User user = FlatUser.deserialize(file);

                if (user == null) {
                    file.delete();
                    i++;
                } else {
                    user.changed();
                }
            }
        }

        if (i > 0) {
            FunnyGuildsLogger.warning("Repaired conflicts: " + i);
        }

        FunnyGuildsLogger.info("Loaded users: " + UserUtils.getUsers().size());
    }

    private void saveRegions(boolean b) {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }
        
        int i = 0;
        for (Region region : RegionUtils.getRegions()) {
            if (!b) {
                if (!region.changed()) {
                    continue;
                }
            }
            if (!new FlatRegion(region).serialize()) {
                RegionUtils.delete(region);
                i++;
            }
        }
        if (i > 0) {
            FunnyGuildsLogger.warning("Deleted defective regions: " + i);
        }
    }

    private void loadRegions() {
        if (!Settings.getConfig().regionsEnabled) {
            FunnyGuildsLogger.info("Regions are disabled and thus - not loaded");
            return;
        }
        
        File[] path = REGIONS.listFiles();

        if (path != null) {
            for (File file : path) {
                Region region = FlatRegion.deserialize(file);
                if (region == null) {
                    file.delete();
                } else {
                    region.changed();
                }
            }
        }
        
        FunnyGuildsLogger.info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    private void saveGuilds(boolean forceSave) {
        int deleted = 0;

        for (Guild guild : GuildUtils.getGuilds()) {
            if (!forceSave && !guild.changed()) {
                continue;
            }
            
            if (!new FlatGuild(guild).serialize()) {
                GuildUtils.deleteGuild(guild);
                deleted++;
            }
        }
        
        if (deleted > 0) {
            FunnyGuildsLogger.warning("Deleted defective guild: " + deleted);
        }
    }

    private void loadGuilds() {
        GuildUtils.getGuilds().clear();
        File[] path = GUILDS.listFiles();

        if (path != null) {
            for (File file : path) {
                Guild guild = FlatGuild.deserialize(file);

                if (guild == null) {
                    file.delete();
                } else {
                    guild.changed();
                }
            }
        }

        // TODO
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getOwner() != null) {
                continue;
            }

            GuildUtils.deleteGuild(guild);
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new DatabaseFixAlliesRequest(), new PrefixGlobalUpdateRequest());

        FunnyGuildsLogger.info("Loaded guilds: " + GuildUtils.getGuilds().size());
    }

}
