package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PermissionConfiguration extends OkaeriConfig {
    
    @Comment("Czy domyślny handler uprawnień FunnyGuilds ma być zarejestrowany?")
    @Comment("Używa on 3 podstawowych ról do obliczenia uprawnień: członek, zastępca i lider")
    @Comment("Uwaga: wyłączenie tej opcji wymaga zaimplementowania własnego handlera uprawnień")
    @Comment("W innym przypadku np. obcy gracze będą mogli niszczyć bloki gildii, a członkowie gildii będą mieli dostęp do wszystkich komend gildii")
    public boolean registerDefaultPermissionsHandler = true;
    
    @Comment("")
    @Comment("Czy domyślny handler wiadomości o braku uprawnień FunnyGuilds ma być zarejestrowany?")
    @Comment("Używa on wiadomości z pliku wiadomości FunnyGuilds")
    @Comment("Uwaga: wyłączenie tej opcji spowoduje, że gracze nie będą otrzymywać żadnych wiadomości, gdy nie będą mieli wymaganych uprawnień")
    public boolean registerDefaultPermissionFailMessagesHandler = true;
}
