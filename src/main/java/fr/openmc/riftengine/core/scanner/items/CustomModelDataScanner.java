package fr.openmc.riftengine.core.scanner.items;

import fr.openmc.riftengine.core.RiftPlugin;
import fr.openmc.riftengine.core.RiftRegistry;
import fr.openmc.riftengine.core.registry.scanner.AbstractScanner;
import fr.openmc.riftengine.core.utils.PathUtils;
import fr.openmc.riftengine.core.utils.YmlUtils;
import org.bukkit.Material;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scan le cache des CustomModelData qui sont assignés par ItemsAdder
 */
public class CustomModelDataScanner extends AbstractScanner<Map<Material, Map<String, Integer>>, Void> {
    private final String CACHE_FILE_NAME = "items_ids_cache.yml";

    public Map<Material, Map<String, Integer>> scan(Void voyd) throws Exception {
        Path cmdPath = PathUtils.getItemsAdderPath(RiftPlugin.getInstance().getDataPath()).resolve("storage").resolve(CACHE_FILE_NAME);

        Map<String, Object> root = YmlUtils.loadYml(cmdPath);
        Map<Material, Map<String, Integer>> mappedCache = new HashMap<>();

        for (var iterRoot : root.entrySet()) {
            String key = iterRoot.getKey();
            Material materialKey = Material.valueOf(key);

            Map<String, Integer> value = new HashMap<>();

            if (!(iterRoot.getValue() instanceof Map<?, ?> valueMap)) continue;

            Map<String, Integer> namespacedIdCmdMap = (Map<String, Integer>) valueMap;

            for (var entryIdCmd : namespacedIdCmdMap.entrySet()) {
                String namespacedId = entryIdCmd.getKey();
                // todo: __manually_handled, qui serait utile lors de l'impl des CustomEmotes et des models par ailleurs
                if (namespacedId.startsWith("__")) continue;

                Integer cmd = entryIdCmd.getValue();

                value.put(namespacedId, cmd);
            }

            mappedCache.put(materialKey, value);
        }

        return mappedCache;
    }
}
