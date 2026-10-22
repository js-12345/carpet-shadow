package com.carpet_shadow.utility;

import net.minecraft.inventory.Inventory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ShadowInvUpdates {

    // TODO: look at when this is maybe cleared, especially:
    // 1. leaking client/server objects to server/client (minor)
    // 2. expanding and not regularly clearing if on dedicated server
    private static final Set<Inventory> INV_UPDATES = new HashSet<>();

    public static synchronized void addInventories(Collection<Inventory> invs) {
        INV_UPDATES.addAll(invs);
    }

    public static synchronized void clear() {
        INV_UPDATES.clear();
    }

    public static synchronized Set<Inventory> getInventories() {
        return new HashSet<>(INV_UPDATES);
    }
}
