package fr.openmc.riftengine.core.scanner.items;

import fr.openmc.riftengine.core.scanner.items.entry.ItemGraphics;
import fr.openmc.riftengine.core.scanner.items.entry.ItemResource;
import fr.openmc.riftengine.core.utils.IdentifierUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

@Getter
public class ItemEntry {
    private final String namespace;
    private final String key;
    private final Integer customModelData;
    private final Material material;

    private final ItemResource resource;
    private final ItemGraphics graphics;
    private final Function<Path, Path> resourcePath;

    private final Path sourceYml;

    private ItemEntry(
            String namespace,
            String key,
            Integer customModelData,
            Material material,

            // * Méthodes de création d'items (moderne ou legacy)
            ItemResource resource,
            ItemGraphics graphics,
            Function<Path, Path> resourcePath,

            Path sourceYml
    ) {
        this.namespace = namespace;
        this.key = key;
        this.customModelData = customModelData;
        this.material = material;
        this.resource = resource;
        this.graphics = graphics;
        this.resourcePath = resourcePath;
        this.sourceYml = sourceYml;
    }

    public static ItemEntry from(Map<Material, Map<String, Integer>> cmdCache, Path sourceYml, String namespace, String key, Map<?, ?> data) {
        ItemResource itemResource = ItemResource.from(data);
        ItemGraphics itemGraphics = ItemGraphics.from(data);

        Material material = getMaterial(itemResource, itemGraphics);

        Integer customModelData = null;

        Map<String, Integer> map = cmdCache.get(material);
        if (map != null && map.get(namespace + ":" + key) != null)
            customModelData = map.get(namespace + ":" + key);

        return new ItemEntry(
                namespace,
                key,
                customModelData,
                material,
                itemResource,
                itemGraphics,
                resolvePathFunction(namespace, itemGraphics, itemResource),
                sourceYml
        );
    }

    public String getBestResourceId() {
        if (resource != null) {
            if (resource.hasTextures())
                return IdentifierUtils.normalizeId(resource.textures().getFirst(), namespace);
            else if (resource.hasModel())
                return IdentifierUtils.normalizeId(resource.model(), namespace);
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return IdentifierUtils.normalizeId(graphics.model(), namespace);
            }
            if (graphics.hasTexture()) {
                return IdentifierUtils.normalizeId(graphics.texture(), namespace);
            }
        }

        return null;
    }

    private static Material getMaterial(ItemResource resource, ItemGraphics graphics) {
        Material byDefault = Material.valueOf(ItemResource.DEFAULT_MATERIAL);
        Material resourceMaterial = resource.material();
        Material graphicsMaterial = graphics.material();

        if (resourceMaterial != byDefault)
            return resourceMaterial;
        if (graphicsMaterial != byDefault)
            return graphicsMaterial;

        return byDefault;
    }

    private static Function<Path, Path> resolvePathFunction(String namespace, ItemGraphics graphics, ItemResource resource) {
        // * Méthode legacy
        if (resource != null) {
            if (resource.hasTextures())
                // todo: support multiple textures
                return javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(resource.textures().getFirst(), namespace));
            else if (resource.hasModel())
                return javaRoot -> IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(resource.model(), namespace));
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return javaRoot -> IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.model(), namespace));
            }
            if (graphics.hasTexture()) {
                return javaRoot -> IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.texture(), namespace));
            }
        }

        return _ -> null;
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}