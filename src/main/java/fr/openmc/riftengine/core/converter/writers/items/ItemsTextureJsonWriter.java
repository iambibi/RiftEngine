package fr.openmc.riftengine.core.converter.writers.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.riftengine.core.converter.writers.PackWriter;
import fr.openmc.riftengine.core.scanner.items.ItemEntry;
import fr.openmc.riftengine.core.utils.IdentifierUtils;
import fr.openmc.riftengine.core.utils.PathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

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
            String resourceId = itemEntry.getBestResourceId();
            String texture = null;

            if (resourceId == null) {
                texture = "textures/items/" + itemEntry.getMaterial().name().toLowerCase();;
            } else if (resourceId.startsWith("minecraft:")) {
                String path = IdentifierUtils.normalizeId(resourceId).split(":", 2)[1];
                texture = IdentifierUtils.toBedrockTexturePath("textures/" + path);
            } else {
                Path resourcePath = itemEntry.getResourcePath().apply(javaRootPath);
                Path reducedPath = resourcePath == null ? null
                        : PathUtils.getPathFromRoot(resourcePath, "textures");
                if (reducedPath != null) {
                    texture = IdentifierUtils.toBedrockTexturePath(reducedPath.toString())
                            .replaceFirst("\\.[^/.]+$", "");
                }
            }

            if (texture == null) {
                OMCLogger.warn("Item {} : impossible de déterminer la texture", itemEntry.namespacedId());
                continue;
            }

            JsonObject itemData = new JsonObject();
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
