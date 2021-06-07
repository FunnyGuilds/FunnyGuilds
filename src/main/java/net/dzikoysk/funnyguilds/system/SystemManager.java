package net.dzikoysk.funnyguilds.system;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.element.DummyManager;
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.war.WarSystem;

public class SystemManager {

    private final BanSystem banSystem;
    private final ProtectionSystem protectionSystem;
    private final SecuritySystem securitySystem;
    private final WarSystem warSystem;
    private final DummyManager dummyManager;
    private final IndividualPrefixManager individualPrefixManager;

    public SystemManager(FunnyGuilds plugin) {
        this.banSystem = new BanSystem(plugin);
        this.protectionSystem = new ProtectionSystem(plugin);
        this.securitySystem = new SecuritySystem(plugin);
        this.warSystem = new WarSystem(plugin);
        this.dummyManager = new DummyManager(plugin);
        this.individualPrefixManager = new IndividualPrefixManager(plugin);
    }

    public BanSystem getBanSystem() {
        return banSystem;
    }

    public ProtectionSystem getProtectionSystem() {
        return protectionSystem;
    }

    public SecuritySystem getSecuritySystem() {
        return securitySystem;
    }

    public WarSystem getWarSystem() {
        return warSystem;
    }

    public DummyManager getDummyManager() {
        return dummyManager;
    }

    public IndividualPrefixManager getIndividualPrefixManager() {
        return individualPrefixManager;
    }

}
