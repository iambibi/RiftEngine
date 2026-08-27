package fr.openmc.riftengine.core.registry.mapping;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.riftengine.core.RiftPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class JsonMapping implements Mapping<String, String> {
    private final Map<String, String> entries = new HashMap<>();

    /**
     * Chemin du fichier de mapping JSON, ex: "mappings/langs/mapping_26_2.json".
     * @return Chemin relatif en partant de resources
     */
    public abstract String getBaseFileName();

    @Override
    public void load() {
        entries.clear();
        mergeFrom(getBaseFileName());

        OMCLogger.infoFormatted(getClass().getSimpleName() + " chargé avec " + entries.size() + " entrées");
    }

    private void mergeFrom(String fileName) {
        try (InputStream is = RiftPlugin.getInstance().getResource(fileName)) {
            if (is == null) return;

            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : json.keySet()) {
                    entries.put(key, json.get(key).getAsString());
                }
            }
        } catch (IOException e) {
            OMCLogger.errorFormatted("Erreur de lecture du mapping " + fileName + " : " + e.getMessage());
        }
    }

    @Override
    public Optional<String> resolve(String javaKey) {
        return Optional.ofNullable(entries.get(javaKey));
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Map<String, String> asMap() {
        return entries;
    }

}
