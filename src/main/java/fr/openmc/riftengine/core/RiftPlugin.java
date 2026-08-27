package fr.openmc.riftengine.core;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.riftengine.core.converter.ConverterManager;
import fr.openmc.riftengine.core.listeners.SessionLoadResourcePackListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;

import java.nio.file.Path;

public class RiftPlugin extends JavaPlugin {
    @Getter
    private static RiftPlugin instance;

    @Getter
    @Setter
    private static ResourcePack resourcePack;

    private ConverterManager converterManager;

    @Override
    public void onEnable() {
        instance = this;

        RiftRegistry.initAll();

        converterManager = new ConverterManager(this);
        updateBedrockResourcePack();

        registerEvent(
                new SessionLoadResourcePackListener()
        );

        OMCLogger.info("RiftEngine activé!");
    }

    @Override
    public void onDisable() {
        RiftRegistry.stopAll();

        OMCLogger.info("RiftEngine désactivé!");
    }

    public void updateBedrockResourcePack() {
        try {
            Path generatedBedrockPack = converterManager.generateConvertedPack();
            resourcePack = ResourcePack.builder(PackCodec.path(generatedBedrockPack)).build();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du resourcepack bedrock", e);
        }
    }

    public static void registerEvent(EventRegistrar registrar) {
        GeyserApi.api().eventBus().register(registrar, registrar);
    }

    public static void registerEvent(EventRegistrar... registrars) {
        for (EventRegistrar registrar : registrars) {
            registerEvent(registrar);
        }
    }
}
