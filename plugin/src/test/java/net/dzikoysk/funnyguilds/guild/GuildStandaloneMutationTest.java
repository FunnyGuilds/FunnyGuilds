package net.dzikoysk.funnyguilds.guild;

import java.time.Instant;
import java.util.Set;
import net.dzikoysk.funnyguilds.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuildStandaloneMutationTest {

    @Test
    void mutatesMembersWithoutInitializedPlugin() {
        // FunnyGuilds.getInstance() is null in a bare test JVM. Guild is a pure data object:
        // its mutations must work with no plugin, guild manager or rank service around.
        Guild guild = new Guild("Standalone", "TAG");
        User owner = Mockito.mock(User.class);
        User member = Mockito.mock(User.class);

        assertDoesNotThrow(() -> guild.setOwner(owner));
        assertTrue(guild.isMember(owner));

        assertDoesNotThrow(() -> guild.addMember(member));
        assertTrue(guild.isMember(member));

        assertDoesNotThrow(() -> guild.setMembers(Set.of(owner, member)));
        assertTrue(guild.isMember(owner));
        assertTrue(guild.isMember(member));

        assertDoesNotThrow(() -> guild.removeMember(member));
        assertFalse(guild.isMember(member));
    }

    @Test
    void storesValidityWithoutInitializedPlugin() {
        // The null/born fallback lives in DeserializationUtils now; the setter is pure.
        Guild guild = new Guild("Standalone", "TAG");

        assertDoesNotThrow(() -> guild.setValidity(Instant.now().plusSeconds(60)));
        assertTrue(guild.isValid());
    }

    @Test
    void pureMutationsNeverTouchUserBackReferences() {
        // Back-reference repair belongs to GuildManager; the data object must leave User alone.
        Guild guild = new Guild("Standalone", "TAG");
        User joined = Mockito.mock(User.class);
        User removed = Mockito.mock(User.class);

        guild.setMembers(Set.of(joined, removed));
        guild.removeMember(removed);
        guild.addMember(removed);

        Mockito.verify(joined, Mockito.never()).setGuild(Mockito.any());
        Mockito.verify(joined, Mockito.never()).removeGuild();
        Mockito.verify(removed, Mockito.never()).setGuild(Mockito.any());
        Mockito.verify(removed, Mockito.never()).removeGuild();
    }

    @Test
    void addMemberReportsWhetherTheMembershipChanged() {
        Guild guild = new Guild("Standalone", "TAG");
        User member = Mockito.mock(User.class);

        assertTrue(guild.addMember(member));
        assertFalse(guild.addMember(member));
        assertEquals(1, guild.getMembers().size());

        assertTrue(guild.removeMember(member));
        assertFalse(guild.removeMember(member));
    }

}
