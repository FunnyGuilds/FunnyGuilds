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

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.manager.DependencyManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum Dependency {
	VAULT("Vault", true),
	VANISHNOPACKET("VanishNoPacket", false),
	ESSENTIALS("Essentials", false),
	BOSSBARAPI("BossBarAPI", false),
	BARAPI("BarAPI", false),
	SCOREBOARDSTATS("ScoreboardStats", false),
	WORLDGUARD("WorldGuard", false, new DependencyManager.WorldGuardFlagInjector()),
	DYNMAP("dynmap", false),
	HOLOGRAPHICDISPLAYS("HolographicDisplays", false, new DependencyManager.HolographicDisplaysAPIChecker());

	private final String name;
	private final boolean hardDependency;
	private final Set<DependencyManager.AdditionalTask> additionalTasks = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param name           the name
	 * @param hardDependency true if required
	 */
	Dependency(String name, boolean hardDependency) {
		this.name = name;
		this.hardDependency = hardDependency;
	}

	/**
	 * The constructor
	 *
	 * @param name            the name
	 * @param hardDependency  true if required
	 * @param additionalTasks additional tasks
	 */
	Dependency(String name, boolean hardDependency, DependencyManager.AdditionalTask... additionalTasks) {
		this(name, hardDependency);
		Collections.addAll(this.additionalTasks, additionalTasks);
	}

	/**
	 * Gets the name
	 *
	 * @return dependency name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if the dependency is required (hard)
	 *
	 * @return boolean
	 */
	public boolean isHardDependency() {
		return hardDependency;
	}

	/**
	 * Checks if there are additional tasks
	 *
	 * @return boolean
	 */
	public boolean hasAdditionalTasks() {
		return !additionalTasks.isEmpty();
	}

	/**
	 * Gets additional tasks
	 *
	 * @return set of runnables
	 */
	public Set<DependencyManager.AdditionalTask> getAdditionalTasks() {
		return additionalTasks;
	}
}
