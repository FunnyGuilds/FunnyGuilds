package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionControllerType;

public class PermissionConfiguration extends OkaeriConfig {
    
    @Comment("Sposób obsługi uprawnień gildii:")
    @Comment(" - STATIC: Uprawnienia są z góry ustalone w kodzie pluginu i nie można ich zmieniać.")
    @Comment(" - EVENT: Uprawnienia są obsługiwane poprzez eventy (API), co pozwala innym pluginom na ich modyfikację. Jeśli żaden plugin nie obsłuży eventu, gracze zawsze nie będą mieli uprawnień.")
    @Comment("          Wiadomości o braku uprawnień również muszą być obsłużone przez inny plugin.")
    @Comment(" - MIXED: Uprawnienia są obsługiwane poprzez eventy (API), ale jeśli żaden plugin nie obsłuży eventu, używane są uprawnienia statyczne z kodu pluginu.")
    public GuildPermissionControllerType guildPermissionHandlingType = GuildPermissionControllerType.MIXED;
}
