package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ItemEntitySlot;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    @Nullable
    private UUID owner;

    @Shadow
    private static void merge(ItemEntity targetEntity, ItemStack targetStack, ItemEntity sourceEntity, ItemStack sourceStack) {}

    @WrapMethod(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V")
    public void fix_tryMerge(ItemEntity other, Operation<Void> original) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix || CarpetShadowSettings.shadowItemDropFix) {
            ItemEntity iThis = (ItemEntity) ((Object) this);

            ItemStack itemStack = iThis.getStack();
            ItemStack itemStack2 = other.getStack();

            if (Objects.equals(owner, owner) && ItemEntity.canMerge(itemStack, itemStack2)) {
                String shadId1 = ((ShadowItem) (Object) itemStack).carpet_shadow$getShadowId();
                String shadId2 = ((ShadowItem) (Object) itemStack2).carpet_shadow$getShadowId();

                if (shadId1 != null) {
                    if (shadId2 == null) {
                        merge(iThis, itemStack, other, itemStack2);
                        return;
                    }
                } else if (shadId2 != null) {
                    merge(other, itemStack2, iThis, itemStack);
                    return;
                }
            }
        }

        original.call(other);
    }

    @WrapOperation(method = "merge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copyWithCount(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack redirect_copy(ItemStack stack, int count, Operation<ItemStack> original) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ((ShadowItem) (Object) stack).carpet_shadow$getShadowId() != null) {
            stack.increment(count - stack.getCount());
            return stack;
        }
        return original.call(stack, count);
    }

    @ModifyReturnValue(method = "canMerge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private static boolean canMerge(boolean original, ItemStack stack1, ItemStack stack2) {
        Globals.mergingThreads.add(Thread.currentThread());
        boolean ret = Globals.shadow_merge_check(stack1, stack2, original);
        Globals.mergingThreads.remove(Thread.currentThread());
        return ret;
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BY, by = 2))
    public void setEntityForStack(PlayerEntity player, CallbackInfo ci, @Local(ordinal = 0) ItemStack stack) {
        ((ItemEntitySlot) (Object) stack).carpet_shadow$setEntity((ItemEntity)(Object)this);
    }

    @Inject(method = "onPlayerCollision", at = @At(value = "RETURN"))
    public void resetEntityForStack(PlayerEntity player, CallbackInfo ci, @Local(ordinal = 0) ItemStack stack) {
        ((ItemEntitySlot) (Object) stack).carpet_shadow$setEntity(null);
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void merging_start(PlayerEntity player, CallbackInfo ci){
        Globals.mergingThreads.add(Thread.currentThread());
    }
    @Inject(method = "onPlayerCollision", at = @At("RETURN"))
    private void merging_end(PlayerEntity player, CallbackInfo ci){
        Globals.mergingThreads.remove(Thread.currentThread());
    }

}
