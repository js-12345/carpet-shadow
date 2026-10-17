package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    // TODO: brittle random method name
    @WrapOperation(
            method = "method_42301",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/Recipe;matches(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Z"
            )
    )
    private static <C extends Inventory> boolean dissallowSameShadowItemMultipleTimes(Recipe<C> instance, C c, World world, Operation<Boolean> original) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix) {
            List<String> shadowIds = new ArrayList<>(c.size());
            for (int i = 0; i < c.size(); ++i) {
                ShadowItem shadowItem = ShadowItem.fromItemStack(c.getStack(i));
                if (shadowItem != null && shadowItem.carpet_shadow$hasShadowId()) {
                    if (shadowIds.contains(shadowItem.carpet_shadow$getShadowId()))
                        return false;
                    else
                        shadowIds.add(shadowItem.carpet_shadow$getShadowId());
                }
            }
        }

        return original.call(instance, c, world);
    }
}
