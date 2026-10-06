package com.carpet_shadow;

import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import com.carpet_shadow.interfaces.ShadowItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ShadowItemCommand {

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
        s.setCount(0);
        inv.setStack(selectedSlot, cpy);

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

        ShadowItem sHandStack = ShadowItem.fromItemStack(handStack);

        String shadowId = sHandStack.carpet_shadow$getShadowId();
        if (shadowId == null)
            shadowId = CarpetShadow.shadow_id_generator.nextString();

        inv.setStack(PlayerInventory.OFF_HAND_SLOT, Globals.getByIdOrAdd(shadowId, handStack));

        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        LiteralArgumentBuilder<ServerCommandSource> cmd =
                CommandManager.literal("shadowItem")
                        .requires(src -> CommandHelper.canUseCommand(src, CarpetShadowSettings.shadowItemCommand) && src.isExecutedByPlayer())
                        .then(CommandManager.literal("delete").executes(ShadowItemCommand::deleteShadowItem))
                        .then(CommandManager.literal("create").executes(ShadowItemCommand::createShadowItem));
        dispatcher.register(cmd);
    }
}
