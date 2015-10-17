package net.dzikoysk.funnyguilds.data.core;

import net.dzikoysk.funnyguilds.basic.Basic;

public interface Data {

    public void load();

    public void save(Basic basic, String... fields);

    public void openBuffer();

    public void closeBuffer();

    public boolean isOpened();

}
