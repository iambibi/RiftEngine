package fr.openmc.riftengine.core.scanner.items;

import fr.openmc.riftengine.core.scanner.items.entry.ItemGraphics;
import fr.openmc.riftengine.core.scanner.items.entry.ItemResource;
import fr.openmc.riftengine.core.utils.IdentifierUtils;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

public record ItemEntry(
        String namespace,
        String key,

        // * Méthodes de création d'items (moderne ou legacy)
        ItemResource resource,
        ItemGraphics graphics,
        Function<Path, Path> resourcePath,

        Path sourceYml
) {
    public static ItemEntry from(Path sourceYml, String namespace, String key, Map<?, ?> data) {
        return new ItemEntry(
                namespace,
                key,
                ItemResource.from(data),
                ItemGraphics.from(data),
                sourceYml
        );
    }

    private static Function<Path, Path> resolvePathFunction(
            String namespace, Map<?, ?> data, ItemGraphics graphics, ItemResource resource) {

        // * Méthode legacy
        if (resource != null) {
            if (resource.hasTextures())
                // todo: support multiple textures
                return javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(resource.textures().getFirst(), namespace));
            else if (resource.hasModel())
                return javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(resource.model(), namespace));
        }

        // * Méthode graphics
        if (graphics.hasModel()) {
            return javaRoot -> IdentifierUtils.resolveModelId(javaRoot,
                    IdentifierUtils.normalizeId(graphics.model(), namespace));
        }
        if (graphics.hasTexture()) {
            return javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                    IdentifierUtils.normalizeId(graphics.texture(), namespace));
        }

        return javaRoot -> null;
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}