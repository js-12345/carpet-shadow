package com.carpet_shadow.interfaces;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.item.ItemStack;

public interface ShadowItem {

    String SHADOW_KEY = "shadow_id";

    static ShadowItem fromItemStack(ItemStack stack) {
        return (ShadowItem) (Object) stack;
    }

    static ItemStack carpet_shadow$copy_redirect(ItemStack instance, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance);

        ShadowItem sStack = ShadowItem.fromItemStack(stack);
        ShadowItem sInstance = ShadowItem.fromItemStack(instance);
        sStack.carpet_shadow$setShadowId(sInstance.carpet_shadow$getShadowId());

        return stack;
    }

    static ItemStack carpet_shadow$copy_supplier(ItemStack instance, ItemStack copy) {
        ShadowItem sCopy = ShadowItem.fromItemStack(copy);
        ShadowItem sInstance = ShadowItem.fromItemStack(instance);
        sCopy.carpet_shadow$setShadowId(sInstance.carpet_shadow$getShadowId());

        return copy;
    }

    boolean carpet_shadow$hasShadowId();
    String carpet_shadow$getShadowId();
    void carpet_shadow$setShadowId(String id);
}
