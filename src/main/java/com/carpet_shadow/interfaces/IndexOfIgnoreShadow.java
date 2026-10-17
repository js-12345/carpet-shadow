package com.carpet_shadow.interfaces;

import net.minecraft.entity.player.PlayerInventory;

public interface IndexOfIgnoreShadow {

    static IndexOfIgnoreShadow fromPlayerInventory(PlayerInventory inventory) {
        return (IndexOfIgnoreShadow) (Object) inventory;
    }

    void carpet_shadow$ignoreShadowItems(boolean ignoreShadowItems);
}
