package fr.openmc.riftengine.core.scanner.items;

import fr.openmc.riftengine.core.RiftRegistry;
import fr.openmc.riftengine.core.registry.scanner.AbstractScanner;
import fr.openmc.riftengine.core.utils.YmlUtils;
import org.bukkit.Material;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scan tout les items initialisé par ItemsAdder
 */
public class ItemScanner extends AbstractScanner<List<ItemEntry>, Path> {
    public List<ItemEntry> scan(Path itemsAdderContentsPath) throws Exception {
        if (!Files.isDirectory(itemsAdderContentsPath)) return new ArrayList<>();

        List<Path> ymlFiles = RiftRegistry.SCANNERS.YAML.scan(itemsAdderContentsPath);

        List<ItemEntry> result = new ArrayList<>();

        for (Path ymlFile : ymlFiles) {
            Map<String, Object> root = YmlUtils.loadYml(ymlFile);

            Object itemsObj = root.get("items");
            if (!(itemsObj instanceof Map<?, ?> itemsMap)) continue;

            String namespace = RiftRegistry.SCANNERS.YAML_ITEMSADDER_NAMESPACE.scan(root);
            Map<Material, Map<String, Integer>> mappedCache = RiftRegistry.SCANNERS.CUSTOM_MODEL_DATA_CACHE.scan(null);

            for (Map.Entry<?, ?> entry : itemsMap.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (!(entry.getValue() instanceof Map<?, ?> data)) continue;

                result.add(ItemEntry.from(mappedCache, ymlFile, namespace, key, data));
            }
        }

        return result;
    }
}
