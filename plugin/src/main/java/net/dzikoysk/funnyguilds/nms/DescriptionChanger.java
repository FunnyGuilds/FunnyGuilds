package net.dzikoysk.funnyguilds.nms;

import java.lang.reflect.Field;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.plugin.PluginDescriptionFile;
import panda.utilities.StringUtils;

public final class DescriptionChanger {

    private final PluginDescriptionFile descriptionFile;

    public DescriptionChanger(PluginDescriptionFile descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public void rename(String pluginName) {
        if (StringUtils.isEmpty(pluginName)) {
            return;
        }

        try {
            Field field = Reflections.getPrivateField(this.descriptionFile.getClass(), "name");
            if (field == null) {
                return;
            }

            field.set(this.descriptionFile, pluginName);
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not change description file", exception);
        }
    }

}
