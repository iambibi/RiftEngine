package fr.openmc.riftengine.core.converter.writers.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.riftengine.core.RiftPlugin;
import fr.openmc.riftengine.core.RiftRegistry;
import fr.openmc.riftengine.core.converter.writers.PackWriter;
import fr.openmc.riftengine.core.scanner.items.ItemEntry;
import fr.openmc.riftengine.core.utils.PathUtils;
import org.bukkit.Material;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class ItemsMappingWriter implements PackWriter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws Exception {
        JsonObject root = new JsonObject();
        root.addProperty("format_version", 2);

        JsonObject itemsSetter = new JsonObject();
        Map<Material, Map<String, Integer>> mappedCmd = RiftRegistry.SCANNERS.CUSTOM_MODEL_DATA_CACHE.scan(null);

        for (Material material : mappedCmd.keySet()) {
            JsonArray itemsDefinitions = new JsonArray();
            Map<String, Integer> namespaceCmd = mappedCmd.get(material);

            for (String namespace : namespaceCmd.keySet()) {
                JsonObject itemDefinition = new JsonObject();
                itemDefinition.addProperty("type", "legacy");
                itemDefinition.addProperty("custom_model_data", namespaceCmd.get(namespace));
                itemDefinition.addProperty("bedrock_identifier", namespace);

                JsonObject bedrockOptions = new JsonObject();
                bedrockOptions.addProperty("icon", namespace);
                itemDefinition.add("bedrock_options", bedrockOptions);

                itemsDefinitions.add(itemDefinition);
            }

            itemsSetter.add(material.getKey().asString(), itemsDefinitions);
        }

        root.add("items", itemsSetter);

        Path outputFile = PathUtils.getGeyserPath(RiftPlugin.getInstance().getDataPath())
                .resolve("custom_mappings").resolve("ia-injected.json");
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, GSON.toJson(root), StandardCharsets.UTF_8);
    }
}