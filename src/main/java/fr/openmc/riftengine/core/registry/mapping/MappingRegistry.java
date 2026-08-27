package fr.openmc.riftengine.core.registry.mapping;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.riftengine.core.registry.mapping.content.LangMapping;

public class MappingRegistry extends Registry<String, Mapping<?, ?>> {
    // ** REGISTER MAPPING **
    public final Mapping<?, ?> LANG_MAPPING = register("langs", new LangMapping());
}