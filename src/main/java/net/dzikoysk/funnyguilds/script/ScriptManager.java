package net.dzikoysk.funnyguilds.script;

import net.dzikoysk.funnyguilds.util.IOUtils;
import net.dzikoysk.panda.util.reflect.PandaReflection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptManager {

    private static ScriptManager instance;
    private List<PandaReflection> reflects;

    public ScriptManager() {
        instance = this;
        reflects = new ArrayList<>();
    }

    public void start() {
        File home = IOUtils.getFile("plugins/FunnyGuilds/scripts", true);
        File[] files = home.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            if (file.getName().endsWith(".reflect")) {
                reflects.add(new PandaReflection(file));
            }
        }
        run();
    }

    public void run() {
        for (PandaReflection reflect : reflects) {
            reflect.getWorker().run();
        }
    }

    public static ScriptManager getInstance() {
        if (instance == null) {
            new ScriptManager();
        }
        return instance;
    }

}
