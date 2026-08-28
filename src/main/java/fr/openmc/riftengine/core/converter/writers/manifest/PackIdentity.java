package fr.openmc.riftengine.core.converter.writers.manifest;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PackIdentity {
 
    private final UUID headerUuid;
    private final UUID moduleUuid;
 
    private PackIdentity(UUID headerUuid, UUID moduleUuid) {
        this.headerUuid = headerUuid;
        this.moduleUuid = moduleUuid;
    }
 
    public UUID headerUuid() {
        return headerUuid;
    }
 
    public UUID moduleUuid() {
        return moduleUuid;
    }
 
    public static PackIdentity loadOrCreate(JavaPlugin plugin) throws IOException {
        File file = new File(plugin.getDataFolder(), "pack-identity.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
 
        boolean changed = false;
        if (!config.contains("header-uuid")) {
            config.set("header-uuid", UUID.randomUUID().toString());
            changed = true;
        }
        if (!config.contains("module-uuid")) {
            config.set("module-uuid", UUID.randomUUID().toString());
            changed = true;
        }
        if (changed) config.save(file);
 
        return new PackIdentity(
            UUID.fromString(config.getString("header-uuid")),
            UUID.fromString(config.getString("module-uuid"))
        );
    }
}
