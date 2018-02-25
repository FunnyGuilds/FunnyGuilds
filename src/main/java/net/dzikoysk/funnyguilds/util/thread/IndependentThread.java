package net.dzikoysk.funnyguilds.util.thread;

import com.google.common.collect.ImmutableList;
import net.dzikoysk.funnyguilds.util.FunnyLogger;

import java.util.ArrayList;
import java.util.List;

public class IndependentThread extends Thread {

    private static IndependentThread instance;
    private static List<Action> temp = new ArrayList<>();
    private final Object locker = new Object();
    private List<Action> actions = new ArrayList<>();

    public IndependentThread() {
        super("FunnyGuilds | IndependentThread");
        instance = this;
        FunnyLogger.info("Available Processors: " + Runtime.getRuntime().availableProcessors());
        FunnyLogger.info("Active Threads: " + Thread.activeCount());
    }

    public static IndependentThread getInstance() {
        try {
            if (instance == null) {
                throw new UnsupportedOperationException("IndependentThread is not setup!");
            }
            
            return instance;
        } catch (Exception ex) {
            if (FunnyLogger.exception(ex.getCause())) {
                ex.printStackTrace();
            }
            
            return null;
        }
    }

    private static void action(Action... actions) {
        IndependentThread it = getInstance();

        for (Action action : ImmutableList.copyOf(temp)) {
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
                if (FunnyLogger.exception(e.getCause())) {
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
                if (FunnyLogger.exception(e.getCause())) {
                    e.printStackTrace();
                }
            }
        }
    }

}
