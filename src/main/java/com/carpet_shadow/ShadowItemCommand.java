package com.carpet_shadow;

import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.utility.ChatTextRenderHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ShadowItemCommand {

    private static final String SHADOW_ID_ARGUMENT_NAME = "shadow_id";

    private static CompletableFuture<Suggestions> getShadowIdSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(CarpetShadow.SHADOW_MAP.getShadowItems().keySet(), builder);
    }

    private static int deleteShadowItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerInventory inv = player.getInventory();

        int selectedSlot = inv.selectedSlot;
        ItemStack s = inv.getStack(selectedSlot);

        if (s.isEmpty() || !ShadowItem.fromItemStack(s).carpet_shadow$hasShadowId()) {
            Messenger.m(player, "r Selected slot doesn't contain shadow item");
            return 0;
        }

        ItemStack cpy = s.copy();
        CarpetShadow.SHADOW_MAP.deleteShadowItem(s);
        inv.setStack(selectedSlot, cpy);

        return 1;
    }

    private static int deleteShadowItemById(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerInventory inv = player.getInventory();

        String shadowId = StringArgumentType.getString(context, SHADOW_ID_ARGUMENT_NAME);
        if (!CarpetShadow.SHADOW_MAP.containsShadowItem(shadowId)) {
            Messenger.m(player, "r No shadow item with id '" + shadowId + "' found");
            return 0;
        }

        CarpetShadow.SHADOW_MAP.deleteShadowItem(CarpetShadow.SHADOW_MAP.getShadowItem(shadowId));
        return 1;
    }

    private static int createShadowItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerInventory inv = player.getInventory();

        ItemStack handStack = inv.getStack(inv.selectedSlot);
        if (handStack.isEmpty()) {
            Messenger.m(player, "r No item selected");
            return 0;
        }

        if (!inv.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty()) {
            Messenger.m(player, "r Offhand must be empty");
            return 0;
        }

        inv.setStack(PlayerInventory.OFF_HAND_SLOT, CarpetShadow.SHADOW_MAP.getOrAddShadowItem(handStack));

        return 1;
    }

    private static int printInfoShadowItem(CommandContext<ServerCommandSource> context) {
        ServerCommandSource src = context.getSource();

        if (CarpetShadow.SHADOW_MAP.getSize() == 0) {
            Messenger.m(src, "r Currently no shadow items loaded");
            return 0;
        }

        String barSeparator = " | ";
        int barSepLen = ChatTextRenderHelper.getWidthOfText(barSeparator);

        int idLen = 0;
        int itemLen = 0;
        for (Map.Entry<String, ItemStack> e : CarpetShadow.SHADOW_MAP.getShadowItems().entrySet()) {
            idLen = Math.max(idLen, ChatTextRenderHelper.getWidthOfText(e.getKey() + " "));
            itemLen = Math.max(itemLen, ChatTextRenderHelper.getWidthOfText(e.getValue().toString()));
        }

        String idLiteral = ChatTextRenderHelper.padSpaces("ID", idLen);
        String itemLiteral = ChatTextRenderHelper.padSpaces("Item", itemLen);

        String separator = "w -" + ChatTextRenderHelper.repeatToWidth("-", idLen + barSepLen + itemLen);

        Messenger.m(src, separator);
        Messenger.m(src, "w " + idLiteral + barSeparator + itemLiteral);
        Messenger.m(src, separator);

        for (Map.Entry<String, ItemStack> e : CarpetShadow.SHADOW_MAP.getShadowItems().entrySet()) {
            String idStr = ChatTextRenderHelper.padSpaces(e.getKey(), idLen);
            String itemStr = e.getValue().toString();
            Messenger.m(src, "d " + idStr, "w " + barSeparator + itemStr);
        }

        Messenger.m(src, separator);
        return 1;
    }

    private static int unlinkShadowItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerInventory inv = player.getInventory();

        int selectedSlot = inv.selectedSlot;
        ItemStack s = inv.getStack(selectedSlot);

        if (s.isEmpty() || !ShadowItem.fromItemStack(s).carpet_shadow$hasShadowId()) {
            Messenger.m(player, "r Selected slot doesn't contain shadow item");
            return 0;
        }

        inv.setStack(selectedSlot, s.copy());
        return 1;
    }

    private static int getShadowItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerInventory inv = player.getInventory();

        int selectedSlot = inv.selectedSlot;
        ItemStack s = inv.getStack(selectedSlot);

        if (!s.isEmpty()) {
            Messenger.m(player, "r Selected slot needs to be empty");
            return 0;
        }

        String shadowId = StringArgumentType.getString(context, SHADOW_ID_ARGUMENT_NAME);
        if (!CarpetShadow.SHADOW_MAP.containsShadowItem(shadowId)) {
            Messenger.m(player, "r No shadow item with id '" + shadowId + "' found");
            return 0;
        }

        inv.setStack(selectedSlot, CarpetShadow.SHADOW_MAP.getShadowItem(shadowId));
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        LiteralArgumentBuilder<ServerCommandSource> cmd =
                CommandManager.literal("shadowItem")
                        .requires(src -> CommandHelper.canUseCommand(src, CarpetShadowSettings.shadowItemCommand) && src.isExecutedByPlayer())
                        .then(CommandManager.literal("delete")
                                .executes(ShadowItemCommand::deleteShadowItem)
                                .then(CommandManager.argument(SHADOW_ID_ARGUMENT_NAME, StringArgumentType.word())
                                        .suggests(ShadowItemCommand::getShadowIdSuggestions)
                                        .executes(ShadowItemCommand::deleteShadowItemById)
                                )
                        )
                        .then(CommandManager.literal("create").executes(ShadowItemCommand::createShadowItem))
                        .then(CommandManager.literal("info").executes(ShadowItemCommand::printInfoShadowItem))
                        .then(CommandManager.literal("unlink").executes(ShadowItemCommand::unlinkShadowItem))
                        .then(CommandManager.literal("get")
                                .then(CommandManager.argument(SHADOW_ID_ARGUMENT_NAME, StringArgumentType.word())
                                        .suggests(ShadowItemCommand::getShadowIdSuggestions)
                                        .executes(ShadowItemCommand::getShadowItem)
                                )
                        );
        dispatcher.register(cmd);
    }
}
