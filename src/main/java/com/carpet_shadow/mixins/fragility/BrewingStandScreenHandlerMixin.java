package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BrewingStandScreenHandler.class)
public class BrewingStandScreenHandlerMixin {

    @WrapOperation(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/BrewingStandScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/screen/BrewingStandScreenHandler$FuelSlot;matches(Lnet/minecraft/item/ItemStack;)Z"
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/screen/slot/Slot;canInsert(Lnet/minecraft/item/ItemStack;)Z"
                    )
            )
    )
    public boolean fixShiftingIntoFuelSlot(BrewingStandScreenHandler instance, ItemStack stack, int startIndex, int endIndex, boolean fromLast, Operation<Boolean> original) {
        boolean changeReturnVal = false;
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && !stack.isEmpty() && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId() && startIndex == 4)
            changeReturnVal = true;

        boolean originalRet = original.call(instance, stack, startIndex, endIndex, fromLast);

        if (originalRet && changeReturnVal)
            return false;
        else
            return originalRet;
    }

}
