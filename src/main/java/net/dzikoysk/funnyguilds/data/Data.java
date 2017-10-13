package net.dzikoysk.funnyguilds.data;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.Reloader;
import net.dzikoysk.funnyguilds.util.Yamler;
import org.bukkit.Bukkit;
import org.panda_lang.panda.util.configuration.PandaConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

public class Data {

    private static final File DATA = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data");
    private static Data instance;
    private static File folder;

    public Data() {
        folder = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "data");
        instance = this;
        funnyguilds(DO.LOAD);
        invitations(DO.LOAD);
    }

    public void save() {
        funnyguilds(DO.SAVE);
        invitations(DO.SAVE);
    }

    private void funnyguilds(DO todo) {
        File file = new File(folder, "funnyguilds.dat");
        PandaConfiguration pc = new PandaConfiguration(file);
        if (todo == DO.SAVE) {
            pc.set("played-before", Bukkit.getOnlinePlayers().size());
            pc.set("reload-count", Reloader.getReloadCount());
            pc.save();
        }
    }

    private void invitations(DO todo) {
        File file = new File(folder, "invitations.yml");
        if (todo == DO.SAVE) {
            file.delete();
            Yamler pc = new Yamler(file);
            for (Entry<String, List<String>> entry : InvitationsList.entrySet()) {
                pc.set(entry.getKey(), entry.getValue());
            }
            pc.save();
            pc = null;
        } else if (todo == DO.LOAD) {
            if (!file.exists()) {
                return;
            }
            Yamler pc = new Yamler(file);
            for (String key : pc.getKeys(true)) {
                String[] check = key.split(",");
                if (check[0].equalsIgnoreCase("U")) {
                    InvitationsList.get(User.get(check[0]), Integer.valueOf(check[1])).set(pc.getStringList(key));
                }
                if (check[0].equalsIgnoreCase("G")) {
                    Guild guild = GuildUtils.get(check[0]);
                    int i = Integer.valueOf(check[1]);
                    if (guild != null) {
                        InvitationsList.get(guild, i).set(pc.getStringList(key));
                    }
                }
            }
            pc = null;
        }

    }

    public static File getPlayerListFile() {
        return new File(folder, "playerlist.yml");
    }

    public static File getDataFolder() {
        return DATA;
    }

    public static Data getInstance() {
        if (instance != null) {
            return instance;
        }
        return new Data();
    }

    private enum DO {
        SAVE,
        LOAD
    }

}
