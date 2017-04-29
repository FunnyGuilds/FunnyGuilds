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

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.impl.basic.MessageWrapperImpl;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Message {
	public static MessageWrapper CHAT_PREFIX;
	public static MessageWrapper CHAT_NOPERMISSIONS;
	public static MessageWrapper CHAT_UNKNOWNCMD;
	public static MessageWrapper CHAT_INVALIDPARAM;
	public static MessageWrapper CHAT_CMDFROMCONSOLE;
	public static MessageWrapper CHAT_ENTERINTEGER;
	public static MessageWrapper CHAT_DELAYEDTELEPORT;
	public static MessageWrapper CHAT_DELAYEDTPMOVED;
	public static MessageWrapper CHAT_ERROROCCURED;
	public static MessageWrapper CHAT_UPDATE;
	public static MessageWrapper CHAT_MAXAMOUNT;

	public static MessageWrapper CHAT_BASIC_NEGATIVENUMBER;
	public static MessageWrapper CHAT_BASIC_ON;
	public static MessageWrapper CHAT_BASIC_OFF;
	public static MessageWrapper CHAT_BASIC_COORDS3D;

	public static MessageWrapper CHAT_CONFIRM_NULLHANDLER;
	public static MessageWrapper CHAT_CONFIRM_NEEDCONFIRM;
	public static MessageWrapper CHAT_CONFIRM_TIMEOUT;
	
	public static MessageWrapper CHAT_ADMIN_CONFIG_RELOADED;
	public static MessageWrapper CHAT_ADMIN_CONFIG_RESET;
	public static MessageWrapper CHAT_ADMIN_CONFIG_SAVED;
	public static MessageWrapper CHAT_ADMIN_CONFIG_SET;
	public static MessageWrapper CHAT_ADMIN_CONFIG_GET_SINGLE;
	public static MessageWrapper CHAT_ADMIN_CONFIG_GET_LIST_SECTION = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);
	public static MessageWrapper CHAT_ADMIN_CONFIG_GET_LIST_KEY = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);

	public static MessageWrapper CHAT_ADMIN_GUILD_TIMEREST_SET;
	public static MessageWrapper CHAT_ADMIN_GUILD_LIST_HEADER;
	public static MessageWrapper CHAT_ADMIN_GUILD_LIST_PAGE_HASNEXT;
	public static MessageWrapper CHAT_ADMIN_GUILD_LIST_PAGE_NONEXT;
	public static MessageWrapper CHAT_ADMIN_GUILD_LIST_ITEM;

	public static MessageWrapper CHAT_ADMIN_GUILD_INACTIVE_UPDATED;
	public static MessageWrapper CHAT_ADMIN_GUILD_INACTIVE_LIST_HEADER;
	public static MessageWrapper CHAT_ADMIN_GUILD_INACTIVE_LIST_ITEM;
	public static MessageWrapper CHAT_ADMIN_GUILD_INACTIVE_LIST_AGO;
	public static MessageWrapper CHAT_ADMIN_GUILD_INACTIVE_LIST_NOW;

	public static MessageWrapper CHAT_ADMIN_GUILD_BANK_PAID;
	public static MessageWrapper CHAT_ADMIN_GUILD_BANK_WITHDREW;

	public static MessageWrapper CHAT_ADMIN_GUILD_KICK_LEADER;
	public static MessageWrapper CHAT_ADMIN_GUILD_INVITED;
	public static MessageWrapper CHAT_ADMIN_GUILD_TELEPORTED_SELF;
	public static MessageWrapper CHAT_ADMIN_GUILD_TELEPORTED_OTHER;

	public static MessageWrapper CHAT_ADMIN_GUILD_SET_TAG;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_POINTS;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_LIVES;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_SLOTS_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_SLOTS_SMALLERTHANPLAYERS;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_NAME_ENTERNEWNAME;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_NAME_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_LEADER_NOTINGUILD;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_LEADER_ALREADYLEADER;
	public static MessageWrapper CHAT_ADMIN_GUILD_SET_LEADER_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_GUILD_RESET_POINTS_INVALIDCONDITIONTYPE;
	public static MessageWrapper CHAT_ADMIN_GUILD_RESET_POINTS_NOVALUE;
	public static MessageWrapper CHAT_ADMIN_GUILD_RESET_POINTS_SUCCESS;

	public static MessageWrapper CHAT_ADMIN_GUILD_RANK_NOTFOUND;
	public static MessageWrapper CHAT_ADMIN_GUILD_RANK_DELETE_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_GUILD_RANK_LIST_HEADER;
	public static MessageWrapper CHAT_ADMIN_GUILD_RANK_LIST_ITEM;

	public static MessageWrapper CHAT_ADMIN_PLAYER_SET_POINTS;

	public static MessageWrapper CHAT_ADMIN_REGION_BYPASS_TOGGLED_SELF;
	public static MessageWrapper CHAT_ADMIN_REGION_BYPASS_TOGGLED_OTHER;
	public static MessageWrapper CHAT_ADMIN_REGION_BYPASS_NOTIFYOTHER;

	public static MessageWrapper CHAT_ADMIN_REGION_SPECTATE_TOGGLED_SELF;
	public static MessageWrapper CHAT_ADMIN_REGION_SPECTATE_TOGGLED_OTHER;
	public static MessageWrapper CHAT_ADMIN_REGION_SPECTATE_NOTIFYOTHER;

	public static MessageWrapper CHAT_ADMIN_REGION_DELETE_SUCCESS;

	public static MessageWrapper CHAT_ADMIN_REGION_TELEPORT_SELF;
	public static MessageWrapper CHAT_ADMIN_REGION_TELEPORT_OTHER;
	public static MessageWrapper CHAT_ADMIN_REGION_TELEPORT_NOTIFYOTHER;

	public static MessageWrapper CHAT_ADMIN_SAVE_PLAYERS;
	public static MessageWrapper CHAT_ADMIN_SAVE_GUILDS;
	public static MessageWrapper CHAT_ADMIN_SAVE_REGIONS;
	public static MessageWrapper CHAT_ADMIN_SAVE_RANKS;
	public static MessageWrapper CHAT_ADMIN_SAVE_ALL;
	public static MessageWrapper CHAT_ADMIN_SAVE_AUTOSAVE;

	public static MessageWrapper CHAT_ADMIN_SPYMODE_NOTIFY;
	public static MessageWrapper CHAT_ADMIN_SPYMODE_SUCCESS_SELF;
	public static MessageWrapper CHAT_ADMIN_SPYMODE_SUCCESS_OTHER;

	public static MessageWrapper CHAT_ADMIN_MIGRATE_INVALIDTYPE;
	public static MessageWrapper CHAT_ADMIN_MIGRATE_SAMETYPE;
	public static MessageWrapper CHAT_ADMIN_MIGRATE_CONNECTIONFAIL;
	public static MessageWrapper CHAT_ADMIN_MIGRATE_SUCCESS;

	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_DISABLED;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_TELEPORT_SELF;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_TELEPORT_OTHER;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_NOTFOUND;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_ADD_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_DELETE_SUCCESS;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_LIST_HEADER;
	public static MessageWrapper CHAT_ADMIN_HOLOGRAM_LIST_ITEM;

	public static MessageWrapper CHAT_RELOAD_RELOADING;
	public static MessageWrapper CHAT_RELOAD_CONFIG;
	public static MessageWrapper CHAT_RELOAD_MESSAGES;
	public static MessageWrapper CHAT_RELOAD_MYSQL;
	public static MessageWrapper CHAT_RELOAD_PLAYERS;
	public static MessageWrapper CHAT_RELOAD_GUILDS;
	public static MessageWrapper CHAT_RELOAD_REGIONS;
	public static MessageWrapper CHAT_RELOAD_NEWMSGFILE;
	public static MessageWrapper CHAT_RELOAD_GROUPS;
	public static MessageWrapper CHAT_RELOAD_RANKS;
	public static MessageWrapper CHAT_RELOAD_RELOADED;

	public static MessageWrapper CHAT_PVP_TEAM;
	public static MessageWrapper CHAT_PVP_ALLY;

	public static MessageWrapper CHAT_RAID_RESTING;
	public static MessageWrapper CHAT_RAID_PROTECTION;

	public static MessageWrapper CHAT_GUILD_COMPASSTARGET_ON;
	public static MessageWrapper CHAT_GUILD_COMPASSTARGET_OFF;
	public static MessageWrapper CHAT_GUILD_NOGUILDS;
	public static MessageWrapper CHAT_GUILD_OPENINVITATION;
	public static MessageWrapper CHAT_GUILD_FPVPTOGGLED;
	public static MessageWrapper CHAT_GUILD_EFFECT_SUCCESS;

	public static MessageWrapper CHAT_GUILD_ALLY_WAR;
	public static MessageWrapper CHAT_GUILD_ALLY_SAMENAME;
	public static MessageWrapper CHAT_GUILD_ALLY_INVITED;
	public static MessageWrapper CHAT_GUILD_ALLY_ACCEPTED;
	public static MessageWrapper CHAT_GUILD_ALLY_NOTIFYGUILD;
	public static MessageWrapper CHAT_GUILD_ALLY_NOTIFYGUILDCANCELED;
	public static MessageWrapper CHAT_GUILD_ALLY_CANCELED;
	public static MessageWrapper CHAT_GUILD_ALLY_LIST_NOALLIES;
	public static MessageWrapper CHAT_GUILD_ALLY_LIST_HEADER_ALLIES;
	public static MessageWrapper CHAT_GUILD_ALLY_LIST_HEADER_INVITATIONS;
	public static MessageWrapper CHAT_GUILD_ALLY_LIST_ITEM;
	public static MessageWrapper CHAT_GUILD_ALLY_LIST_SEPARATOR;

	public static MessageWrapper CHAT_GUILD_COULDNOTFIND;
	public static MessageWrapper CHAT_GUILD_HASNOREGION;
	public static MessageWrapper CHAT_GUILD_ENTERNAME;
	public static MessageWrapper CHAT_GUILD_ENTERTAG;
	public static MessageWrapper CHAT_GUILD_NOTINGUILD;
	public static MessageWrapper CHAT_GUILD_ABANDONED;
	public static MessageWrapper CHAT_GUILD_NOTLEADER;
	public static MessageWrapper CHAT_GUILD_NOGUILDPERM;
	public static MessageWrapper CHAT_GUILD_SETHOME_SUCCESS;
	public static MessageWrapper CHAT_GUILD_SETHOME_OUTSIDEREGION;
	public static MessageWrapper CHAT_GUILD_SETHOME_OVERLAPS;
	public static MessageWrapper CHAT_GUILD_SET_NAME;
	public static MessageWrapper CHAT_GUILD_SET_TAG;
	public static MessageWrapper CHAT_GUILD_HOME;
	public static MessageWrapper CHAT_GUILD_JOINED;
	public static MessageWrapper CHAT_GUILD_EXPLOSIONATREGION;
	public static MessageWrapper CHAT_GUILD_KICKYOURSELF;
	public static MessageWrapper CHAT_GUILD_TOOCLOSE;
	public static MessageWrapper CHAT_GUILD_NAMENOTEXIST;
	public static MessageWrapper CHAT_GUILD_NOTENOUGHMONEY;
	public static MessageWrapper CHAT_GUILD_ISFULL;

	public static MessageWrapper CHAT_GUILD_LEADER_SUCCESS;
	public static MessageWrapper CHAT_GUILD_LEADER_SAMENICK;
	public static MessageWrapper CHAT_GUILD_LEADER_NOTSAMEGUILD;

	public static MessageWrapper CHAT_GUILD_LEAVE_ISLEADER;
	public static MessageWrapper CHAT_GUILD_LEAVE_LEFT;

	public static MessageWrapper CHAT_GUILD_BANK_ENTERAMOUNT;
	public static MessageWrapper CHAT_GUILD_BANK_PAY_PAID;
	public static MessageWrapper CHAT_GUILD_BANK_PAY_NOTENOUGH;
	public static MessageWrapper CHAT_GUILD_BANK_WITHDRAW_SUCCESS;
	public static MessageWrapper CHAT_GUILD_BANK_WITHDRAW_NOTENOUGH;

	public static MessageWrapper CHAT_GUILD_WAR_ALLY;
	public static MessageWrapper CHAT_GUILD_WAR_YOURGUILDWAR;
	public static MessageWrapper CHAT_GUILD_WAR_NOWAR_INVITE_SUCCESS;
	public static MessageWrapper CHAT_GUILD_WAR_NOWAR_INVITE_NOTIFY;
	public static MessageWrapper CHAT_GUILD_WAR_NOWAR_CANCEL_SUCCESS;
	public static MessageWrapper CHAT_GUILD_WAR_NOWAR_CANCEL_NOTIFY;
	public static MessageWrapper CHAT_GUILD_WAR_LIST_NOWARS;
	public static MessageWrapper CHAT_GUILD_WAR_LIST_WARSHEADER;
	public static MessageWrapper CHAT_GUILD_WAR_LIST_NOWARINVHEADER;
	public static MessageWrapper CHAT_GUILD_WAR_LIST_ITEM;
	public static MessageWrapper CHAT_GUILD_WAR_LIST_SEPARATOR;

	public static MessageWrapper CHAT_GUILD_BUY_SLOT_SUCCESS;
	public static MessageWrapper CHAT_GUILD_BUY_SLOT_MAXREACHED;
	public static MessageWrapper CHAT_GUILD_BUY_LIFE_SUCCESS;
	public static MessageWrapper CHAT_GUILD_BUY_LIFE_MAXREACHED;

	public static MessageWrapper CHAT_GUILD_VAULT_OUTSIDEREGION = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_PLACE_SUCCESS = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_DROP = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_PLACE_EXISTS = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_PLACE_DOUBLECHEST = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_BREAK_NOTEMPTY = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_BREAK_SUCCESS = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_DENYRELATIVE = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_GUILD_VAULT_RESTORE_HASALREADY;
	public static MessageWrapper CHAT_GUILD_VAULT_RESTORE_SUCCESS;

	public static MessageWrapper CHAT_GUILD_CHATMODE_SUCCESS;
	public static MessageWrapper CHAT_GUILD_CHATMODE_INVALID;
	public static MessageWrapper CHAT_GUILD_CHATMODE_NAMES_NORMAL;
	public static MessageWrapper CHAT_GUILD_CHATMODE_NAMES_GUILD;
	public static MessageWrapper CHAT_GUILD_CHATMODE_NAMES_ALLY;

	public static MessageWrapper CHAT_PLAYER_ENTERNAME;
	public static MessageWrapper CHAT_PLAYER_NOTEXISTS;
	public static MessageWrapper CHAT_PLAYER_NOTONLINE;
	public static MessageWrapper CHAT_PLAYER_HASGUILD;
	public static MessageWrapper CHAT_PLAYER_HASNOGUILD;
	public static MessageWrapper CHAT_PLAYER_ALREADYINVITED;
	public static MessageWrapper CHAT_PLAYER_NOTINYOURGUILD;
	public static MessageWrapper CHAT_PLAYER_PVPMONEY_KILL;
	public static MessageWrapper CHAT_PLAYER_PVPMONEY_REVENGE;
	public static MessageWrapper CHAT_PLAYER_INVITE_INVITED;
	public static MessageWrapper CHAT_PLAYER_INVITE_LIST_HEADER;
	public static MessageWrapper CHAT_PLAYER_INVITE_LIST_ITEM;
	public static MessageWrapper CHAT_PLAYER_INVITE_LIST_SEPARATOR;
	public static MessageWrapper CHAT_PLAYER_INVITE_LIST_NOTHING;
	public static MessageWrapper CHAT_PLAYER_INVITE_NOTINVITED;
	public static MessageWrapper CHAT_PLAYER_INVITE_NOTIFY;
	public static MessageWrapper CHAT_PLAYER_INVITE_CANCEL_SUCCESS;
	public static MessageWrapper CHAT_PLAYER_INVITE_CANCEL_NOTIFY;
	public static MessageWrapper CHAT_PLAYER_INFO_HEADER;
	public static MessageWrapper CHAT_PLAYER_INFO_ITEMS;
	public static MessageWrapper CHAT_PLAYER_INFO_GUILDROW;

	public static MessageWrapper CHAT_REGION_RESIZE_START;
	public static MessageWrapper CHAT_REGION_RESIZE_TOGGLE;
	public static MessageWrapper CHAT_REGION_RESIZE_SUCCESS;
	public static MessageWrapper CHAT_REGION_DENY_INTERACT;
	public static MessageWrapper CHAT_REGION_DENY_RIDEMOB;
	public static MessageWrapper CHAT_REGION_DENY_ATTACKMOB;
	public static MessageWrapper CHAT_REGION_DENY_UNLEASH;
	public static MessageWrapper CHAT_REGION_DENY_LEASH;
	public static MessageWrapper CHAT_REGION_NOREGIONHERE;
	public static MessageWrapper CHAT_REGION_REGIONHERE;
	public static MessageWrapper CHAT_REGION_VALIDATION_TOOSMALL;
	public static MessageWrapper CHAT_REGION_VALIDATION_TOOBIG;
	public static MessageWrapper CHAT_REGION_VALIDATION_TOOCLOSE;
	public static MessageWrapper CHAT_REGION_VALIDATION_OVERLAPS;
	public static MessageWrapper CHAT_REGION_VALIDATION_WORLDGUARD;
	public static MessageWrapper CHAT_REGION_VALIDATION_NOTSELECTED;
	public static MessageWrapper CHAT_REGION_VALIDATION_NOTVALID;
	public static MessageWrapper CHAT_REGION_VALIDATION_VALID;
	public static MessageWrapper CHAT_REGION_SIZE;
	public static MessageWrapper CHAT_REGION_PRICE;
	public static MessageWrapper CHAT_REGION_CNOTAFFORD;
	public static MessageWrapper CHAT_REGION_MAXAMOUNT;
	public static MessageWrapper CHAT_REGION_CREATED;
	public static MessageWrapper CHAT_REGION_MUSTVEGUILD;
	public static MessageWrapper CHAT_REGION_ENTERED = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_REGION_EXITED = new MessageWrapperImpl(MessageWrapper.Flag.TITLE);
	public static MessageWrapper CHAT_REGION_NOTFOUND;
	public static MessageWrapper CHAT_REGION_NOTIFYGUILD_ENTERED;
	public static MessageWrapper CHAT_REGION_BELONGSTO;
	public static MessageWrapper CHAT_REGION_LIST_HEADER;
	public static MessageWrapper CHAT_REGION_LIST_ITEM;
	public static MessageWrapper CHAT_REGION_TOOL_TOGGLEDMODE;
	public static MessageWrapper CHAT_REGION_TOOL_MODES_CHECK;
	public static MessageWrapper CHAT_REGION_TOOL_MODES_SELECT;
	public static MessageWrapper CHAT_REGION_BLOCKEDCMD;
	public static MessageWrapper CHAT_REGION_DELETED;

	public static MessageWrapper CHAT_USAGE_NGA_ACCESS;
	public static MessageWrapper CHAT_USAGE_NGA_RELOAD;
	public static MessageWrapper CHAT_USAGE_NGA_SAVE;
	public static MessageWrapper CHAT_USAGE_NGA_CHATSPY;
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_ACCESS = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_GET;
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_RELOAD;
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_RESET;
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_SAVE;
	public static MessageWrapper CHAT_USAGE_NGA_CONFIG_SET;

	public static MessageWrapper CHAT_USAGE_NGA_PLAYER_ACCESS;
	public static MessageWrapper CHAT_USAGE_NGA_PLAYER_SET_POINTS;

	public static MessageWrapper CHAT_USAGE_NGA_GUILD_ACCESS;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_POINTS;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_LIVES;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_TIMEREST;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_NAME;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_TAG;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_SLOTS;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_LEADER;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_SET_LIVEREGENERATIONTIME;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_ABANDON;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_BANK_PAY;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_BANK_WITHDRAW;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_INACTIVE_LIST;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_INVITE;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_KICK;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_LIST;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_PURGE;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_TP;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_RESET_POINTS;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_RANK_ACCESS;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_RANK_DELETE;
	public static MessageWrapper CHAT_USAGE_NGA_GUILD_RANK_LIST;

	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_ACCESS;
	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_LIST;
	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_TELEPORT_SELF;
	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_TELEPORT_HERE;
	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_DELETE;
	public static MessageWrapper CHAT_USAGE_NGA_HOLOGRAM_ADDTOP;
	public static MessageWrapper CHAT_USAGE_GUILD_ACCESS;

	public static MessageWrapper CHAT_USAGE_NGA_REGION_ACCESS = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);
	public static MessageWrapper CHAT_USAGE_NGA_REGION_BYPASS;
	public static MessageWrapper CHAT_USAGE_NGA_REGION_DELETE;
	public static MessageWrapper CHAT_USAGE_NGA_REGION_LIST;
	public static MessageWrapper CHAT_USAGE_NGA_REGION_SPECTATE;
	public static MessageWrapper CHAT_USAGE_NGA_REGION_TELEPORT;
	public static MessageWrapper CHAT_USAGE_NGA_REGION_BUY;

	public static MessageWrapper CHAT_USAGE_NGA_MIGRATE;

	public static MessageWrapper CHAT_USAGE_GUILD_ABANDON;
	public static MessageWrapper CHAT_USAGE_GUILD_ALLY;
	public static MessageWrapper CHAT_USAGE_GUILD_BANK_PAY;
	public static MessageWrapper CHAT_USAGE_GUILD_BANK_WITHDRAW;
	public static MessageWrapper CHAT_USAGE_GUILD_BUY_SLOT;
	public static MessageWrapper CHAT_USAGE_GUILD_BUY_LIFE;
	public static MessageWrapper CHAT_USAGE_GUILD_COMPASS;
	public static MessageWrapper CHAT_USAGE_GUILD_CHATMODE;
	public static MessageWrapper CHAT_USAGE_GUILD_CREATE;
	public static MessageWrapper CHAT_USAGE_GUILD_EFFECT;
	public static MessageWrapper CHAT_USAGE_GUILD_HOME_SET;
	public static MessageWrapper CHAT_USAGE_GUILD_HOME_TELEPORT;
	public static MessageWrapper CHAT_USAGE_GUILD_INFO;
	public static MessageWrapper CHAT_USAGE_GUILD_INVITE;
	public static MessageWrapper CHAT_USAGE_GUILD_JOIN;
	public static MessageWrapper CHAT_USAGE_GUILD_KICK;
	public static MessageWrapper CHAT_USAGE_GUILD_LEADER;
	public static MessageWrapper CHAT_USAGE_GUILD_LEAVE;
	public static MessageWrapper CHAT_USAGE_GUILD_MENU;
	public static MessageWrapper CHAT_USAGE_GUILD_OPENINVITATION;
	public static MessageWrapper CHAT_USAGE_GUILD_PVPTOGGLE;
	public static MessageWrapper CHAT_USAGE_GUILD_REQUIREDITEMS;
	public static MessageWrapper CHAT_USAGE_GUILD_SET_NAME;
	public static MessageWrapper CHAT_USAGE_GUILD_SET_TAG;
	public static MessageWrapper CHAT_USAGE_GUILD_RANK_ACCESS;
	public static MessageWrapper CHAT_USAGE_GUILD_RANK_DELETE;
	public static MessageWrapper CHAT_USAGE_GUILD_RANK_LIST;
	public static MessageWrapper CHAT_USAGE_GUILD_TOP;
	public static MessageWrapper CHAT_USAGE_GUILD_VAULT_RESTORE;
	public static MessageWrapper CHAT_USAGE_GUILD_WAR;

	public static MessageWrapper CHAT_USAGE_REGION_ACCESS;
	public static MessageWrapper CHAT_USAGE_REGION_BUY;
	public static MessageWrapper CHAT_USAGE_REGION_DELETE;
	public static MessageWrapper CHAT_USAGE_REGION_LIST;

	public static MessageWrapper CHAT_USAGE_TOOL;
	public static MessageWrapper CHAT_USAGE_PLAYER_INFO;
	public static MessageWrapper CHAT_USAGE_CONFIRM;

	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_MAIN;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_CONFIG;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_REGION;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_GUILD_MAIN;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_GUILD_RANK;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_PLAYER;
	public static MessageWrapper CHAT_COMMANDS_HEADER_ADMIN_HOLOGRAM;
	public static MessageWrapper CHAT_COMMANDS_HEADER_GUILD_RANK;
	public static MessageWrapper CHAT_COMMANDS_HEADER_REGION;
	public static MessageWrapper CHAT_COMMANDS_GUILD_HASGUILD = new MessageWrapperImpl(MessageWrapper.Flag.LIST);
	public static MessageWrapper CHAT_COMMANDS_GUILD_NOGUILD = new MessageWrapperImpl(MessageWrapper.Flag.LIST);

	public static MessageWrapper CHAT_CREATEGUILD_NOTENOUGHMONEY;
	public static MessageWrapper CHAT_CREATEGUILD_ITEMLIST;
	public static MessageWrapper CHAT_CREATEGUILD_ITEMLISTSEP;
	public static MessageWrapper CHAT_CREATEGUILD_NOITEMS;
	public static MessageWrapper CHAT_CREATEGUILD_TAGEXISTS;
	public static MessageWrapper CHAT_CREATEGUILD_NAMEEXISTS;
	public static MessageWrapper CHAT_CREATEGUILD_HASGUILD;
	public static MessageWrapper CHAT_CREATEGUILD_SUCCESS;
	public static MessageWrapper CHAT_CREATEGUILD_REGIONHERE;
	public static MessageWrapper CHAT_CREATEGUILD_TOOCLOSESPAWN;
	public static MessageWrapper CHAT_CREATEGUILD_NOTALLOWEDSTRING;
	public static MessageWrapper CHAT_CREATEGUILD_TAG_TOOSHORT;
	public static MessageWrapper CHAT_CREATEGUILD_TAG_TOOLONG;
	public static MessageWrapper CHAT_CREATEGUILD_NAME_TOOSHORT;
	public static MessageWrapper CHAT_CREATEGUILD_NAME_TOOLONG;
	public static MessageWrapper CHAT_CREATEGUILD_DISABLEDWORLD;

	public static MessageWrapper CHAT_GUILDINFO_PLAYERSEPARATOR;
	public static MessageWrapper CHAT_GUILDINFO_LEADERPREFIX;
	public static MessageWrapper CHAT_GUILDINFO_PLAYERCOLOR_ONLINE;
	public static MessageWrapper CHAT_GUILDINFO_PLAYERCOLOR_OFFLINE;
	public static MessageWrapper CHAT_GUILDINFO_ROW_ALLY;
	public static MessageWrapper CHAT_GUILDINFO_ROW_PLAYER = new MessageWrapperImpl(MessageWrapper.Flag.NOAFTERVARCOLOR);
	public static MessageWrapper CHAT_GUILDINFO_ROW_WAR;
	public static MessageWrapper CHAT_GUILDINFO_FULLINFO;
	public static MessageWrapper CHAT_GUILDINFO_INFO;

	public static MessageWrapper BROADCAST_ADMIN_GUILD_ABANDON;
	public static MessageWrapper BROADCAST_ADMIN_GUILD_CLEANUP;

	public static MessageWrapper BROADCAST_GUILD_WAR;
	public static MessageWrapper BROADCAST_GUILD_NOWAR;
	public static MessageWrapper BROADCAST_GUILD_JOINED;
	public static MessageWrapper BROADCAST_GUILD_LEFT;
	public static MessageWrapper BROADCAST_GUILD_CREATED;
	public static MessageWrapper BROADCAST_GUILD_ABANDONED;
	public static MessageWrapper BROADCAST_GUILD_SETLEADER;
	public static MessageWrapper BROADCAST_GUILD_ALLIED;
	public static MessageWrapper BROADCAST_GUILD_ENDALLY;
	public static MessageWrapper BROADCAST_GUILD_KICKED;
	public static MessageWrapper BROADCAST_GUILD_NEWLEADER;
	public static MessageWrapper BROADCAST_GUILD_DESTROYED;

	public static MessageWrapper BROADCAST_GUILD_RAID_FINISHED_ATTACKERWON;
	public static MessageWrapper BROADCAST_GUILD_RAID_FINISHED_DEFENDERWON;

	public static MessageWrapper BROADCAST_PVP_KILLED = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);

	public static MessageWrapper HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER;
	public static MessageWrapper HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW = new MessageWrapperImpl(MessageWrapper.Flag.NOPREFIX);

	public static MessageWrapper BARAPI_WARPROGRESS;

	public static MessageWrapper MISC_POINTSBELOWNAME;

	public static MessageWrapper INVENTORY_REQUIREDITEMS_NAME;
	public static MessageWrapper INVENTORY_REQUIREDITEMS_LORE = new MessageWrapperImpl(MessageWrapper.Flag.LIST);
	public static MessageWrapper INVENTORY_GGUI_NAME;

	public static MessageWrapper INVENTORY_GUI_GUILDTOP;
	public static MessageWrapper INVENTORY_GUI_HOMETP;
	public static MessageWrapper INVENTORY_GUI_BACK;
	public static MessageWrapper INVENTORY_GUI_JOIN_TITLE;
	public static MessageWrapper INVENTORY_GUI_JOIN_ICONITEM;
	public static MessageWrapper INVENTORY_GUI_JOIN_ROWITEM;
	public static MessageWrapper INVENTORY_GUI_PLAYERSLIST_ICONITEM;
	public static MessageWrapper INVENTORY_GUI_PLAYERSLIST_ROWITEM;
	public static MessageWrapper INVENTORY_GUI_PLAYERSLIST_TITLE;
	public static MessageWrapper INVENTORY_GUI_PLAYERSETTINGS_TITLE;
	public static MessageWrapper INVENTORY_GUI_PLAYERSETTINGS_ITEM_RANK;
	public static MessageWrapper INVENTORY_GUI_PLAYERSETTINGS_ITEM_KICK;
	public static MessageWrapper INVENTORY_GUI_RANKS_TITLE;
	public static MessageWrapper INVENTORY_GUI_RANKS_ICONITEM;
	public static MessageWrapper INVENTORY_GUI_RANKS_ROWITEM;
	public static MessageWrapper INVENTORY_GUI_RANKS_ADDITEM;
	public static MessageWrapper INVENTORY_GUI_RANKS_DEFAULTNAME;
	public static MessageWrapper INVENTORY_GUI_RANKS_LEADERNAME;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_TITLE;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_CLONEPREFIX;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_EDITPERMISSIONS;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_SETDEFAULT;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_CLONE;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_RENAME;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_DELETE;
	public static MessageWrapper INVENTORY_GUI_RANK_SETTINGS_ITEM_MEMBERLIST;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_TITLE;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_ICON;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_SET_NAME;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_SET_TAG;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_SET_HOME;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_ON;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_OFF;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_OPENINVITATION;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_BUYLIFE;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_BUYSLOT;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_ITEM_INVITE;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_INVITE_TITLE;
	public static MessageWrapper INVENTORY_GUI_SETTINGS_INVITE_ITEM;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_TITLE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_ITEM_ENABLED;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_ITEM_DISABLED;

	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BLOCK_BREAK;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BLOCK_PLACE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_MOB_ATTACK;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_MOB_RIDE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_MOB_LEASH;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_INTERACT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_ABANDON;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_KICK;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_EFFECT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_INVITE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BANK_PAY;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BANK_WITHDRAW;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_PVPTOGGLE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_ALLY_CANCEL;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_ALLY_INVITE_SEND;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_ALLY_INVITE_CANCEL;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_ALLY_ACCEPT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BUYLIFE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_BUYSLOT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_HOME_SET;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_HOME_TELEPORT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_OPENINVITATION;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_SET_NAME;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_SET_TAG;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_WAR_INVITE_SEND;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_WAR_INVITE_CANCEL;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_WAR_INVITE_ACCEPT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_WAR_START;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_VAULT_ACCESS;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_VAULT_PUT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_VAULT_TAKE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_VAULT_PLACE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_VAULT_BREAK;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_REGION_CREATE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_REGION_REMOVE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_REGION_RESIZE;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_RANK_LIST;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_RANK_SET;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_RANK_EDIT;
	public static MessageWrapper INVENTORY_GUI_PERMISSIONS_NAMES_RANK_DELETE;

	public static MessageWrapper SIGNGUI_GUILD_SETTINGS_SET_NAME;
	public static MessageWrapper SIGNGUI_GUILD_SETTINGS_SET_TAG;
	public static MessageWrapper SIGNGUI_GUILD_RANKS_SET_NAME;

	public static MessageWrapper DYNMAP_REGION = new MessageWrapperImpl(MessageWrapper.Flag.NOAFTERVARCOLOR);
	public static MessageWrapper DYNMAP_GUILDHOME = new MessageWrapperImpl(MessageWrapper.Flag.NOAFTERVARCOLOR);

	public static MessageWrapper TIMEUNIT_SECOND_SINGULAR;
	public static MessageWrapper TIMEUNIT_SECOND_PLURAL;
	public static MessageWrapper TIMEUNIT_MINUTE_SINGULAR;
	public static MessageWrapper TIMEUNIT_MINUTE_PLURAL;
	public static MessageWrapper TIMEUNIT_HOUR_SINGULAR;
	public static MessageWrapper TIMEUNIT_HOUR_PLURAL;
	public static MessageWrapper TIMEUNIT_DAY_SINGULAR;
	public static MessageWrapper TIMEUNIT_DAY_PLURAL;
	public static MessageWrapper TIMEUNIT_WEEK_SINGULAR;
	public static MessageWrapper TIMEUNIT_WEEK_PLURAL;
	public static MessageWrapper TIMEUNIT_MONTH_SINGULAR;
	public static MessageWrapper TIMEUNIT_MONTH_PLURAL;
	public static MessageWrapper TIMEUNIT_YEAR_SINGULAR;
	public static MessageWrapper TIMEUNIT_YEAR_PLURAL;

	private static final Map<String, MessageWrapper> wrapperMap = new HashMap<>();

	static {
		for(FieldAccessor<MessageWrapper> fieldAccessor : Reflections.getFields(Message.class, MessageWrapper.class)) {
			MessageWrapper wrapper = fieldAccessor.get();
			String path = StringUtils.replace(fieldAccessor.getName(), "_", ".").toLowerCase();

			if(wrapper == null) {
				wrapper = new MessageWrapperImpl(path);
				fieldAccessor.set(wrapper);
			}
			else {
				wrapper.setPath(path);
			}

			if(wrapper.getName().startsWith("CHAT_USAGE_")) {
				wrapper.getFlags().add(MessageWrapper.Flag.NOPREFIX);
			}

			wrapperMap.put(fieldAccessor.getName(), wrapper);
		}
	}

	/**
	 * Gets a message depending on the boolean, it can be either ON or OFF message
	 *
	 * @param b boolean
	 * @return string of ON or OFF message
	 */
	public static String getOnOff(boolean b) {
		return b ? Message.CHAT_BASIC_ON.get() : Message.CHAT_BASIC_OFF.get();
	}

	/**
	 * Sends a list of messages
	 *
	 * @param list   the list
	 * @param sender the receiver
	 */
	public static void send(List<MessageWrapper> list, CommandSender sender) {
		for(MessageWrapper message : list) {
			message.send(sender);
		}
	}

	/**
	 * Gets a message from path
	 *
	 * @param path path string
	 * @return message wrapper
	 */
	public static MessageWrapper fromPath(String path) {
		return wrapperMap.get(StringUtils.replace(path, ".", "_").toUpperCase());
	}

	/**
	 * Gets a message with filled coordinated
	 *
	 * @param location location instance
	 * @return the message
	 */
	public static MessageWrapper getCoords3D(Location location) {
		MessageWrapper message = Message.CHAT_BASIC_COORDS3D.clone();

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.X, String.valueOf(location.getBlockX()));
		vars.put(VarKey.Y, String.valueOf(location.getBlockY()));
		vars.put(VarKey.Z, String.valueOf(location.getBlockZ()));
		message.vars(vars);

		return message;
	}

	/**
	 * Gets a wrapper from an enum like name
	 *
	 * @param name field name
	 * @return wrapper
	 */
	public static MessageWrapper valueOf(String name) {
		MessageWrapper wrapper = wrapperMap.get(name);

		if(wrapper == null) {
			throw new IllegalArgumentException("Missing message wrapper: " + name);
		}

		return wrapper;
	}

	/**
	 * Gets all wrappers
	 *
	 * @return array of wrappers
	 */
	public static MessageWrapper[] values() {
		return wrapperMap.values().toArray(new MessageWrapper[wrapperMap.size()]);
	}
}
