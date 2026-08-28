package fr.openmc.riftengine.core.converter.writers.manifest;

import fr.openmc.riftengine.core.converter.writers.PackWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ManifestWriter implements PackWriter {

    private final PackIdentity identity;

    public ManifestWriter(PackIdentity identity) {
        this.identity = identity;
    }

    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws IOException {
        String manifest = """
            {
              "format_version": 2,
              "header": {
                "name": "RiftPack",
                "description": "Généré par RiftEngine à partir de celui de Java",
                "uuid": "%s",
                "version": [1, 0, 0],
                "min_engine_version": [26, 44, 0]
              },
              "modules": [
                {
                  "type": "resources",
                  "uuid": "%s",
                  "version": [1, 0, 0]
                }
              ]
            }
            """.formatted(identity.headerUuid(), identity.moduleUuid());

        Files.writeString(bedrockRootPath.resolve("manifest.json"), manifest, StandardCharsets.UTF_8);

    }
}
