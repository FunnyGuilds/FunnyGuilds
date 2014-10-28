package net.dzikoysk.funnyguilds.util;

import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;

public class IndependentThread extends Thread {
	
	private static IndependentThread instance;
	private List<Action> actions = new ArrayList<>();
	private static List<Action> temp = new ArrayList<>();
	private Object locker = new Object();
	
	public IndependentThread(){
		instance = this;
	}
	
	@Override
	public void run() {
		while(true) try {
			List<Action> currently = new ArrayList<Action>(actions);
			actions.clear();
			execute(currently);
			synchronized (locker){
				locker.wait();
			}
		} catch (InterruptedException e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
    }
	
	private void execute(List<Action> actions){
		for(Action action : actions){
			try {
				//int mb = 1024*1024;
		        //Runtime runtime = Runtime.getRuntime();	         
		        //long l = (runtime.totalMemory() - runtime.freeMemory()) / mb)
				action.execute();
				//long now = ((runtime.totalMemory() - runtime.freeMemory()) / mb) - l;
		        // if(now != 0) System.out.println("[" + action.getActionType().name() + "] Difference: " + now);
			} catch (Exception e) {
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
				continue;
			}
		}
	}
	
	public static void action(Action... actions){
		for(Action action : temp) getInstance().actions.add(action);
		for(Action action : actions) getInstance().actions.add(action);
		temp.clear();
		synchronized(getInstance().locker){
			getInstance().locker.notify();
		}
	}
	
	public static void action(ActionType type){
		action(new Action(type));
	}
	
	public static void action(ActionType type, Object... values){
		action(new Action(type, values));
	}
	
	public static void actions(ActionType type){
		temp.add(new Action(type));
	}
	
	public static void actions(ActionType type, Object... values){
		temp.add(new Action(type, values));
	}
	
	public static IndependentThread getInstance(){
		if(instance == null) new IndependentThread().start();
		return instance;
	}
}
