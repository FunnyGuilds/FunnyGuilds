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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.ChatBroadcast;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.impl.util.AbstractVarKeyApplicable;
import co.marcin.novaguilds.impl.util.ChatBroadcastImpl;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.ItemStackUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageWrapperImpl extends AbstractVarKeyApplicable<MessageWrapper> implements MessageWrapper {
	private String path;
	private final Set<Flag> flags = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param path  message path
	 * @param flags flags
	 */
	public MessageWrapperImpl(String path, Flag... flags) {
		this.path = path;

		for(Flag flag : flags) {
			if(flag == Flag.LIST) {
				this.flags.add(Flag.NOPREFIX);
			}
		}

		Collections.addAll(this.flags, flags);
	}

	/**
	 * The constructor
	 *
	 * @param path message path
	 */
	public MessageWrapperImpl(String path) {
		this(path, new Flag[0]);
	}

	/**
	 * The constructor
	 * path is null and has to be set.
	 *
	 * @param flags array with flags
	 */
	public MessageWrapperImpl(Flag... flags) {
		this(null, flags);
	}

	/**
	 * Clones the wrapper
	 *
	 * @param wrapper wrapper to clone
	 */
	public MessageWrapperImpl(MessageWrapper wrapper) {
		this(wrapper.getPath(), wrapper.getFlags().toArray(new Flag[wrapper.getFlags().size()]));
		vars(wrapper.getVars());
	}

	@Override
	public Set<Flag> getFlags() {
		return flags;
	}

	@Override
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	@Override
	public boolean getTitle() {
		return hasFlag(Flag.TITLE);
	}

	@Override
	public String getPath() {
		if(path == null) {
			throw new IllegalArgumentException("Path has not been set!");
		}

		return path;
	}

	@Override
	public String getName() {
		return StringUtils.replace(path, ".", "_").toUpperCase();
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean isPrefix() {
		return !hasFlag(Flag.NOPREFIX);
	}

	@Override
	public boolean isEmpty() {
		return get().equals("none");
	}

	@Override
	public void send(CommandSender sender) {
		if(hasFlag(Flag.LIST)) {
			MessageManager.sendMessagesList(sender, this);
		}
		else {
			MessageManager.sendMessagesMsg(sender, this);
		}
	}

	@Override
	public void send(NovaPlayer nPlayer) {
		if(nPlayer.isOnline()) {
			send(nPlayer.getPlayer());
		}
	}

	@Override
	public MessageWrapper prefix(boolean prefix) {
		if(prefix) {
			if(flags.contains(Flag.NOPREFIX)) {
				flags.remove(Flag.NOPREFIX);
			}
		}
		else {
			flags.add(Flag.NOPREFIX);
		}

		return this;
	}

	@Override
	public void broadcast(NovaGuild guild) {
		MessageManager.broadcast(guild, this);
	}

	@Override
	public void broadcast() {
		MessageManager.broadcast(this);
	}

	@Override
	public void broadcast(Permission permission) {
		MessageManager.broadcast(this, permission);
	}

	@Override
	public String get() {
		return MessageManager.replaceVarKeyMap(MessageManager.getMessagesString(this), vars, !hasFlag(Flag.NOAFTERVARCOLOR));
	}

	@Override
	public void set(String string) {
		MessageManager.set(this, string);
	}

	@Override
	public void set(List<String> list) {
		MessageManager.set(this, list);
	}

	@Override
	public ItemStack getItemStack() {
		return ItemStackUtils.stringToItemStack(get());
	}

	@Override
	public List<String> getList() {
		return co.marcin.novaguilds.util.StringUtils.fixColors(MessageManager.replaceVarKeyMap(MessageManager.getMessages().getStringList(getPath()), vars, !hasFlag(Flag.NOAFTERVARCOLOR)));
	}

	@Override
	public ConfigurationSection getConfigurationSection() {
		return MessageManager.getMessages().getConfigurationSection(getParentPath());
	}

	@Override
	public List<MessageWrapper> getNeighbours() {
		List<MessageWrapper> list = new ArrayList<>();
		String parentPath = getParentPath();
		for(String key : getConfigurationSection().getKeys(false)) {
			key = parentPath + "." + key;

			if(!key.equals(getPath())) {
				list.add(Message.fromPath(key).prefix(isPrefix()));
			}
		}

		return list;
	}

	@Override
	public String getParentPath() {
		String[] split = StringUtils.split(getPath(), ".");
		return StringUtils.removeEnd(getPath(), "." + split[split.length - 1]);
	}

	@Override
	public ChatBroadcast newChatBroadcast() {
		return new ChatBroadcastImpl(this);
	}

	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public MessageWrapper clone() {
		return new MessageWrapperImpl(this);
	}
}
