package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @WrapOperation(
            method = "setStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    public boolean preventResetOfCookTimeShadowItem(ItemStack stack, ItemStack otherStack, Operation<Boolean> original) {
        boolean oneIsShadow = ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId() || ShadowItem.fromItemStack(otherStack).carpet_shadow$hasShadowId();
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && oneIsShadow)
            return stack == otherStack;

        return original.call(stack, otherStack);
    }
}
