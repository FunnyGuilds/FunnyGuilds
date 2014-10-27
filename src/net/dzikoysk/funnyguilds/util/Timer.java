package net.dzikoysk.funnyguilds.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

public class Timer implements Runnable {
	
	private static Timer instance;
	private transient long lastPoll = System.nanoTime();
	private final LinkedList<Double> history = new LinkedList<>();
	private static double tps;
	  
	public Timer(){
		instance = this;
		this.history.add(Double.valueOf(20.0D));
	}

	@Override
	public void run(){
		long startTime = System.nanoTime();
	    long timeSpent = (startTime - this.lastPoll) / 1000L;
	    if (timeSpent == 0L) timeSpent = 1L;
	    if (this.history.size() > 10) this.history.remove();
	    double tps = 50000000.0D / timeSpent;
	    if (tps <= 21.0D) this.history.add(Double.valueOf(tps));
	    this.lastPoll = startTime;
	    this.calc();
	}
	
	private void calc(){
		double avg = 0.0D;
	    for (Double f : this.history){ 
	    	if (f != null){
	    		avg += f.doubleValue();
	    	}
	    }
	    tps = new BigDecimal(avg / this.history.size()).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
	}
	
	public static double getTPS(){
		return tps;
	}
	
	public static Timer getInstance(){
		return instance;
	}
}
