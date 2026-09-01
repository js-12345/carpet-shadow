package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CrafterInputSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrafterInputSlot.class)
public class CrafterInputSlotMixin {

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    public void disallowShadowedItemsInCrafter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId())
            cir.setReturnValue(false);
    }
}
