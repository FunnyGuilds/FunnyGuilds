package net.dzikoysk.funnyguilds.system.event;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.panda.util.configuration.PandaConfiguration;

import java.io.File;

public class EventSettings {

    private static final File FILE = new File(FunnyGuilds.getInstance().getDataFolder(), "events.yml");
    private static final String VERSION = "3.5 NewYear";
    private static EventSettings instance;

    public boolean christmasEvent;
    public boolean newYearEvent;

    public EventSettings() {
        instance = this;
        FILE.delete();
    }

    protected void load() {
        Manager.loadDefaultFiles(new String[]{ "events.yml" });
        PandaConfiguration pc = new PandaConfiguration(FILE);
        String version = pc.getString("version");
        if (version == null || !version.equals(VERSION)) {
            FILE.delete();
            Manager.loadDefaultFiles(new String[]{ "events.yml" });
            pc = new PandaConfiguration(FILE);
        }
        christmasEvent = pc.getBoolean("christmas-event");
        newYearEvent = pc.getBoolean("newYear-event");
    }

    public static EventSettings getInstance() {
        if (instance == null) {
            new EventSettings();
        }
        return instance;
    }
}
