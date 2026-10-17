package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.interfaces.IndexOfIgnoreShadow;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.InputSlotFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InputSlotFiller.class)
public class InputSlotFillerMixin {

    @WrapOperation(
            method = "fillInputSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;indexOf(Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private int ignoreShadowItemsForCraftingResults(PlayerInventory instance, ItemStack stack, Operation<Integer> original) {
        IndexOfIgnoreShadow ii = IndexOfIgnoreShadow.fromPlayerInventory(instance);

        ii.carpet_shadow$ignoreShadowItems(true);
        int orig = original.call(instance, stack);
        ii.carpet_shadow$ignoreShadowItems(false);

        return orig;
    }
}
