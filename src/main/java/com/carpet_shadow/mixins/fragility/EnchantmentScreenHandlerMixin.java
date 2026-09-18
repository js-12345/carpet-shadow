package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    // TODO: also prevent direct click??? Currently doesnt create bugs

    @WrapOperation(
            method = "quickMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;canInsert(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    public boolean disallowShadowItemsInEnchantingTable(Slot instance, ItemStack stack, Operation<Boolean> original) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId())
            return false;

        return original.call(instance, stack);
    }
}
