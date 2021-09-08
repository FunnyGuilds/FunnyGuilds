package net.dzikoysk.funnyguilds;

import groovy.transform.CompileStatic;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@CompileStatic
@ExtendWith(MockitoExtension.class)
class BukkitSpec {

    @BeforeEach
    void prepareBukkit() {
        MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(() -> Bukkit.getPlayer(any(UUID.class))).thenReturn(null);
    }

}
