package fr.openmc.riftengine.core.converter.writers.translations;

import com.google.gson.*;
import fr.openmc.riftengine.core.converter.writers.PackWriter;
import fr.openmc.riftengine.core.utils.ScanUtils;
import org.geysermc.geyser.api.GeyserApi;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pas réelement un pack writer, on mets juste les .json des langs dans plugins/Geyser-Spigot/locales/overrides.
 */
public class TranslationInjector implements PackWriter {

    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws IOException {
        Path localesOverrides = GeyserApi.api()
                .configDirectory()
                .resolve("locales")
                .resolve("overrides");

        Files.createDirectories(localesOverrides);

        List<Path> locales = ScanUtils.scanAllPath(javaRootPath, "lang");

        Path mergedDirectory = Files.createTempDirectory("merged-locales");
        List<Path> mergedLocales = mergeLocales(locales, mergedDirectory);

        for (Path langFile : mergedLocales) {
            Files.copy(
                    langFile,
                    localesOverrides.resolve(langFile.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private List<Path> mergeLocales(List<Path> localesPath, Path outputDirectory) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Map<String, JsonObject> mergedLocales = new HashMap<>();

        for (Path localePath : localesPath) {
            String fileName = localePath.getFileName().toString();

            if (!fileName.endsWith(".json")) continue;

            JsonObject locale;
            try (Reader reader = Files.newBufferedReader(localePath)) {
                locale = JsonParser.parseReader(reader).getAsJsonObject();
            }

            JsonObject merged = mergedLocales.computeIfAbsent(
                    fileName,
                    _ -> new JsonObject()
            );

            for (Map.Entry<String, JsonElement> entry : locale.entrySet()) {
                merged.add(entry.getKey(), entry.getValue());
            }
        }

        Files.createDirectories(outputDirectory);

        List<Path> mergedPaths = new ArrayList<>();

        for (Map.Entry<String, JsonObject> entry : mergedLocales.entrySet()) {
            Path outputPath = outputDirectory.resolve(entry.getKey());

            try (Writer writer = Files.newBufferedWriter(outputPath)) {
                gson.toJson(entry.getValue(), writer);
            }

            mergedPaths.add(outputPath);
        }

        return mergedPaths;
    }
}
