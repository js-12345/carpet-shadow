package com.carpet_shadow;

import com.carpet_shadow.utility.ShadowInvUpdates;
import com.carpet_shadow.utility.ShadowStackMerger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ClientCarpetShadowInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ShadowStackMerger.clear();
            ShadowInvUpdates.clear();
        });
    }
}
