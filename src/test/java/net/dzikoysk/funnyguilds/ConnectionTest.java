package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.util.IOUtils;

public class ConnectionTest {

    public static void main(String[] args) {
        String content = IOUtils.getContent("https://dzikoysk.net/projects/funnyguilds/latest.info");
        System.out.println("Content: " + content);
    }

}
