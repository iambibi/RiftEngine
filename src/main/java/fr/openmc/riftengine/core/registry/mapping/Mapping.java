package fr.openmc.riftengine.core.registry.mapping;

import java.util.Map;
import java.util.Optional;

public interface Mapping<K, V> {
    void load();
    Optional<V> resolve(K javaKey);
    int size();
    Map<K, V> asMap();

    default Mapping<K, V> get() {
        return this;
    }
}
