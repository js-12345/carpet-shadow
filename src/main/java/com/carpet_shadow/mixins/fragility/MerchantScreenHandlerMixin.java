package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.MerchantInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreenHandler.class)
public class MerchantScreenHandlerMixin {

    @Shadow
    @Final
    private MerchantInventory merchantInventory;

    @WrapOperation(
            method = "autofill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    public boolean preventAutofillToUseShadowItems(ItemStack stack, ItemStack otherStack, Operation<Boolean> original) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(otherStack).carpet_shadow$hasShadowId())
            return false;

        return original.call(stack, otherStack);
    }

    @Inject(method = "switchTo", at = @At("HEAD"), cancellable = true)
    public void preventSwitchingIfShadowItemsInInv(int recipeIndex, CallbackInfo ci) {
        boolean slot1ShadowId = ShadowItem.fromItemStack(this.merchantInventory.getStack(0)).carpet_shadow$hasShadowId();
        boolean slot2ShadowId = ShadowItem.fromItemStack(this.merchantInventory.getStack(1)).carpet_shadow$hasShadowId();
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && (slot1ShadowId || slot2ShadowId))
            ci.cancel();
    }
}
