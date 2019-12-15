package net.dzikoysk.funnyguilds.data.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.BasicType;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class FlatDataModel implements DataModel {

    private final File guildsFolderFile;
    private final File regionsFolderFile;
    private final File usersFolderFile;

    public FlatDataModel(FunnyGuilds funnyGuilds) {
        this.guildsFolderFile = new File(funnyGuilds.getPluginDataFolder(), "guilds");
        this.regionsFolderFile = new File(funnyGuilds.getPluginDataFolder(), "regions");
        this.usersFolderFile = new File(funnyGuilds.getPluginDataFolder(), "users");

        FlatPatcher flatPatcher = new FlatPatcher();
        flatPatcher.patch(this);
    }

    public File getGuildsFolder() {
        return this.guildsFolderFile;
    }

    public File getRegionsFolder() {
        return this.regionsFolderFile;
    }

    public File getUsersFolder() {
        return this.usersFolderFile;
    }

    File loadCustomFile(BasicType type, String name) {
        switch (type) {
            case GUILD: {
                File file = new File(this.guildsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case REGION: {
                File file = new File(this.regionsFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            case USER: {
                File file = new File(this.usersFolderFile, name + ".yml");
                IOUtils.initialize(file, true);
                return file;
            }
            default:
                return null;
        }
    }

    public File getUserFile(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getName());
        sb.append(".yml");
        return new File(this.usersFolderFile, sb.toString());
    }

    public File getRegionFile(Region region) {
        StringBuilder sb = new StringBuilder();
        sb.append(region.getName());
        sb.append(".yml");
        return new File(this.regionsFolderFile, sb.toString());
    }

    public File getGuildFile(Guild guild) {
        StringBuilder sb = new StringBuilder();
        sb.append(guild.getName());
        sb.append(".yml");
        return new File(this.guildsFolderFile, sb.toString());
    }

    @Override
    public void load() {
        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        this.validateLoadedData();
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.saveUsers(ignoreNotChanged);
        this.saveRegions(ignoreNotChanged);
        this.saveGuilds(ignoreNotChanged);
    }

    private void saveUsers(boolean ignoreNotChanged) {
        if (UserUtils.getUsers().isEmpty()) {
            return;
        }

        for (User user : UserUtils.getUsers()) {
            if (user.getUUID() != null && user.getName() != null) {
                if (! ignoreNotChanged) {
                    if (! user.wasChanged()) {
                        continue;
                    }
                }

                new FlatUser(user).serialize(this);
            }
        }
    }

    private void loadUsers() {
        int i = 0;
        File[] path = usersFolderFile.listFiles();

        if (path != null) {
            for (File file : path) {
                if (file.isDirectory() || file.length() == 0) {
                    file.delete();
                    i++;
                    continue;
                }

                if (!UserUtils.validateUsername(StringUtils.removeEnd(file.getName(), ".yml"))) {
                    FunnyGuilds.getInstance().getPluginLogger().warning("Skipping loading of user file '" + file.getName() + "'. Name is invalid.");
                    continue;
                }

                User user = FlatUser.deserialize(file);

                if (user == null) {
                    file.delete();
                    i++;
                }
                else {
                    user.wasChanged();
                }
            }
        }

        if (i > 0) {
            FunnyGuilds.getInstance().getPluginLogger().warning("Repaired conflicts: " + i);
        }

        FunnyGuilds.getInstance().getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size());
    }

    private void saveRegions(boolean ignoreNotChanged) {
        if (! FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        int i = 0;
        for (Region region : RegionUtils.getRegions()) {
            if (ignoreNotChanged) {
                if (! region.wasChanged()) {
                    continue;
                }
            }
            if (! new FlatRegion(region).serialize(this)) {
                RegionUtils.delete(region);
                i++;
            }
        }
        if (i > 0) {
            FunnyGuilds.getInstance().getPluginLogger().warning("Deleted defective regions: " + i);
        }
    }

    private void loadRegions() {
        if (! FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getInstance().getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        File[] path = regionsFolderFile.listFiles();

        if (path != null) {
            for (File file : path) {
                Region region = FlatRegion.deserialize(file);
                if (region == null) {
                    file.delete();
                }
                else {
                    region.wasChanged();
                }
            }
        }

        FunnyGuilds.getInstance().getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size());
    }

    private void saveGuilds(boolean ignoreNotChanged) {
        int deleted = 0;

        for (Guild guild : GuildUtils.getGuilds()) {
            if (ignoreNotChanged && ! guild.wasChanged()) {
                continue;
            }

            if (! new FlatGuild(guild).serialize(this)) {
                GuildUtils.deleteGuild(guild);
                deleted++;
            }
        }

        if (deleted > 0) {
            FunnyGuilds.getInstance().getPluginLogger().warning("Deleted defective guild: " + deleted);
        }
    }

    private void loadGuilds() {
        GuildUtils.getGuilds().clear();
        File[] path = guildsFolderFile.listFiles();

        if (path != null) {
            for (File file : path) {
                Guild guild = FlatGuild.deserialize(file);

                if (guild == null) {
                    file.delete();
                }
                else {
                    guild.wasChanged();
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

        FunnyGuilds.getInstance().getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size());
    }

}
