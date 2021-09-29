package net.dzikoysk.funnyguilds.feature.placeholders;

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.UserManager
import org.bukkit.ChatColor
import org.junit.jupiter.api.Test
import panda.std.Pair

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class PlaceholdersTest extends FunnyGuildsSpec {

    @Test
    void 'test ONLINE placeholder' () {
        def text = "§7offline - <online>online</online> - offline - <online>online</online> - offline"
        def formatter = Placeholders.ONLINE.toFormatter(ChatColor.getLastColors(text.split("<online>")[0]))


        text = formatter.format text

        assertEquals '§7offline - §aonline§7 - offline - §aonline§7 - offline', text
    }

    @Test
    void 'test GUILD_MEMBERS_COLOR_CONTEXT placeholder' () {
        def userManager = new UserManager()
        def guild = new Guild("guild")
        def user1 = userManager.create(UUID.randomUUID(), "user1")
        def user2 = userManager.create(UUID.nameUUIDFromBytes("online".getBytes()), "user2")
        def user3 = userManager.create(UUID.randomUUID(), "user3")

        guild.addMember(user1)
        guild.addMember(user2)
        guild.addMember(user3)

        def formatter = Placeholders.GUILD_MEMBERS_COLOR_CONTEXT.toFormatter(Pair.of("§7", guild))
        def text = formatter.format "§7{MEMBERS}"

        text = formatter.format(text)

        assertEquals '§7user1, §auser2§7, user3', text
    }

}
