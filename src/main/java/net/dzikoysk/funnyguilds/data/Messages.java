package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.ConfigUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class Messages {

    private static MessagesConfig instance;
    private static File messages = new File(FunnyGuilds.getInstance().getDataFolder(), "messages.yml");

    public Messages() {
        instance = ConfigUtils.loadConfig(messages, MessagesConfig.class);

        try {
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.set(instance, StringUtils.colored((String) field.get(instance)));
                }
                
                if (field.getType() == List.class) {
                    @SuppressWarnings("unchecked") final List<String> list = (List<String>) field.get(instance);
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, StringUtils.colored(list.get(i)));
                    }
                }
            }
        } catch (Exception e) {
            FunnyGuilds.exception(e);
        }
    }

    public static MessagesConfig getInstance() {
        if (instance == null) {
            new Messages();
        }
        
        return instance;
    }

}
