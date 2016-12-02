package net.dzikoysk.funnyguilds.data.util;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class InvitationsList {

    private static List<InvitationsList> is = new ArrayList<>();

    private int i;
    private Object o;
    private List<String> ls;

    private InvitationsList(Object o, int i) {
        this.i = i;
        this.o = o;
        this.ls = new ArrayList<String>();
        is.add(this);
    }

    public void set(int i) {
        this.i = i;
    }

    public void set(List<String> ls) {
        this.ls = ls;
    }

    public boolean contains(String s) {
        if (ls == null) {
            ls = new ArrayList<String>();
        }
        return this.ls.contains(s.toLowerCase());
    }

    public void add(String s) {
        if (!ls.contains(s.toLowerCase())) {
            this.ls.add(s.toLowerCase());
        }
    }

    public void remove(String s) {
        this.ls.remove(s.toLowerCase());
    }

    public static List<InvitationsList> getIS() {
        return is;
    }

    public int getType() {
        return i;
    }

    public Object getO() {
        return o;
    }

    public List<String> getLS() {
        return ls;
    }

    public static InvitationsList get(Object o, int i) {
        for (InvitationsList x : is) {
            if (x.getO().equals(o) && x.getType() == i) {
                return x;
            }
        }
        return new InvitationsList(o, i);
    }

    public static Set<Entry<String, List<String>>> entrySet() {
        HashMap<String, List<String>> to = new HashMap<String, List<String>>();
        for (InvitationsList x : is) {
            StringBuilder sb = new StringBuilder();
            if (x.getO() instanceof User) {
                sb.append("U,");
                sb.append(x.getType());
                sb.append(",");
                sb.append(((User) x.getO()).getName());
            }
            else if (x.getO() instanceof Guild) {
                sb.append("G,");
                sb.append(x.getType());
                sb.append(",");
                sb.append(((Guild) x.getO()).getName());
            }
            to.put(sb.toString(), x.getLS());
        }
        return to.entrySet();
    }

}
