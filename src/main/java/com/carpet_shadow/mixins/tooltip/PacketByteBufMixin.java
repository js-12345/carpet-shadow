package com.carpet_shadow.mixins.tooltip;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.utility.ShadowIdTooltipGenerator;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin {

    @WrapOperation(
            method = "writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;"
            )
    )
    public NbtCompound add_shadow_lore(ItemStack instance, Operation<NbtCompound> original) {
        NbtCompound ret = original.call(instance);

        ShadowItem sInstance = ShadowItem.fromItemStack(instance);
        if (sInstance.carpet_shadow$hasShadowId()) {
            if (ret == null)
                ret = new NbtCompound();

            ret.putString(ShadowItem.SHADOW_KEY, sInstance.carpet_shadow$getShadowId());

            if (CarpetShadowSettings.shadowItemTooltip) {
                if (!ret.contains(ItemStack.DISPLAY_KEY))
                    ret.put(ItemStack.DISPLAY_KEY, new NbtCompound());

                NbtCompound displayTag = ret.getCompound(ItemStack.DISPLAY_KEY);
                if (!displayTag.contains(ItemStack.LORE_KEY))
                    displayTag.put(ItemStack.LORE_KEY, new NbtList());

                NbtList loreTags = displayTag.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

                MutableText text = ShadowIdTooltipGenerator.generateTooltip(sInstance).formatted(Formatting.ITALIC);
                loreTags.add(NbtString.of(Text.Serialization.toJsonString(text)));

                displayTag.put(ItemStack.LORE_KEY, loreTags);
                ret.put(ItemStack.DISPLAY_KEY, displayTag);
            }
        }

        return ret;
    }

    @Inject(method = "readItemStack", at = @At(value = "RETURN"))
    public void filter_shadow_lore(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        ShadowItem sStack = ShadowItem.fromItemStack(stack);

        NbtCompound nbt = stack.getNbt();
        if (nbt == null)
            return;

        // extract shadow id
        if (nbt.contains(ShadowItem.SHADOW_KEY)) {
            sStack.carpet_shadow$setShadowId(nbt.getString(ShadowItem.SHADOW_KEY));
            nbt.remove(ShadowItem.SHADOW_KEY);
        }

        // remove lore tags
        if (nbt.contains(ItemStack.DISPLAY_KEY)) {
            NbtCompound displayTag = nbt.getCompound(ItemStack.DISPLAY_KEY);
            if (displayTag.contains(ItemStack.LORE_KEY)) {
                NbtList loreTags = displayTag.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

                for (int i = 0; i < loreTags.size(); i++) {
                    try {
                        MutableText singleLoreText = Text.Serialization.fromJson(loreTags.getString(i));
                        if (ShadowIdTooltipGenerator.isShadowIdTooltip(singleLoreText)) {
                            loreTags.remove(i);
                            i--;
                        }
                    } catch (JsonParseException ignored) {
                    }
                }

                if (loreTags.isEmpty())
                    displayTag.remove(ItemStack.LORE_KEY);
            }

            if (displayTag.isEmpty())
                nbt.remove(ItemStack.DISPLAY_KEY);
        }

        // if nbt now empty, remove
        if (nbt.isEmpty())
            stack.setNbt(null);
    }
}
