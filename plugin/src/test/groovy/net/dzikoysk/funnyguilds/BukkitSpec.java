package net.dzikoysk.funnyguilds;

import groovy.transform.CompileStatic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@CompileStatic
@ExtendWith(MockitoExtension.class)
class BukkitSpec {

    private static MockedStatic<Bukkit> mockedBukkit;

    @BeforeAll
    static void openMockedBukkit() {
        mockedBukkit = mockStatic(Bukkit.class);
    }

    @BeforeEach
    void prepareBukkit() {
        Player player = mock(Player.class);

        mockedBukkit.when(() -> Bukkit.getPlayer(any(UUID.class))).thenAnswer(invocation -> {
            if (invocation.getArguments()[0].equals(UUID.nameUUIDFromBytes("online".getBytes()))) {
                return player;
            }

            return null;
        });
    }

    @AfterAll
    static void closeMockedBukkit() {
        mockedBukkit.close();
    }

}
