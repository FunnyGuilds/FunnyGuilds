package net.dzikoysk.funnyguilds.util.thread;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.util.ArrayList;
import java.util.List;

public class IndependentThread extends Thread {

    private static IndependentThread instance;
    private static List<Action> temp = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();
    private final Object locker = new Object();

    public IndependentThread() {
        instance = this;
        this.setName("IndependentThread");
        FunnyGuilds.info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        FunnyGuilds.info("Active Threads: " + Thread.activeCount());
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Action> currently = new ArrayList<>(actions);
                actions.clear();
                execute(currently);

                synchronized (locker) {
                    locker.wait();
                }
            } catch (InterruptedException e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void execute(List<Action> actions) {
        for (Action action : actions) {
            try {
                if (action == null) {
                    continue;
                }

                action.execute();
            } catch (Exception e) {
                if (FunnyGuilds.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static IndependentThread getInstance() {
        if (instance == null) {
            new IndependentThread().start();
        }

        return instance;
    }

    private static void action(Action... actions) {
        IndependentThread it = getInstance();

        for (Action action : temp) {
            if (!it.actions.contains(action)) {
                it.actions.add(action);
            }
        }

        for (Action action : actions) {
            if (!it.actions.contains(action)) {
                it.actions.add(action);
            }
        }

        temp.clear();

        synchronized (getInstance().locker) {
            getInstance().locker.notify();
        }
    }

    public static void action(ActionType type) {
        action(new Action(type));
    }

    public static void action(ActionType type, Object... values) {
        action(new Action(type, values));
    }

    public static void actions(ActionType type) {
        Action action = new Action(type);
        if (!temp.contains(action)) {
            temp.add(action);
        }
    }

    public static void actions(ActionType type, Object... values) {
        Action action = new Action(type, values);
        if (!temp.contains(action)) {
            temp.add(action);
        }
    }

}
