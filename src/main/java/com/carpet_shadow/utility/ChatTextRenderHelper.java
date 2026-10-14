package com.carpet_shadow.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class ChatTextRenderHelper {

    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().inGameHud.getTextRenderer();

    public static int getWidthOfText(String text) {
        return TEXT_RENDERER.getWidth(text);
    }

    public static String trimToWidth(String text, int width) {
        return TEXT_RENDERER.trimToWidth(text, width);
    }

    public static String padSpaces(String text, int width) {
        return trimToWidth(text + " ".repeat(width), width);
    }

    public static String repeatToWidth(String text, int width) {
        return ChatTextRenderHelper.trimToWidth(text.repeat(width), width);
    }
}
