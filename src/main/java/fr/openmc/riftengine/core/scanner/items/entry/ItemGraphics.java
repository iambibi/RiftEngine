package fr.openmc.riftengine.core.scanner.items.entry;

import fr.openmc.riftengine.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.Map;

public record ItemGraphics(
        String texture,
        String model,
        Material material
) {
    public static ItemGraphics from(Map<?, ?> data) {
        if (!(data.get("graphics") instanceof Map<?, ?> graphics)) {
            return new ItemGraphics(null, null, Material.PAPER);
        }

        String texture = YmlUtils.getString(graphics.get("texture"), null);
        String model = YmlUtils.getString(graphics.get("model"), null);
        Material material = Material.valueOf(YmlUtils.getString(
                data.get("material"), ItemResource.DEFAULT_MATERIAL).toUpperCase());

        return new ItemGraphics(texture, model, material);
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public boolean hasModel() {
        return model != null;
    }

    public boolean isPresent() {
        return hasTexture() || hasModel();
    }
}