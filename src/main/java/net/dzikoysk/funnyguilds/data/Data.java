package net.dzikoysk.funnyguilds.data;

public class Data {

    private static Data instance;

    public Data() {
    }

    public static Data getInstance() {
        if (instance != null)
            return instance;
        return new Data();
    }

}
