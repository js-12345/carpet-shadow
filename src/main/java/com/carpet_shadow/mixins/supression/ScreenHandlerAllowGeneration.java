package com.carpet_shadow.mixins.supression;

import com.carpet_shadow.CarpetShadowSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerAllowGeneration {

    // ADDING SHADOW SUPPRESSION GENERATION FROM CARPET-FIXES FROM FX-PROCESS
    // Copy mixins for rule reIntroduceItemShadowing
    // Only

    @Unique
    private int carpet_shadow$lastButton = 0;

    @Redirect(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 1
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            require = 0
    )
    private void carpet_shadow$dontRunBeforeInventoryUpdate(PlayerInventory instance, int slot, ItemStack stack) {
        if (!CarpetShadowSettings.shadowSuppressionGeneration) {
            instance.setStack(slot, stack);
        }
    }

    @Inject(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            require = 0
    )
    private void carpet_shadow$runAfterInventoryUpdate(int slotIndex, int button, SlotActionType actionType,
                                                       PlayerEntity player, CallbackInfo ci) {
        if (CarpetShadowSettings.shadowSuppressionGeneration) {
            player.getInventory().setStack(button, ItemStack.EMPTY);
        }
    }

    @Redirect(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            require = 0
    )
    private void carpet_shadow$dontRunBeforeSecondInventoryUpdate(PlayerInventory instance, int slot, ItemStack stack) {
        if (!CarpetShadowSettings.shadowSuppressionGeneration) {
            instance.setStack(slot, stack);
        } else {
            carpet_shadow$lastButton = slot;
        }
    }

    @Redirect(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;" +
                            "Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            require = 0
    )
    private void carpet_shadow$runAfterSecondInventoryUpdate(Slot instance, PlayerEntity player, ItemStack stack) {
        if (CarpetShadowSettings.shadowSuppressionGeneration) {
            player.getInventory().setStack(carpet_shadow$lastButton, stack);
        }
        instance.onTakeItem(player, stack);
    }

    @Inject(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "QUICK_MOVE:Lnet/minecraft/screen/slot/SlotActionType;",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            require = 0
    )
    private void carpet_shadow$runBeforeThirdInventoryUpdate(int slotIndex, int button, SlotActionType actionType,
                                                             PlayerEntity player, CallbackInfo ci, PlayerInventory inventory,
                                                             ClickType clickType, Slot slot, ItemStack itemStack,
                                                             ItemStack itemStack5) {
        if (CarpetShadowSettings.shadowSuppressionGeneration) {
            slot.setStackNoCallbacks(itemStack5);
        }
    }

    @Redirect(
            method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "QUICK_MOVE:Lnet/minecraft/screen/slot/SlotActionType;",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/screen/slot/SlotActionType;" +
                                    "SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            require = 0
    )
    private void carpet_shadow$dontRunBeforeThirdInventoryUpdate(Slot slot, ItemStack stack) {
        if (!CarpetShadowSettings.shadowSuppressionGeneration) {
            slot.setStackNoCallbacks(stack);
        }
    }
}
