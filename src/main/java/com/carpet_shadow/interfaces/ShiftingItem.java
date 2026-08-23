package com.carpet_shadow.interfaces;

import net.minecraft.item.ItemStack;

public interface ShiftingItem {

    static ShiftingItem fromItemStack(ItemStack stack) {
        return (ShiftingItem) (Object) stack;
    }

    boolean carpet_shadow$isShiftMoving();
    void carpet_shadow$setShiftMoving(boolean val);
}
