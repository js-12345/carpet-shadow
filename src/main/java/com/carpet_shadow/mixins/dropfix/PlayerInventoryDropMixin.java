package com.carpet_shadow.mixins.dropfix;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInventory.class)
public class PlayerInventoryDropMixin {

    @ModifyReturnValue(method = "canStackAddMore(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    public boolean canStackAddMore(boolean original, ItemStack existingStack, ItemStack stack) {
        if (CarpetShadowSettings.shadowItemDropFix && original && ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId())
            return false;

        return original;
    }

    @WrapMethod(method = "dropSelectedItem")
    public ItemStack fixDrop(boolean entireStack, Operation<ItemStack> original) {
        PlayerInventory iThis = (PlayerInventory) ((Object) this);

        if (CarpetShadowSettings.shadowItemDropFix) {
            ItemStack itemStack = iThis.getMainHandStack();

            if (itemStack != null && ShadowItem.fromItemStack(itemStack).carpet_shadow$hasShadowId() && (entireStack || itemStack.getCount() == 1)) {
                iThis.setStack(iThis.selectedSlot, ItemStack.EMPTY);
                return itemStack;
            }
        }

        return original.call(entireStack);
    }
}
