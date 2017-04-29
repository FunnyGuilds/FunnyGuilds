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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.CommandExecutorHandler;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.runnable.CommandExecutorHandlerImpl;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NovaPlayerImpl extends AbstractResource implements NovaPlayer {
	private int id = 0;
	private String name;
	private NovaGuild guild;
	private int points = 0;
	private int kills = 0;
	private int deaths = 0;

	private final Collection<Vehicle> vehicles = new HashSet<>();
	private final List<NovaGuild> invitedTo = new ArrayList<>();
	private final List<GUIInventory> guiInventoryHistory = new ArrayList<>();
	private final Map<UUID, Long> killingHistory = new HashMap<>();
	private final Preferences preferences = new PreferencesImpl();

	private NovaRaid partRaid;
	private NovaRank guildRank;
	private NovaRegion atRegion;
	private TabList tabList;
	private CommandExecutorHandler commandExecutorHandler;
	private RegionSelection activeSelection;

	/**
	 * The constructor
	 *
	 * @param uuid the UUID
	 */
	public NovaPlayerImpl(UUID uuid) {
		super(uuid);
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(getUUID());
	}

	@Override
	public NovaGuild getGuild() {
		return guild;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<NovaGuild> getInvitedTo() {
		return invitedTo;
	}

	@Override
	public RegionSelection getActiveSelection() {
		return activeSelection;
	}

	@Override
	public NovaRegion getAtRegion() {
		return atRegion;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public int getDeaths() {
		return deaths;
	}

	@Override
	public int getKills() {
		return kills;
	}

	@Override
	public double getKillDeathRate() {
		return NumberUtils.roundOffTo2DecPlaces((double) getKills() / (getDeaths() == 0 ? 1 : (double) getDeaths()));
	}

	@Override
	public double getMoney() {
		OfflinePlayer oPlayer = CompatibilityUtils.getOfflinePlayer(getUUID());

		if(!oPlayer.hasPlayedBefore() || oPlayer.getName() == null) {
			return 0D;
		}

		return NovaGuilds.getInstance().getDependencyManager().getEconomy().getBalance(oPlayer);
	}

	@Override
	public TabList getTabList() {
		return tabList;
	}

	@Override
	public CommandExecutorHandler getCommandExecutorHandler() {
		return commandExecutorHandler;
	}

	@Override
	public NovaRaid getPartRaid() {
		return partRaid;
	}

	@Override
	public GUIInventory getGuiInventory() {
		return guiInventoryHistory.isEmpty() ? null : guiInventoryHistory.get(guiInventoryHistory.size() - 1);
	}

	@Override
	public List<GUIInventory> getGuiInventoryHistory() {
		return guiInventoryHistory;
	}

	@Override
	public NovaRank getGuildRank() {
		return guildRank;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		setChanged();
	}

	@Override
	public Preferences getPreferences() {
		return preferences;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChanged();
	}

	@Override
	public void setInvitedTo(Collection<NovaGuild> list) {
		invitedTo.clear();
		invitedTo.addAll(list);
		setChanged();
	}

	@Override
	public void setActiveSelection(RegionSelection selection) {
		activeSelection = selection;
	}

	@Override
	public void setAtRegion(NovaRegion region) {
		atRegion = region;
	}

	@Override
	public void setPoints(int points) {
		this.points = points;
		setChanged();
	}

	@Override
	public void setDeaths(int deaths) {
		this.deaths = deaths;
		setChanged();
	}

	@Override
	public void setKills(int kills) {
		this.kills = kills;
		setChanged();
	}

	@Override
	public void setTabList(TabList tabList) {
		this.tabList = tabList;
	}

	@Override
	public void setPartRaid(NovaRaid partRaid) {
		this.partRaid = partRaid;
	}

	@Override
	public void setGuiInventory(GUIInventory guiInventory) {
		if(guiInventory == null) {
			removeLastGUIInventoryHistory();
			return;
		}

		if(!guiInventory.equals(getGuiInventory())) {
			guiInventoryHistory.add(guiInventory);
		}
	}

	@Override
	public void setGuildRank(NovaRank guildRank) {
		if(this.guildRank != null) {
			this.guildRank.removeMember(this);
		}

		if(guildRank != null) {
			if(!hasPermission(GuildPermission.REGION_CREATE) && !hasPermission(GuildPermission.REGION_RESIZE)) {
				cancelToolProgress();
			}

			guildRank.addMember(this);
		}

		this.guildRank = guildRank;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean hasGuild() {
		return getGuild() != null;
	}

	@Override
	public boolean isOnline() {
		return getPlayer() != null;
	}

	@Override
	public boolean isInvitedTo(NovaGuild guild) {
		return invitedTo.contains(guild);
	}

	@Override
	public boolean isPartRaid() {
		return partRaid != null;
	}

	@Override
	public boolean isVehicleListed(Vehicle vehicle) {
		return vehicles.contains(vehicle);
	}

	@Override
	public boolean isLeader() {
		return hasGuild() && getGuild().isLeader(this);
	}

	@Override
	public boolean isAtRegion() {
		return atRegion != null;
	}

	@Override
	public boolean hasMoney(double money) {
		return getMoney() >= money;
	}

	@Override
	public boolean hasPermission(GuildPermission permission) {
		return guildRank != null && guildRank.hasPermission(permission);
	}

	@Override
	public boolean hasTabList() {
		return tabList != null;
	}

	@Override
	public boolean canGetKillPoints(Player player) {
		return !killingHistory.containsKey(player.getUniqueId()) || NumberUtils.systemSeconds() - killingHistory.get(player.getUniqueId()) > Config.KILLING_COOLDOWN.getSeconds();
	}

	@Override
	public void addInvitation(NovaGuild guild) {
		if(!isInvitedTo(guild)) {
			invitedTo.add(guild);
			setChanged();
		}
	}

	@Override
	public void addPoints(int points) {
		this.points += points;
		setChanged();
	}

	@Override
	public void addKill() {
		kills++;
		setChanged();
	}

	@Override
	public void addDeath() {
		deaths++;
		setChanged();
	}

	@Override
	public void addMoney(double money) {
		OfflinePlayer oPlayer = CompatibilityUtils.getOfflinePlayer(getUUID());

		if(!oPlayer.hasPlayedBefore() || oPlayer.getName() == null) {
			return;
		}

		NovaGuilds.getInstance().getDependencyManager().getEconomy().depositPlayer(oPlayer, money);
	}

	@Override
	public void addKillHistory(Player player) {
		if(killingHistory.containsKey(player.getUniqueId())) {
			killingHistory.remove(player.getUniqueId());
		}

		killingHistory.put(player.getUniqueId(), NumberUtils.systemSeconds());
	}

	@Override
	public void addVehicle(Vehicle vehicle) {
		if(!isVehicleListed(vehicle)) {
			vehicles.add(vehicle);
		}
	}

	@Override
	public void newCommandExecutorHandler(CommandWrapper command, String[] args) {
		commandExecutorHandler = new CommandExecutorHandlerImpl(command, getPlayer(), args);
		Message.CHAT_CONFIRM_NEEDCONFIRM.send(this);
	}

	@Override
	public void deleteInvitation(NovaGuild guild) {
		invitedTo.remove(guild);
		setChanged();
	}

	@Override
	public void takePoints(int points) {
		this.points -= points;
		setChanged();
	}

	@Override
	public void takeMoney(double money) {
		OfflinePlayer oPlayer = CompatibilityUtils.getOfflinePlayer(getUUID());

		if(!oPlayer.hasPlayedBefore() || oPlayer.getName() == null) {
			return;
		}

		NovaGuilds.getInstance().getDependencyManager().getEconomy().withdrawPlayer(oPlayer, money);
	}

	@Override
	public void cancelToolProgress() {
		if(isOnline()) {
			if(getActiveSelection() != null) {
				getActiveSelection().reset();
			}

			if(getPreferences().getRegionMode() == RegionMode.RESIZE) {
				getPreferences().setRegionMode(RegionMode.CHECK);
			}
		}
	}

	@Override
	public void removeCommandExecutorHandler() {
		commandExecutorHandler = null;
	}

	@Override
	public void removeLastGUIInventoryHistory() {
		getGuiInventoryHistory().remove(getGuiInventoryHistory().size() - 1);
	}

	public class PreferencesImpl implements Preferences {
		private boolean bypass;
		private boolean compassPointingGuild;
		private boolean spyMode;
		private boolean regionSpectate;
		private ChatMode chatMode = ChatMode.NORMAL;
		private RegionMode regionMode = RegionMode.CHECK;

		@Override
		public boolean getBypass() {
			return bypass;
		}

		@Override
		public ChatMode getChatMode() {
			return chatMode;
		}

		@Override
		public boolean isCompassPointingGuild() {
			return compassPointingGuild;
		}

		@Override
		public RegionMode getRegionMode() {
			return regionMode;
		}

		@Override
		public boolean getRegionSpectate() {
			return regionSpectate;
		}

		@Override
		public boolean getSpyMode() {
			return spyMode;
		}

		@Override
		public void setBypass(boolean bypass) {
			this.bypass = bypass;
		}

		@Override
		public void setChatMode(ChatMode chatMode) {
			this.chatMode = chatMode;
		}

		@Override
		public void setCompassPointingGuild(boolean compassPointingGuild) {
			this.compassPointingGuild = compassPointingGuild;
		}

		@Override
		public void setSpyMode(boolean spyMode) {
			this.spyMode = spyMode;
		}

		@Override
		public void setRegionMode(RegionMode regionMode) {
			this.regionMode = regionMode;
		}

		@Override
		public void setRegionSpectate(boolean regionSpectate) {
			this.regionSpectate = regionSpectate;
		}

		@Override
		public void toggleBypass() {
			bypass = !bypass;
		}

		@Override
		public void toggleRegionSpectate() {
			regionSpectate = !regionSpectate;
		}

		@Override
		public void toggleSpyMode() {
			spyMode = !spyMode;
		}

		@Override
		public void toggleCompassPointingGuild() {
			compassPointingGuild = !compassPointingGuild;
		}
	}
}
