package com.carpet_shadow;

import com.carpet_shadow.interfaces.ShadowItem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

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
                        ));
    }
}
