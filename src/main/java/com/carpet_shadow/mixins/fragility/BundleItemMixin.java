package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(
            method = "onStackClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"
            ),
            cancellable = true
    )
    private static void disallowShadowItemsInBundle_onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(slot.getStack()).carpet_shadow$hasShadowId())
            cir.setReturnValue(true);
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private static void disallowShadowItemsInBundle_onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(otherStack).carpet_shadow$hasShadowId())
            cir.setReturnValue(true);
    }

    @Inject(
            method = "addToBundle",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void disallowShadowItemsInBundle_addToBundle(ItemStack bundle, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId())
            cir.setReturnValue(0);
    }
}
