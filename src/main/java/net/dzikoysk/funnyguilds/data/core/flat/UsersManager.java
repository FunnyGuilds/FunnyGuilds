package net.dzikoysk.funnyguilds.data.core.flat;

import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.data.core.Data;
import net.dzikoysk.funnyguilds.util.IOUtils;
import net.dzikoysk.panda.util.configuration.PandaConfiguration;

import java.io.File;
import java.util.UUID;

public class UsersManager implements Data {

    private static final File USERS = Flat.USERS;
    private boolean buffer;

    public UsersManager() {

    }

    public static File getUserFile(String user) {
        return new File(USERS + File.separator + user.charAt(1) + File.separator + user + ".pc");
    }

    @Override
    public void load() {
        IOUtils.initialize(USERS, true);
        File[] indexes = USERS.listFiles();
        if (indexes == null || indexes.length == 0)
            return;
        for (File index : indexes) {
            File[] files = index.listFiles();
            if (files == null || files.length == 0)
                continue;
            for (File file : files)
                load(new PandaConfiguration(file));
        }
    }

    public void load(String user) {
        if (user == null || user.isEmpty())
            return;
        File file = getUserFile(user);
        if (!file.exists())
            return;
        PandaConfiguration pc = new PandaConfiguration(file);
        load(pc);
    }

    public User load(PandaConfiguration pc) {
        User user = User.get(UUID.fromString(pc.getString("uuid")));
        user.setName(pc.getString("name"));
        user.setBan(pc.getLong("ban"));
        user.setReason(pc.getString("reason"));
        Rank rank = user.getRank();
        rank.setPoints(pc.getInt("points"));
        rank.setDeaths(pc.getInt("deaths"));
        rank.setKills(pc.getInt("kills"));
        return user;
    }

    @Override
    public void save(Basic basic, String... fields) {
        if (basic.getType() != BasicType.USER)
            return;
        if (this.buffer) {

        } else {
            User user = (User) basic;
            PandaConfiguration pc = new PandaConfiguration(getUserFile(user.getName()));
            save(pc, user, fields);
        }
    }

    public void save(PandaConfiguration pc, User user, String... fields) {
        for (String field : fields)
            try {
                if (field.equals("rank")) {
                    Rank rank = user.getVariable("rank", Rank.class);
                    pc.set("points", Integer.toString(rank.getPoints()));
                    pc.set("kills", Integer.toString(rank.getKills()));
                    pc.set("deaths", Integer.toString(rank.getDeaths()));
                } else
                    pc.set(field, user.getVariable(field).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        pc.save();
    }

    @Override
    public void openBuffer() {
        this.buffer = true;
    }

    @Override
    public void closeBuffer() {
        this.buffer = false;
        for (; ; ) {

        }
    }

    @Override
    public boolean isOpened() {
        return this.buffer;
    }

}
