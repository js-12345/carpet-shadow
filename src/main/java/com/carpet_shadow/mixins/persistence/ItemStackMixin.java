package com.carpet_shadow.mixins.persistence;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isEmpty();

    @ModifyReturnValue(method = "fromNbt", at = @At("RETURN"))
    private static ItemStack post_fromNbt(ItemStack stack, NbtCompound nbt) {
        if (nbt.contains("shadow")) {
            if (CarpetShadowSettings.shadowItemMode.shouldResetCount()) {
                stack.setCount(0);
            } else if (CarpetShadowSettings.shadowItemMode.shouldLoadItem()) {
                String shadow_id = nbt.getString("shadow");
                stack = Globals.getByIdOrAdd(shadow_id, stack);
            }
        }

        return stack;
    }

    @ModifyReturnValue(method = "writeNbt", at = @At("RETURN"))
    private NbtCompound post_writeNbt(NbtCompound ret, NbtCompound orig) {
        ShadowItem sThis = (ShadowItem) this;
        if (sThis.carpet_shadow$hasShadowId()) {
            if (this.isEmpty()) {
                CarpetShadow.shadowMap.invalidate(sThis.carpet_shadow$getShadowId());
                sThis.carpet_shadow$setShadowId(null);
            } else {
                ret.putString("shadow", sThis.carpet_shadow$getShadowId());
            }
        }
        return ret;
    }
}
