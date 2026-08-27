package fr.openmc.riftengine.core;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geyser.api.event.EventRegistrar;

public class RiftPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        OMCLogger.info("RiftEngine activé!");
    }

    @Override
    public void onDisable() {
        OMCLogger.info("RiftEngine désactivé!");
    }
}
