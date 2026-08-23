package com.carpet_shadow.interfaces;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface ItemEntitySlot {

    static ItemEntitySlot fromItemStack(ItemStack stack) {
        return (ItemEntitySlot) (Object) stack;
    }

    ItemEntity carpet_shadow$getEntity();
    void carpet_shadow$setEntity(ItemEntity entity);
}
