package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.Yamler;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Messages {

    private static Messages instance;
    private static String version = "4.0";
    private static File messages = new File(FunnyGuilds.getInstance().getDataFolder(), "messages.yml");

    private HashMap<String, String> single = new HashMap<>();
    private HashMap<String, List<String>> multiple = new HashMap<>();

    public Messages() {
        instance = this;
        Manager.loadDefaultFiles(new String[]{"messages.yml"});
        Yamler pc = loadConfiguration();
        if (pc == null) {
            FunnyGuilds.error("[Messages] Messages.yml not loaded!");
            return;
        }
        for (String key : pc.getKeys(true)) {
            if (key.toLowerCase().contains("list")) {
                List<String> list = pc.getStringList(key);
                if (list == null) {
                    continue;
                }
                for (int i = 0; i < list.size(); i++) {
                    list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i))
                            .replace("�", "")
                            .replace("�", "")
                    );
                }
                multiple.put(key, list);
                continue;
            }
            String message = ChatColor.translateAlternateColorCodes('&', pc.getString(key));
            single.put(key, message
                    .replace("�", "")
                    .replace("�", "")
            );
        }
    }

    private Yamler loadConfiguration() {
        Yamler pc = new Yamler(messages);
        String version = pc.getString("version");
        if (version != null && version.equals(Messages.version)) {
            return pc;
        }
        FunnyGuilds.info("Updating the plugin messages ...");
        messages.renameTo(new File(FunnyGuilds.getInstance().getDataFolder(), "messages.old"));
        Manager.loadDefaultFiles(new String[]{"messages.yml"});
        pc = new Yamler(messages);
        FunnyGuilds.info("Successfully updated messages!");
        return pc;
    }

    public String getMessage(String key) {
        String s = single.get(key);
        if (s == null) {
            return StringUtils.colored("&cMessage '" + key + "' not found");
        }
        else {
            return s;
        }
    }

    public List<String> getList(String key) {
        List<String> list = multiple.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.isEmpty()) {
            list.add(StringUtils.colored("&cMessage '" + key + "' not found"));
        }
        return list;
    }

    public static Messages getInstance() {
        if (instance != null) {
            return instance;
        }
        return new Messages();
    }

}
