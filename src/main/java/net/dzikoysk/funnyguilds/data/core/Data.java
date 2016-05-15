package net.dzikoysk.funnyguilds.data.core;

import net.dzikoysk.funnyguilds.basic.Basic;

public interface Data {

    void load();

    void save(Basic basic, String... fields);

    void openBuffer();

    void closeBuffer();

    boolean isOpened();

}
