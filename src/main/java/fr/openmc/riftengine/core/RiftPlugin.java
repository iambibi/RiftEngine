package fr.openmc.riftengine.core;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geyser.api.event.EventRegistrar;

public class RiftPlugin extends JavaPlugin {
    @Getter
    private static RiftPlugin instance;


    @Override
    public void onEnable() {
        instance = this;

        RiftRegistry.initAll();

        OMCLogger.info("RiftEngine activé!");
    }

    @Override
    public void onDisable() {
        RiftRegistry.stopAll();

        OMCLogger.info("RiftEngine désactivé!");
    }
}
