package com.carpet_shadow.mixins.supression;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerTrackShadowing {

    @Shadow
    public abstract Slot getSlot(int index);

    @Shadow
    public abstract ItemStack getCursorStack();

    @Shadow
    protected abstract boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

    @WrapOperation(
            method = "onSlotClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"
            )
    )
    private void handle_shadowing(ScreenHandler instance, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, Operation<Void> original) {
        try {
            original.call(instance, slotIndex, button, actionType, player);
        } catch (Throwable error) {
            if (actionType != SlotActionType.SWAP && actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_CRAFT)
                throw error;

            ItemStack stack1 = this.getSlot(slotIndex).getStack();
            ItemStack stack2 = player.getInventory().getStack(button);
            ItemStack stack3 = this.getCursorStack();
            ItemStack shadow = null;

            if (stack1 == stack2 || stack1 == stack3)
                shadow = stack1;
            else if (stack2 == stack3)
                shadow = stack2;

            if (shadow != null) {
                String shadow_id = ShadowItem.fromItemStack(shadow).carpet_shadow$getShadowId();
                if (shadow_id == null)
                    shadow_id = CarpetShadow.shadow_id_generator.nextString();

                Globals.getByIdOrAdd(shadow_id, shadow);

                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK)
                    throw error;
            }
        }
    }
}


