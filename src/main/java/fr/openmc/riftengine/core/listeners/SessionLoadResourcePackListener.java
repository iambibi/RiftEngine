package fr.openmc.riftengine.core.listeners;

import fr.openmc.riftengine.core.RiftPlugin;
import org.bukkit.event.Listener;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.bedrock.SessionLoadResourcePacksEvent;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;
import org.geysermc.geyser.api.pack.option.PriorityOption;
import org.geysermc.geyser.api.pack.option.ResourcePackOption;

public class SessionLoadResourcePackListener implements EventRegistrar {
    @Subscribe
    public void onBedrockPlayerJoin(SessionLoadResourcePacksEvent event) {
        if (RiftPlugin.getResourcePack() == null) {
            RiftPlugin.getInstance().updateBedrockResourcePack();
            RiftPlugin.setResourcePack(RiftPlugin.getResourcePack());
        }

        event.register(
                RiftPlugin.getResourcePack(),
                PriorityOption.HIGHEST
        );
    }
}
