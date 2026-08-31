package fr.openmc.riftengine.core.utils;

public class IdentifierUtils {
    /**
     * Normalise un identifiant (qui n'a pas un namespace devant
     * @param id l'id (font/default.png, minecraft:font/default.png)
     * @return l'id final
     */
    public static String normalizeId(String id) {
        if (id.split(":").length == 2) return id;

        return "minecraft:" + id;
    }
}
