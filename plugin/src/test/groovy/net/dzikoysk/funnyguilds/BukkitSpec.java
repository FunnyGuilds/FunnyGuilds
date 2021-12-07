package net.dzikoysk.funnyguilds;

import groovy.transform.CompileStatic;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@CompileStatic
@ExtendWith(MockitoExtension.class)
public class BukkitSpec {

    protected static MockedStatic<Bukkit> mockedBukkit;

    @BeforeAll
    static void openMockedBukkit() {
        mockedBukkit = mockStatic(Bukkit.class);
    }

    @BeforeEach
    protected void prepareBukkit() {
        World world = mock(World.class);

        mockedBukkit.when(() -> Bukkit.getPlayer(any(UUID.class))).thenReturn(null);
        mockedBukkit.when(Bukkit::getWorlds).thenReturn(Collections.singletonList(world));
    }

    @AfterAll
    static void closeMockedBukkit() {
        mockedBukkit.close();
    }

}
