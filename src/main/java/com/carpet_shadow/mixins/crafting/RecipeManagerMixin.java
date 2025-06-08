package com.carpet_shadow.mixins.crafting;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    // TODO: a lot of duplication glitches with crafting table, e.g. 9x9, recipe book click

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0, shift = At.Shift.BEFORE))
    private void addShadowRecipe(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci,
                                 @Local(ordinal = 1) Map<RecipeType<?>, ImmutableMap.Builder<Identifier, RecipeEntry<?>>> map2, @Local ImmutableMap.Builder<Identifier, Recipe<?>> builder) {
        Identifier identifier = new Identifier("carpet_shadow", "shadow_recipe");
        Recipe<?> recipe = new BookCloningRecipe(CraftingRecipeCategory.MISC) {
            @Override
            public boolean matches(RecipeInputInventory inventory, World world) {
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return false;

                boolean hasEnderchest = false;
                int itemCount = 0;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack = inventory.getStack(i);
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem().equals(Items.ENDER_CHEST))
                            hasEnderchest = true;
                        itemCount++;
                    }
                }

                return hasEnderchest && itemCount == 2;
            }

            @Override
            public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return ItemStack.EMPTY;

                ItemStack itemToShadow = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack = inventory.getStack(i);
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem().equals(Items.ENDER_CHEST)) {
                            if (enderchest != null)
                                itemToShadow = enderchest;
                            enderchest = itemStack;
                        } else {
                            itemToShadow = itemStack;
                        }
                    }
                }

                if (itemToShadow == null || enderchest == null)
                    return ItemStack.EMPTY;

                String id = ((ShadowItem) (Object) itemToShadow).carpet_shadow$getShadowId();
                if (id == null) {
                    id = CarpetShadow.shadow_id_generator.nextString();
                }

                return Globals.getByIdOrAdd(id, itemToShadow);
            }

            @Override
            public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
                ItemStack itemToShadow = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack = inventory.getStack(i);
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem().equals(Items.ENDER_CHEST)) {
                            if (enderchest != null)
                                itemToShadow = enderchest;
                            enderchest = itemStack;
                        } else {
                            itemToShadow = itemStack;
                        }
                    }
                }

                if (itemToShadow != null && enderchest != null) {
                    itemToShadow.setCount(itemToShadow.getCount() + 1);
                    enderchest.setCount(enderchest.getCount() + 1);
                }

                return super.getRemainder(inventory);
            }


            @Override
            public boolean fits(int width, int height) {
                return width * height >= 2;
            }

            @Override
            public boolean showNotification() {
                return false;
            }
        };
        RecipeEntry<?> recipeEntry = new RecipeEntry<>(identifier, recipe);
        map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder()).put(identifier, recipeEntry);
        builder.put(identifier, recipe);
    }
}