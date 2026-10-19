package com.carpet_shadow.mixins.tooltip;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.utility.ShadowIdTooltipGenerator;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void postToolTip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> list = cir.getReturnValue();

        ShadowItem sThis = (ShadowItem) this;
        if (CarpetShadowSettings.shadowItemTooltip && sThis.carpet_shadow$hasShadowId())
            list.add(ShadowIdTooltipGenerator.generateTooltip(sThis));
    }
}
