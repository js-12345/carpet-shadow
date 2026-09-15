package com.carpet_shadow;

import com.carpet_shadow.interfaces.ShadowItem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CarpetShadowCommands {

    private static void registerCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, cra, env) -> dispatcher.register(builder)
        );
    }

    public static void init() {
        registerCommand(
                CommandManager.literal("carpetShadowItemDelete")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    PlayerInventory inv = player.getInventory();

                                    int selectedSlot = inv.selectedSlot;
                                    ItemStack s = inv.getStack(selectedSlot);
                                    if (!s.isEmpty() && ShadowItem.fromItemStack(s).carpet_shadow$hasShadowId()) {
                                        ItemStack cpy = s.copy();
                                        s.setCount(0);
                                        inv.setStack(selectedSlot, cpy);
                                    }

                                    return 1;
                                }
                        )
        );

        registerCommand(
                CommandManager.literal("carpetShadowItemCreate")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    PlayerInventory inv = player.getInventory();

                                    ItemStack handStack = inv.getStack(inv.selectedSlot);
                                    if (inv.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty()) {
                                        ShadowItem sHandStack = ShadowItem.fromItemStack(handStack);

                                        String shadowId = sHandStack.carpet_shadow$getShadowId();
                                        if (shadowId == null)
                                            shadowId = CarpetShadow.shadow_id_generator.nextString();

                                        inv.setStack(PlayerInventory.OFF_HAND_SLOT, Globals.getByIdOrAdd(shadowId, handStack));
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.literal("Offhand must be empty"), false);
                                    }

                                    return 1;
                                }
                        )
        );
    }
}
