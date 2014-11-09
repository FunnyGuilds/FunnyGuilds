package net.dzikoysk.funnyguilds.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;

public class ThreadMonitor implements Runnable{
	
	private long prevUpTime;
	private List<Long> prevThreadCpuTime = new ArrayList<>();
	
	public void start(){
		Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), this, 0, 100);
	}
	
	@Override
	public void run(){
		ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
		long upTime = ManagementFactory.getRuntimeMXBean().getUptime();
		
		List<Thread> threadIds = new ArrayList<Thread>();
		threadIds.addAll(Thread.getAllStackTraces().keySet());
		
        List<Long> threadCpuTime = new ArrayList<Long>();
        for (int i = 0; i < threadIds.size(); i++) {
            long threadId = threadIds.get(i).getId();
            if (threadId != -1) {
                threadCpuTime.add(tmxb.getThreadCpuTime(threadId));
            } else {
                threadCpuTime.add(0L);
            }
        }
        int nCPUs = Runtime.getRuntime().availableProcessors();
        List<Float> cpuUsageList = new ArrayList<Float>();
        
        if (prevUpTime > 0L && upTime > prevUpTime) {
            long elapsedTime = upTime - prevUpTime;
            for (int i = 0; i < threadIds.size(); i++) {
            	if(i < threadCpuTime.size() && i < prevThreadCpuTime.size()){
	                long elapsedCpu = threadCpuTime.get(i) - prevThreadCpuTime.get(i);
	                float cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 1000000F * nCPUs));
	                cpuUsageList.add(cpuUsage);
            	}
            }
        }
        
        prevUpTime = upTime;
        prevThreadCpuTime = threadCpuTime;
        
        for(int i = 0; i < threadIds.size(); i++){
        	if(i < cpuUsageList.size())
        		System.out.println(threadIds.get(i).getName() + ": " + cpuUsageList.get(i));
        }
	}

}
