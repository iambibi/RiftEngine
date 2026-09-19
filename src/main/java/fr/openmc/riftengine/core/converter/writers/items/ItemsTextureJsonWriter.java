package fr.openmc.riftengine.core.converter.writers.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.riftengine.core.converter.writers.PackWriter;
import fr.openmc.riftengine.core.scanner.items.ItemEntry;
import fr.openmc.riftengine.core.utils.PathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ItemsTextureJsonWriter implements PackWriter {
    private final List<ItemEntry> items;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ItemsTextureJsonWriter(List<ItemEntry> items) {
        this.items = items;
    }

    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws IOException {
        JsonObject root = new JsonObject();

        JsonObject textureData = new JsonObject();

        for (ItemEntry itemEntry : items) {
            JsonObject itemData = new JsonObject();

            Path resourcePath = itemEntry.resourcePath().apply(javaRootPath);
            if (resourcePath == null) {
                OMCLogger.warn("Item {} a aucune resource (Model ou texture)", itemEntry.namespacedId());
                continue;
            }

            Path reducedPath = PathUtils.getPathFromRoot(resourcePath, "textures");
            if (reducedPath == null) {
                OMCLogger.warn("Item {} a un Path impossible a réduire {}", itemEntry.namespacedId(), resourcePath.toString());
                continue;
            }

            String texture = reducedPath.toString().replaceFirst("\\.[^/.]+$", "");

            itemData.addProperty("textures", texture);

            textureData.add(itemEntry.namespacedId(), itemData);
        }

        root.addProperty("texture_name", "atlas.items");
        root.add("texture_data", textureData);

        Path outputFile = bedrockRootPath.resolve("textures").resolve("item_texture.json");
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, GSON.toJson(root), StandardCharsets.UTF_8);
    }
}
