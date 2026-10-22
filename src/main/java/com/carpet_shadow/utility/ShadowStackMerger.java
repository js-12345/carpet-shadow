package com.carpet_shadow.utility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ShadowStackMerger {

    private static final Set<Thread> mergingThreads = new HashSet<>();

    public synchronized static void addCurrentThread() {
        mergingThreads.add(Thread.currentThread());
    }

    public synchronized static void removeCurrentThread() {
        mergingThreads.remove(Thread.currentThread());
    }

    public synchronized static void clear() {
        mergingThreads.clear();
    }

    public synchronized static boolean check(ItemStack stack1, ItemStack stack2, boolean ret) {
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
