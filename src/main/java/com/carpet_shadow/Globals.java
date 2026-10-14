package com.carpet_shadow;

import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Globals {

    public static final Set<Thread> mergingThreads = new HashSet<>();
    public static final Set<Inventory> toUpdate = new HashSet<>();

    public static boolean shadow_merge_check(ItemStack stack1, ItemStack stack2, boolean ret) {
        if (CarpetShadowSettings.shadowItemInventoryFragilityFix && mergingThreads.contains(Thread.currentThread()) && ret) {
            String shadow1 = ShadowItem.fromItemStack(stack1).carpet_shadow$getShadowId();
            String shadow2 = ShadowItem.fromItemStack(stack2).carpet_shadow$getShadowId();

            if (CarpetShadowSettings.shadowItemPreventCombine) {
                if (shadow1 != null && shadow2 != null)
                    return false;
            } else {
                if (shadow1 != null && shadow1.equals(shadow2))
                    return false;
            }
        }

        return ret;
    }
}
