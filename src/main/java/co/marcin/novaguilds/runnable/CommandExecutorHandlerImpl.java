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

package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.CommandExecutorHandler;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class CommandExecutorHandlerImpl implements CommandExecutorHandler {
	private final CommandSender sender;
	private final CommandWrapper command;
	private final String[] args;
	private State state = State.WAITING;
	private final BukkitTask bukkitTask;
	private Resource executorVariable;

	/**
	 * The constructor
	 *
	 * @param command the command
	 * @param sender  command sender
	 * @param args    arguments
	 */
	public CommandExecutorHandlerImpl(CommandWrapper command, CommandSender sender, String[] args) {
		this.command = command;
		this.sender = sender;
		this.args = args;

		if(command.hasFlag(CommandWrapper.Flag.CONFIRM)) {
			bukkitTask = Bukkit.getScheduler().runTaskLater(NovaGuilds.getInstance(), this, Config.CHAT_CONFIRMTIMEOUT.getSeconds() * 20);
		}
		else {
			bukkitTask = null;
		}
	}

	@Override
	public void execute() {
		if(getState() == State.CONFIRMED || !command.hasFlag(CommandWrapper.Flag.CONFIRM)) {
			command.executorVariable(executorVariable);
			command.execute(sender, args);
			PlayerManager.getPlayer(sender).removeCommandExecutorHandler();
		}
	}

	@Override
	public void cancel() {
		state = State.CANCELED;
		bukkitTask.cancel();
		PlayerManager.getPlayer(sender).removeCommandExecutorHandler();
	}

	@Override
	public void confirm() {
		if(state != State.CANCELED) {
			state = State.CONFIRMED;
			execute();
		}
	}

	@Override
	public void run() {
		if(state == State.WAITING) {
			cancel();
			Message.CHAT_CONFIRM_TIMEOUT.send(sender);
		}
	}

	@Override
	public CommandWrapper getCommand() {
		return command;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public Resource getExecutorVariable() {
		return executorVariable;
	}

	@Override
	public void executorVariable(Resource executorVariable) {
		this.executorVariable = executorVariable;
	}
}
