package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This is first slot class with in place overridden canInsert
@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$2")
public class GrindstoneScreenHandler$2Mixin {

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    public void disallowShadowItemsInInputSlots(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId())
            cir.setReturnValue(false);
    }
}
