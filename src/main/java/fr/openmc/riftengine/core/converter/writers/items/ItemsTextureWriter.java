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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ItemsTextureWriter implements PackWriter {
    private final List<ItemEntry> items;

    public ItemsTextureWriter(List<ItemEntry> items) {
        this.items = items;
    }

    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws IOException {
        for (ItemEntry itemEntry : items) {
            String ressouceId = itemEntry.getBestResourceId();
            if (ressouceId == null) {
                OMCLogger.warn("Item {} a pas de resource id", itemEntry.namespacedId());
                continue;
            }
            System.out.println(ressouceId);
            if (ressouceId.split(":")[0].equals("minecraft")) continue;

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

            Path outputFile = bedrockRootPath.resolve(reducedPath);
            Files.createDirectories(outputFile.getParent());
            Files.copy(resourcePath, outputFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}