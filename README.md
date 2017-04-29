<a href="http://novaguilds.pl/"><img src="http://novaguilds.pl/img/newlogo.png" /></a><br/><br/>

NovaGuilds is my own guilds plugin, still in development, but I want to hear your opinions and ideas.<br/>
Check the wiki on github for details.<br/>
Please leave feedback!<br/><br/>

<span style="font-size: 20px;color:red">
    I do not allow anybody to publish compiled source (.jar) on their websites.<br/>
    Please download NovaGuilds' binary files only from bukkit.org, github and novaguilds.pl<br/>
    The safest way is to compile it yourself!<br/>
    You are free to redistribute modified versions of source code to others, but you must not distribute compiled versions of the plugin using the name NovaGuilds.
</span>

<br/><br/>

<b>Vault</b> is required!<br/>
<b>Essentials</b> is optional but <b>highly</b> recommended!<br/>
<b>BarAPI/BossBarAPI</b> is optional (Not anymore since 1.9, it's not needed)<br/>
<b>HolographicDisplays</b> is optional<br/>
<b>VanishNoPacket</b> is supported by the plugin<br/>
<b>ScoreBoardStats</b> is optional<br/>
<br/><br/>
[![Join the chat at https://gitter.im/MarcinWieczorek/NovaGuilds](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/MarcinWieczorek/NovaGuilds?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/MarcinWieczorek/NovaGuilds.svg?branch=master)](https://travis-ci.org/MarcinWieczorek/NovaGuilds)
[![ghit.me](https://ghit.me/badge.svg?repo=MarcinWieczorek/NovaGuilds)](https://ghit.me/repo/MarcinWieczorek/NovaGuilds)
<br/>

<h2><b>Downloads</b></h2>
The plugin works on all versions including and above <b>1.7.5</b>
<a href="http://novaguilds.pl">http://novaguilds.pl/</a><br/>
<b>SNAPSHOTS: </b> <a href="http://repo.novaguilds.pl/co/marcin/novaguilds/">http://repo.novaguilds.pl/co/marcin/novaguilds/</a>
<br/><br/><br/>

<h2><b>Support</b></h2>
If you need support:<br/>
<a href="https://github.com/MarcinWieczorek/NovaGuilds/issues">Visit issues page</a><br/>
<a href="mailto:marcin@marcin.co">Send me an email</a><br/>
Send me a message using XMPP/Jabber: marcin@marcin.co
<br/><br/><br/>

<h2><b>How to compile NovaGuilds on your own?</b></h2>
First of all, you'll need to download the source. Download the zip from github and unpack, or (and this is a better idea)
Then you need Maven to build the project. Download it from public repositories (your machine probably runs Linux).
'cd' into plugin's directory and build it.
My project uses CraftBukkit <i>1.7.10-R0.1</i> Please build it on your own, because redistributing is against its license. 
```bash
git clone https://github.com/MarcinWieczorek/NovaGuilds.git
cd NovaGuilds/
mvn clean install
```
<br/><br/>

<h2>Special thanks:</h2>
<a href="https://www.spigotmc.org/members/x_2088.65434/">@x_2088</a> for Chinese translation<br/>
<a href="https://github.com/Mondanzo">@Mondanzo</a> for German translation<br/>
<a href="https://github.com/SgtLegoTown">@SgtLegoTown</a> for Dutch translation<br/>
<a href="https://github.com/Noiknez">@Noiknez</a> for French translation

<h2>Screenshots</h2>
<img src="http://novaguilds.pl/img/ss/ngss1.jpg" alt="ss" />
<br/><br/><br/>

<h2><b>Setup</b></h2>
<ul>
    <li>Download latest version</li>
    <li>Put it in your plugins/ directory</li>
    <li>Also put there Vault plugin</li>
    <li>Add BarAPI/BossBarAPI and HolographicDisplays plugins if you need.</li>
    <li>Restart the server</li>
    <li>Edit <b>config.yml</b> to setup your database</li>
    <li>Add <b>{TAG}</b> to players chat message/prefix (I recommend Essentials config)</li>
    <li>Restart the server</li>
    <li>Enjoy and leave feedback!</li>
</ul>

<br/>
<h2><b>Features</b></h2>
<ul>
    <li>MySQL, SQLite and Flat support</li>
    <li>Configurable money required to create a guild</li>
    <li>Configurable items required to create a guild</li>
    <li>Configurable region interaction</li>
    <li>Configurable messages</li>
    <li>Tags in chat, above player and in the tablist</li>
    <li>Automatic MySQL tables configuration</li>
    <li>Broadcast messages</li>
    <li>Pay/withdraw money to/from guild's bank</li>
    <li>Allies, wars between guilds</li>
    <li>Multi-language support</li>
    <li>Guild/Ally chat</li>
    <li>Configurable command aliases</li>
    <li>Advanced region selection and resizing</li>
    <li>Automatic regions</li>
    <li>Guild vaults</li>
    <li>VanishNoPacket support</li>
    <li>Auto update MySQL tables</li>
    <li>Advanced horse protection</li>
</ul>

<br/>
<h2><b>Planned features</b></h2>
<ul>
    <li>Auto update to latest build (?)</li>
    <li>Auto update config (?)</li>
    <li>You tell me!</li>
</ul>

<br/>
<h2><b>Undocumented features (bugs)</b></h2>
<ul>
    <li>Sometimes breaks chat plugin</li>
    <li>Found any? Github -> issues</li>
</ul>

<br/>
<h2><b>Commands</b></h2>
<table>
    <tr>
        <td>Command</td>
        <td>Description</td>
        <td>Usage</td>
    </tr>
    <tr>
        <td>/novaguilds, /ng</td>
        <td>Main cmd and plugin info</td>
        <td>/novaguilds [cmd] [params]</td>
    </tr>
    <tr>
        <td>/guild, /g</td>
        <td>Main guild command</td>
        <td>/g to list commands</td>
    </tr>
    <tr>
        <td>/ng tool</td>
        <td>Get NovaGuilds tool!</td>
        <td>Read its lore.</td>
    </tr>
    <tr>
        <td>/nga</td>
        <td>Admin commands</td>
        <td>Alias: /ng admin</td>
    </tr>
    <tr>
        <td>/nga reload</td>
        <td>Reload the plugin</td>
        <td>/nga reload</td>
    </tr>
    <tr>
        <td>/nga rg bypass</td>
        <td>Toggle region bypass
        <td>/nga rg bypass [player]</td>
    </tr>
    <tr>
        <td>/create</td>
        <td>Create a guild</td>
        <td>/create <tag> <name></td>
    </tr>
    <tr>
        <td>/abandon</td>
        <td>Abandon your guild</td>
        <td>/abandon</td>
    </tr>
    <tr>
        <td>/guildinfo, /gi</td>
        <td>Guild's information</td>
        <td>/gi <name></td>
    </tr>
    <tr>
        <td>/join</td>
        <td>Join a guild</td>
        <td>/join [name]</td>
    </tr>
    <tr>
        <td>/leave</td>
        <td>leave the guild</td>
        <td>/leave</td>
    </tr>
</table>

<br/>
<h2><b>Permissions</b></h2>
<table id="permissions-table">
    <thead>
        <tr>
            <td>Permission</td>
            <td>Description</td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>novaguilds.admin.access</td>
            <td>Access to /nga</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.access</td>
            <td>Access /nga config</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.get</td>
            <td>/nga config get</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.reload</td>
            <td>/nga config reload</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.reset</td>
            <td>/nga config reset</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.save</td>
            <td>/nga config save</td>
        </tr>
        <tr>
            <td>novaguilds.admin.config.set</td>
            <td>/nga config set</td>
        </tr>
        <tr>
            <td>novaguilds.admin.error.access</td>
            <td>/nga error</td>
        </tr>
        <tr>
            <td>novaguilds.admin.error.list</td>
            <td>/nga error list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.player.access</td>
            <td>/nga error</td>
        </tr>
        <tr>
            <td>novaguilds.admin.player.set.points</td>
            <td>/nga error list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.access</td>
            <td>Access to /nga g</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.abandon</td>
            <td>/nga g <guild> abandon</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.bank.pay</td>
            <td>/nga g <guild> pay <amount></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.bank.withdraw</td>
            <td>/nga g <guild> withdraw <amount></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.update</td>
            <td>/nga g inactive update</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.clean</td>
            <td>/nga g inactive clean</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.inactive.list</td>
            <td>/nga g inactive list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.invite</td>
            <td>/nga g <guild> invite <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.kick</td>
            <td>/nga g <guild> kick <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.list</td>
            <td>/nga g list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.leader</td>
            <td>/nga g <guild> leader <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.liveregenerationtime</td>
            <td>/nga g <guild> liveregentime <timestring></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.lives</td>
            <td>/nga g <guild> lives <lives></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.name</td>
            <td>/nga g <guild> setname <newname></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.points</td>
            <td>/nga g <guild> setpoints <points></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.tag</td>
            <td>/nga g <guild> setpoints <points></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.timerest</td>
            <td>/nga g <guild> timerest <timestring></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.set.slots</td>
            <td>/nga g <guild> slots <amount></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.purge</td>
            <td>/nga g purge/td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.teleport.self</td>
            <td>/nga g <guild> tp</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.teleport.other</td>
            <td>/nga g <guild> tp <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.fullinfo</td>
            <td>Display full guild's info</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.reset.points</td>
            <td>Reset guild's points</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.rank.access</td>
            <td>Accessing admin rank commands</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.rank.list</td>
            <td>Listing ranks</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.rank.edit</td>
            <td>Editing ranks</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.rank.delete</td>
            <td>Deleting ranks</td>
        </tr>
        <tr>
            <td>novaguilds.admin.guild.rank.set</td>
            <td>Setting ranks</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.access</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.list</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.teleport</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.teleport.here</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.delete</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.add</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.hologram.addtop</td>
            <td>/nga h</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.access</td>
            <td>/nga rg bypass</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.bypass.self</td>
            <td>/nga rg bypass</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.bypass.other</td>
            <td>/nga rg bypass <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.change.spectate.self</td>
            <td>/nga rg spectate</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.change.spectate.other</td>
            <td>/nga rg spectate <player></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.delete</td>
            <td>/nga rg delete <guild></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.list</td>
            <td>/nga rg list</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.teleport.self</td>
            <td>/nga rg tp <guild></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.buy</td>
            <td>/nga rg <guild> buy</td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.teleport.other</td>
            <td>/nga rg tp <guild></td>
        </tr>
        <tr>
            <td>novaguilds.admin.region.spectate</td>
            <td>Spectate others' area selection</td>
        </tr>
        <tr>
            <td>novaguilds.admin.reload</td>
            <td>/nga reload</td>
        </tr>
        <tr>
            <td>novaguilds.admin.save</td>
            <td>/nga save [guilds/players/regions]</td>
        </tr>
        <tr>
            <td>novaguilds.admin.save.notify</td>
            <td>Autosave notify message</td>
        </tr>
        <tr>
            <td>novaguilds.admin.updateavailable</td>
            <td>Update notify message</td>
        </tr>
        <tr>
            <td>novaguilds.admin.chatspy.self</td>
            <td>Toggle your chat spy mode</td>
        </tr>
        <tr>
            <td>novaguilds.admin.chatspy.other</td>
            <td>Toggle somebodies chat spy mode</td>
        </tr>
        <tr>
            <td>novaguilds.admin.migrate</td>
            <td>Migrate data to other storage</td>
        </tr>
        <tr>
            <td>novaguilds.admin.noconfirm</td>
            <td>Execute commands without /confirm</td>
        </tr>
        <tr>
            <td>novaguilds.guild.access</td>
            <td>/g</td>
        </tr>
        <tr>
            <td>novaguilds.guild.abandon</td>
            <td>/abandon</td>
        </tr>
        <tr>
            <td>novaguilds.guild.leave</td>
            <td>/leave</td>
        </tr>
        <tr>
            <td>novaguilds.guild.ally</td>
            <td>/g ally [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.bank.pay</td>
            <td>/g pay <amount></td>
        </tr>
        <tr>
            <td>novaguilds.guild.bank.withdraw</td>
            <td>/g withdraw <amount></td>
        </tr>
        <tr>
            <td>novaguilds.guild.compass</td>
            <td>/g compass</td>
        </tr>
        <tr>
            <td>novaguilds.guild.create</td>
            <td>/create <tag> <guildname></td>
        </tr>
        <tr>
            <td>novaguilds.guild.effect</td>
            <td>/g effect</td>
        </tr>
        <tr>
            <td>novaguilds.guild.home</td>
            <td>/g home</td>
        </tr>
        <tr>
            <td>novaguilds.guild.home.set</td>
            <td>/g home set</td>
        </tr>
        <tr>
            <td>novaguilds.guild.invite</td>
            <td>/invite <player></td>
        </tr>
        <tr>
            <td>novaguilds.guild.join</td>
            <td>/join [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.kick</td>
            <td>/g kick <player></td>
        </tr>
        <tr>
            <td>novaguilds.guild.pvptoggle</td>
            <td>/g pvp</td>
        </tr>
        <tr>
            <td>novaguilds.guild.requireditems</td>
            <td>/g items</td>
        </tr>
        <tr>
            <td>novaguilds.guild.top</td>
            <td>/g top</td>
        </tr>
        <tr>
            <td>novaguilds.guild.vault.restore</td>
            <td>/g vault</td>
        </tr>
        <tr>
            <td>novaguilds.guild.war</td>
            <td>/g war [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.buylife</td>
            <td>/g buylife</td>
        </tr>
        <tr>
            <td>novaguilds.guild.buyslot</td>
            <td>/g buyslot</td>
        </tr>
        <tr>
            <td>novaguilds.guild.chatmode</td>
            <td>Change chat mode</td>
        </tr>
        <tr>
            <td>novaguilds.guild.info</td>
            <td>/gi [guild]</td>
        </tr>
        <tr>
            <td>novaguilds.guild.leader</td>
            <td>/g leader <player></td>
        </tr>
        <tr>
            <td>novaguilds.guild.menu</td>
            <td>Guild's menu</td>
        </tr>
        <tr>
            <td>novaguilds.guild.boss</td>
            <td>Soon.</td>
        </tr>
        <tr>
            <td>novaguilds.guild.openinvitation</td>
            <td>/g </td>
        </tr>
        <tr>
            <td>novaguilds.guild.set.name</td>
            <td>Set guild's name</td>
        </tr>
        <tr>
            <td>novaguilds.guild.set.tag</td>
            <td>Set guild's tag</td>
        </tr>
        <tr>
            <td>novaguilds.guild.rank.access</td>
            <td>Accessing rank commands</td>
        </tr>
        <tr>
            <td>novaguilds.guild.rank.list</td>
            <td>Listing ranks</td>
        </tr>
        <tr>
            <td>novaguilds.guild.rank.edit</td>
            <td>Editing ranks</td>
        </tr>
        <tr>
            <td>novaguilds.guild.rank.delete</td>
            <td>Deleting ranks</td>
        </tr>
        <tr>
            <td>novaguilds.guild.rank.set</td>
            <td>Setting ranks</td>
        </tr>
        <tr>
            <td>novaguilds.region.access</td>
            <td>/g rg</td>
        </tr>
        <tr>
            <td>novaguilds.region.create</td>
            <td>/g rg buy</td>
        </tr>
        <tr>
            <td>novaguilds.region.resize</td>
            <td>Resize with the tool</td>
        </tr>
        <tr>
            <td>novaguilds.region.delete</td>
            <td>/g rg delete</td>
        </tr>
        <tr>
            <td>novaguilds.region.list</td>
            <td>/g rg list</td>
        </tr>
        <tr>
            <td>novaguilds.chat.notag</td>
            <td>No tag in chat</td>
        </tr>
        <tr>
            <td>novaguilds.playerinfo</td>
            <td>/pi or right click player with shift</td>
        </tr>
        <tr>
            <td>novaguilds.tool.check</td>
            <td>Checking regions with the tool</td>
        </tr>
        <tr>
            <td>novaguilds.tool.get</td>
            <td>/ng tool</td>
        </tr>
        <tr>
            <td>novaguilds.error</td>
            <td>Receive errors (for admins)</td>
        </tr>
        <tr>
            <td>novaguilds.novaguilds</td>
            <td>/novaguilds</td>
        </tr>
        <tr>
            <td>novaguilds.confirm</td>
            <td>/confirm</td>
        </tr>
    </tbody>
</table>
