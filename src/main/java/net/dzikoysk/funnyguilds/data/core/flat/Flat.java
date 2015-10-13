package net.dzikoysk.funnyguilds.data.core.flat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.data.core.Data;

import java.io.File;

public class Flat implements Data {

    public static final File DATA = new File(FunnyGuilds.getFolder() + "data");
    public static final File USERS = new File(DATA + File.separator + "users");
    public static final File GUILDS = new File(DATA + File.separator + "guilds");
    public static final File REGIONS = new File(DATA + File.separator + "regions");

    private static Flat instance;
    private UsersManager users;
    private GuildsManager guilds;
    private RegionsManager regions;
    private boolean buffer;

    private Flat() {
        this.users = new UsersManager();
        this.guilds = new GuildsManager();
        this.regions = new RegionsManager();
    }

    public static Flat getInstance() {
        if (instance == null)
            instance = new Flat();
        return instance;
    }

    @Override
    public void load() {
        this.regions.load();
        this.guilds.load();
    }

    @Override
    public void save(Basic basic, String... fields) {
        switch (basic.getType()) {
            case USER:
                this.users.save(basic, fields);
                break;
            case GUILD:
                this.guilds.save(basic, fields);
                break;
            case REGION:
                this.regions.save(basic, fields);
                break;
            default:
                break;
        }
    }

    @Override
    public void openBuffer() {
        this.buffer = true;
        this.users.openBuffer();
        this.guilds.openBuffer();
        this.regions.openBuffer();
    }

    @Override
    public void closeBuffer() {
        this.buffer = false;
        this.users.closeBuffer();
        this.guilds.closeBuffer();
        this.regions.closeBuffer();
    }

    @Override
    public boolean isOpened() {
        return this.buffer;
    }

}
