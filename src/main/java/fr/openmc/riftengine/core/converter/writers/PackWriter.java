package fr.openmc.riftengine.core.converter.writers;

import java.io.IOException;
import java.nio.file.Path;

public interface PackWriter {
    void write(Path bedrockRootPath, Path javaRootPath) throws IOException;
}
