package fr.openmc.riftengine.core;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class RiftConfig {
    private final FileConfiguration config;

    public RiftConfig(RiftPlugin plugin, FileConfiguration config) throws IOException {
        this.config = config;

        plugin.saveDefaultConfig();

        load();
    }

    @Getter
    private boolean hideScoreboardNumberBedrock;

    public void load() {
        this.hideScoreboardNumberBedrock = config.getBoolean("bedrock.hide-scoreboard-number", true);
    }
}
