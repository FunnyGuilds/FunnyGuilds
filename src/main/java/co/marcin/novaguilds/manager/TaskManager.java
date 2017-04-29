/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableInactiveCleaner;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.runnable.RunnableRefreshHolograms;
import co.marcin.novaguilds.runnable.RunnableRefreshTabList;
import co.marcin.novaguilds.util.LoggerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskManager {
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	private final Map<Task, ScheduledFuture<?>> taskRunnableMap = new HashMap<>();

	public enum Task {
		AUTOSAVE(null, RunnableAutoSave.class, Config.SAVEINTERVAL),
		LIVEREGENERATION(null, RunnableLiveRegeneration.class, Config.LIVEREGENERATION_TASKINTERVAL),
		CLEANUP(Config.CLEANUP_ENABLED, RunnableInactiveCleaner.class, Config.CLEANUP_STARTUPDELAY, Config.CLEANUP_INTERVAL),
		HOLOGRAM_REFRESH(Config.HOLOGRAPHICDISPLAYS_ENABLED, RunnableRefreshHolograms.class, Config.HOLOGRAPHICDISPLAYS_REFRESH),
		TABLIST_REFRESH(Config.TABLIST_ENABLED, RunnableRefreshTabList.class, Config.TABLIST_REFRESH);

		private final ConfigWrapper start;
		private final ConfigWrapper interval;
		private final ConfigWrapper condition;
		private final Class clazz;

		/**
		 * Task enum constructor
		 *
		 * @param condition condition Config enum (boolean)
		 * @param clazz     Runnable class
		 * @param both      both time interval and start delay
		 */
		Task(ConfigWrapper condition, Class<? extends Runnable> clazz, ConfigWrapper both) {
			this.clazz = clazz;
			this.start = both;
			this.interval = both;
			this.condition = condition;
		}

		/**
		 * Task enum constructor
		 *
		 * @param condition condition Config enum (boolean)
		 * @param clazz     Runnable class
		 * @param start     delay after running the task for the first time
		 * @param interval  time interval
		 */
		Task(ConfigWrapper condition, Class<? extends Runnable> clazz, ConfigWrapper start, ConfigWrapper interval) {
			this.clazz = clazz;
			this.start = start;
			this.interval = interval;
			this.condition = condition;
		}

		/**
		 * Gets Runnable class
		 *
		 * @return the Class
		 */
		public Class getClazz() {
			return clazz;
		}

		/**
		 * Gets start delay
		 *
		 * @return seconds
		 */
		public long getStart() {
			return start.getSeconds();
		}

		/**
		 * Gets the interval
		 *
		 * @return seconds
		 */
		public long getInterval() {
			return interval.getSeconds();
		}

		/**
		 * Checks Task's condition
		 *
		 * @return boolean
		 */
		public boolean checkCondition() {
			return condition == null || condition.getBoolean();
		}
	}

	/**
	 * Starts a task
	 *
	 * @param task the task
	 */
	public void startTask(Task task) {
		if(isStarted(task)) {
			LoggerUtils.info("Task " + task.name() + " has been already started");
			return;
		}

		try {
			Runnable taskInstance = (Runnable) task.getClazz().newInstance();
			ScheduledFuture<?> future = worker.scheduleAtFixedRate(taskInstance, task.getStart(), task.getInterval(), TimeUnit.SECONDS);
			taskRunnableMap.put(task, future);
		}
		catch(InstantiationException | IllegalAccessException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Stops a task
	 *
	 * @param task the task
	 */
	public void stopTask(Task task) {
		if(isStarted(task)) {
			taskRunnableMap.get(task).cancel(true);
			LoggerUtils.info("Task " + task.name() + " has been stopped");
		}
	}

	/**
	 * Stops all tasks
	 */
	public void stopTasks() {
		for(Task task : taskRunnableMap.keySet()) {
			stopTask(task);
		}
	}

	/**
	 * Checks if a task is running
	 *
	 * @param task the task
	 * @return boolean
	 */
	public boolean isStarted(Task task) {
		return taskRunnableMap.containsKey(task) && !taskRunnableMap.get(task).isCancelled();
	}

	/**
	 * Runs all task depending on the config
	 */
	public void runTasks() {
		for(Task task : Task.values()) {
			boolean condition = task.checkCondition();

			if(condition && !isStarted(task)) {
				startTask(task);
				LoggerUtils.info("Task " + task.name() + " has been started");
			}
			else if(isStarted(task) && !condition) {
				stopTask(task);
				LoggerUtils.info("Task " + task.name() + " has been stopped");
			}
		}
	}
}
