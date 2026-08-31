package fr.openmc.riftengine.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ScanUtils {
    /**
     * Scanne tous les fichiers d'un resourcepack java, dans un registre spécial
     * @param javaRootPath la racine du pack java
     * @param registryName le nom du registre (ex lang, font, ...)
     * @return la liste des chemins des fichiers
     * @throws IOException si erreur pdt la lecture
     */
    public static List<Path> scanAllPath(Path javaRootPath, String registryName) throws IOException {
        List<Path> localesPath = new ArrayList<>();

        Path assetsPath = javaRootPath.resolve("assets");
        if (!Files.isDirectory(assetsPath)) return localesPath;

        try (Stream<Path> namespaces = Files.list(assetsPath)) {
            for (Path namespaceDir : namespaces.filter(Files::isDirectory).toList()) {
                Path registryDir = namespaceDir.resolve(registryName);
                if (!Files.isDirectory(registryDir)) continue;

                try (Stream<Path> registryFile = Files.list(registryDir)) {
                    localesPath.addAll(registryFile.filter(Files::isRegularFile).toList());
                }
            }
        }
        return localesPath;
    }
}
