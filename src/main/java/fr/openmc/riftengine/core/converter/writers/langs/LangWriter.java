package fr.openmc.riftengine.core.converter.writers.langs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.openmc.riftengine.core.RiftRegistry;
import fr.openmc.riftengine.core.converter.writers.PackWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LangWriter implements PackWriter {

    private static final Pattern LOCALE_FILENAME = Pattern.compile("^([a-z]+)_([a-z]+)\\.json$");


    @Override
    public void write(Path bedrockRootPath, Path javaRootPath) throws IOException {
        Map<String, Map<String, String>> entriesByLocale = scanAllLocales(javaRootPath);

        Path textsDir = bedrockRootPath.resolve("texts");
        Files.createDirectories(textsDir);

        for (Map.Entry<String, Map<String, String>> localeEntry : entriesByLocale.entrySet()) {
            writeLangFile(textsDir, localeEntry.getKey(), localeEntry.getValue());
        }

        writeLanguagesJson(textsDir, entriesByLocale.keySet());
    }

    private Map<String, Map<String, String>> scanAllLocales(Path javaRootPath) throws IOException {
        // * Map [Locale -> Map [JavaKey -> BedrockKey]]
        Map<String, Map<String, String>> byLocale = new LinkedHashMap<>();

        Path assetsDir = javaRootPath.resolve("assets");
        if (!Files.isDirectory(assetsDir)) return byLocale;

        try (Stream<Path> namespaces = Files.list(assetsDir)) {
            for (Path namespaceDir : namespaces.filter(Files::isDirectory).toList()) {
                Path langDir = namespaceDir.resolve("lang");
                if (!Files.isDirectory(langDir)) continue;

                try (Stream<Path> langFiles = Files.list(langDir)) {
                    for (Path langFile : langFiles.filter(Files::isRegularFile).toList()) {
                        mergeLangFile(langFile, byLocale);
                    }
                }
            }
        }

        return byLocale;
    }

    private void mergeLangFile(Path langFile, Map<String, Map<String, String>> byLocale) throws IOException {
        String fileName = langFile.getFileName().toString();
        Matcher matcher = LOCALE_FILENAME.matcher(fileName);
        if (!matcher.matches()) return;

        String bedrockLocale = toBedrockLocale(matcher.group(1), matcher.group(2));
        Map<String, String> bedrockEntries = byLocale.computeIfAbsent(bedrockLocale,
                k -> new LinkedHashMap<>());

        for (Map.Entry<String, String> entry : readJsonLang(langFile).entrySet()) {
            String javaKey = entry.getKey();

            Map<String, String> langsMapping = RiftRegistry.MAPPINGS.LANG_MAPPING.asMap();

            bedrockEntries.put(langsMapping.getOrDefault(javaKey, javaKey), entry.getValue());
        }
    }

    private Map<String, String> readJsonLang(Path path) throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (String key : json.keySet()) {
                entries.put(key, json.get(key).getAsString());
            }
        }
        return entries;
    }

    private String toBedrockLocale(String language, String region) {
        return language.toLowerCase(Locale.ROOT) + "_" + region.toUpperCase(Locale.ROOT);
    }

    private void writeLangFile(Path textsDir, String bedrockLocale, Map<String, String> entries) throws IOException {
        Path langFile = textsDir.resolve(bedrockLocale + ".lang");
        try (BufferedWriter writer = Files.newBufferedWriter(langFile, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        }
    }

    private void writeLanguagesJson(Path textsDir, java.util.Set<String> locales) throws IOException {
        String json = locales.stream()
                .map(locale -> "\"" + locale + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        Files.writeString(textsDir.resolve("languages.json"), json, StandardCharsets.UTF_8);
    }
}
