package net.dzikoysk.funnyguilds.util.element;

import java.util.*;

public class PlayerListScheme {

    private static final String[] colorsCode = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static String[] scheme = new String[60];
    private static List<Integer> edit = new ArrayList<>();

    public PlayerListScheme(String[] ss) {
        scheme = ss;
        this.update();
    }

    public static String[] uniqueFields() {
        List<String> fields = new LinkedList<>();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(".r");
        for (int i = 0; i < 60; i++) {
            while (fields.contains(sb.toString())) {
                sb.setLength(0);
                for (int x = 0; x < 3; x++) {
                    String r = colorsCode[random.nextInt(colorsCode.length)];
                    sb.append(".");
                    sb.append(r);
                }
            }
            fields.add(sb.toString());
        }
        Collections.sort(fields);
        return fields.toArray(new String[60]);
    }

    public static String[] getScheme() {
        return scheme.clone();
    }

    public static List<Integer> getEdit() {
        return edit;
    }

    private void update() {
        edit.clear();
        for (int i = 0; i < 60; i++)
            if (scheme[i] != null && scheme[i].contains("{") && scheme[i].contains("}"))
                edit.add(i);
    }
}
