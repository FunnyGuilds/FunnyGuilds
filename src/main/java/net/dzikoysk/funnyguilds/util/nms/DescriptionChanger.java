package net.dzikoysk.funnyguilds.util.nms;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;

public final class DescriptionChanger {

    private final PluginDescriptionFile descriptionFile;

    public DescriptionChanger(PluginDescriptionFile descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public void rename(String pluginName) {
        if (pluginName == null || pluginName.isEmpty()) {
            return;
        }
        
        try {
            Field field = Reflections.getPrivateField(descriptionFile.getClass(), "name");

            if (field == null) {
                return;
            }

            field.set(descriptionFile, pluginName);
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not change description file", ex);
        }
    }

    public String[] extractVersion() {
        String version = descriptionFile.getVersion();
        return new String[]{version, version.split("-")[0]};
    }

}
