package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ItemEntitySlot;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    public abstract void setStack(int slot, ItemStack stack);

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow
    public int selectedSlot;

    @Shadow
    public abstract int getEmptySlot();

    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(
            method = "offer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void fixDropOfHeldStackOnClosingInv(ItemStack stack, boolean notifiesClient, CallbackInfo ci) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && !stack.isEmpty() && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId()) {
            int slot = -1;
            if (this.getStack(selectedSlot).isEmpty())
                slot = selectedSlot;
            else
                slot = this.getEmptySlot();

            if (slot == -1)
                this.player.dropItem(stack, false);
            else
                this.setStack(slot, stack);

            ci.cancel();
        }
    }

    @WrapOperation(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;copyAndEmpty()Lnet/minecraft/item/ItemStack;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime(I)V"
                    )
            )
    )
    private ItemStack copy_damaged_item(ItemStack instance, Operation<ItemStack> original) {
        if ((CarpetShadowSettings.shadowItemInventoryFragilityFix || CarpetShadowSettings.shadowItemDropFix) && ShadowItem.fromItemStack(instance).carpet_shadow$hasShadowId()) {
            ItemEntity entity = ItemEntitySlot.fromItemStack(instance).carpet_shadow$getEntity();
            if (entity != null)
                entity.discard();
            return instance;
        }

        return original.call(instance);
    }

    @WrapOperation(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;setCount(I)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"
                    )
            )
    )
    private void modify_count(ItemStack instance, int count, Operation<Void> original) {
        if (count == 0 && (CarpetShadowSettings.shadowItemInventoryFragilityFix || CarpetShadowSettings.shadowItemDropFix) && ShadowItem.fromItemStack(instance).carpet_shadow$hasShadowId()) {
            ItemEntity entity = ItemEntitySlot.fromItemStack(instance).carpet_shadow$getEntity();
            if (entity != null)
                entity.discard();
            else
                instance.setCount(0);
        } else {
            original.call(instance, count);
        }
    }

    @Inject(
            method = "addStack(ILnet/minecraft/item/ItemStack;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
            ),
            cancellable = true
    )
    public void add_shadow_item(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if ((CarpetShadowSettings.shadowItemInventoryFragilityFix || CarpetShadowSettings.shadowItemDropFix) && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId()) {
            this.setStack(slot, stack);
            ItemEntity entity = ItemEntitySlot.fromItemStack(stack).carpet_shadow$getEntity();
            if (entity != null)
                entity.discard();
            cir.setReturnValue(-1);
        }
    }
}
