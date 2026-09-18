package fr.openmc.riftengine.core.scanner.items.entry;

import fr.openmc.riftengine.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public record ItemResource(
        Material material,
        boolean generate,
        List<String> textures,
        String model,
        Integer modelId
) {
    public static final String DEFAULT_MATERIAL = "PAPER";

    public static ItemResource from(Map<?, ?> data) {
        Object resourceObj = data.get("resource");
        if (!(resourceObj instanceof Map<?, ?> resource)) {
            return new ItemResource(Material.valueOf(DEFAULT_MATERIAL), false, List.of(), null, null);
        }

        Material material = Material.valueOf(YmlUtils.getString(
                resource.get("material"), DEFAULT_MATERIAL).toUpperCase());

        boolean generate = YmlUtils.getBool(resource.get("generate"), false);

        List<String> textures = resource.get("textures") instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();

        String modelPath = resource.get("model_path") != null
                ? String.valueOf(resource.get("model_path"))
                : null;

        Integer modelId = resource.get("model_id") != null
                ? Integer.valueOf(String.valueOf(resource.get("model_id")))
                : null;

        return new ItemResource(material, generate, textures, modelPath, modelId);
    }

    public boolean hasCustomModel() {
        return model != null;
    }

    public boolean hasTextures() {
        return !textures.isEmpty();
    }

    public boolean hasModel() {
        return !model.isEmpty();
    }
}