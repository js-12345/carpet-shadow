package com.carpet_shadow;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.carpet_shadow.utility.CarpetShadowMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class CarpetShadow implements CarpetExtension, ModInitializer {

    public static final String MOD_ID = "carpet-shadow";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static CarpetShadowMap SHADOW_MAP;

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetShadowSettings.class);
        CarpetShadow.LOGGER.info("Carpet Shadow Loaded!");
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new CarpetShadow());

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            SHADOW_MAP = CarpetShadowMap.loadShadowMapFromServer(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            SHADOW_MAP = null;
        });

        ServerLifecycleEvents.BEFORE_SAVE.register((server, b1, b2) -> {
            SHADOW_MAP.markDirty();
        });
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        ShadowItemCommand.register(dispatcher, commandBuildContext);
    }

    // TODO: change update suppression information with carpet-fixes on the fly?
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        InputStream langFile = getClass().getClassLoader().getResourceAsStream("assets/carpet-shadow/lang/%s.json".formatted(lang));
        if (langFile == null) {
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().setLenient().create();
        return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {
        }.getType());
    }
}
