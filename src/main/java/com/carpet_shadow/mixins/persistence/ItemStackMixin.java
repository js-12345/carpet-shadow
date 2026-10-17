package com.carpet_shadow.mixins.persistence;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Inject(method = "setCount", at = @At("HEAD"))
    private void deleteOnCountLT0(int count, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (ShadowItem.fromItemStack(stack).carpet_shadow$hasShadowId() && count <= 0)
            CarpetShadow.SHADOW_MAP.deleteShadowItem(stack);
    }

    @ModifyReturnValue(method = "fromNbt", at = @At("RETURN"))
    private static ItemStack post_fromNbt(ItemStack stack, NbtCompound nbt) {
        if (nbt.contains("shadow") || nbt.contains(ShadowItem.SHADOW_KEY)) {
            String shadow_id = nbt.getString(nbt.contains("shadow") ? "shadow" : ShadowItem.SHADOW_KEY);

            // TODO: is this safe or only scope to server???
            if (CarpetShadow.SHADOW_MAP.containsShadowItem(shadow_id))
                stack = CarpetShadow.SHADOW_MAP.getShadowItem(shadow_id);
            else if (CarpetShadowSettings.shadowTrackUntrackedItems) {
                // TODO: remove in later versions
                ShadowItem.fromItemStack(stack).carpet_shadow$setShadowId(shadow_id);
                CarpetShadow.SHADOW_MAP.addShadowItem(stack);
            }

            CarpetShadow.LOGGER.info(Thread.currentThread());
        }

        return stack;
    }

    @ModifyReturnValue(method = "writeNbt", at = @At("RETURN"))
    private NbtCompound post_writeNbt(NbtCompound ret, NbtCompound orig) {
        ShadowItem sThis = (ShadowItem) this;
        if (sThis.carpet_shadow$hasShadowId())
            ret.putString(ShadowItem.SHADOW_KEY, sThis.carpet_shadow$getShadowId());
        return ret;
    }
}
