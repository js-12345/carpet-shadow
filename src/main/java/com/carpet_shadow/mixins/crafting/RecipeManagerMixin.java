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

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/recipe/RecipeManager;recipes:Ljava/util/Map;"))
         //   at = @At(value = "FIELD", target = "Lnet/minecraft/recipe/RecipeManager;builder:Lcom/google/common/collect/ImmutableMap$Builder;"))
    //at = @At(value = "HEAD", shift = At.Shift.BY, by = 3))
    private void addShadowRecipe(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, @Local(ordinal = 1) Map<RecipeType<?>, ImmutableMap.Builder<Identifier, RecipeEntry<?>>> map2, @Local ImmutableMap.Builder<Identifier, RecipeEntry<?>> builder) {
        Identifier identifier = new Identifier("carpet_shadow", "shadow_recipe");
        Recipe<?> recipe = new BookCloningRecipe(CraftingRecipeCategory.MISC) {
            @Override
            public boolean matches(RecipeInputInventory inventory, World world) {
                System.out.println("Matches");
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return false;
                boolean enderchest = false;
                int count = 0;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack2 = inventory.getStack(i);
                    if (!itemStack2.isEmpty()) {
                        if (itemStack2.getItem().equals(Items.ENDER_CHEST))
                            enderchest = true;
                        count++;
                    }
                }
                return enderchest && count == 2;
            }

            @Override
            public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
                System.out.println("craft");
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return ItemStack.EMPTY;
                ItemStack item = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack2 = inventory.getStack(i);
                    if (!itemStack2.isEmpty()) {
                        if (itemStack2.getItem().equals(Items.ENDER_CHEST)) {
                            if (enderchest != null)
                                item = enderchest;
                            enderchest = itemStack2;
                        } else
                            item = itemStack2;
                    }
                }
                if (item == null || enderchest == null)
                    return ItemStack.EMPTY;
                String id = ((ShadowItem) (Object) item).carpet_shadow$getShadowId();
                if (id == null) {
                    id = CarpetShadow.shadow_id_generator.nextString();
                }
                return Globals.getByIdOrAdd(id, item);
            }

            @Override
            public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
                System.out.println("getRemainder");
                ItemStack item = null;
                ItemStack enderchest = null;
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack2 = inventory.getStack(i);
                    if (!itemStack2.isEmpty()) {
                        if (itemStack2.getItem().equals(Items.ENDER_CHEST)) {
                            if (enderchest != null)
                                item = enderchest;
                            enderchest = itemStack2;
                        } else
                            item = itemStack2;
                    }
                }
                if (item != null && enderchest != null)
                    item.setCount(item.getCount() + 1);
                return super.getRemainder(inventory);
            }

            @Override
            public boolean fits(int width, int height) {
                System.out.println("fits");
                if (CarpetShadowSettings.shadowItemMode == CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return false;
                return width * height >= 2;
            }
        };
        System.out.println(map2.size());
        ((ImmutableMap.Builder) map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder())).put(identifier, recipe);
        builder.put(identifier, new RecipeEntry<>(identifier, recipe));
        System.out.println(map2.size());
    }
}