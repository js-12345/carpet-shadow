package com.carpet_shadow.mixins.tooltip;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.utility.ShadowIdTooltipGenerator;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
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
        if(sInstance.carpet_shadow$hasShadowId()) {
            if (ret == null)
                ret = new NbtCompound();

            ret.putString(ShadowItem.SHADOW_KEY, sInstance.carpet_shadow$getShadowId());

            if (CarpetShadowSettings.shadowItemTooltip) {
                if (!ret.contains(ItemStack.DISPLAY_KEY))
                    ret.put(ItemStack.DISPLAY_KEY, new NbtCompound());

                NbtCompound displayTag = ret.getCompound(ItemStack.DISPLAY_KEY);
                if (!displayTag.contains(ItemStack.LORE_KEY))
                    displayTag.put(ItemStack.LORE_KEY, new NbtList());

                NbtList loreTags = displayTag.getList(ItemStack.LORE_KEY, 8);

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

        String string;
        MutableText mutableText2;
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && (nbt.contains(ItemStack.DISPLAY_KEY) || nbt.contains(ShadowItem.SHADOW_KEY))) {
            String shadow_id = nbt.getString(ShadowItem.SHADOW_KEY);
            if (!shadow_id.isEmpty()) {
                sStack.carpet_shadow$setShadowId(shadow_id);
                nbt.remove(ShadowItem.SHADOW_KEY);
            }

            NbtCompound display = nbt.getCompound(ItemStack.DISPLAY_KEY);
            if (display.contains(ItemStack.LORE_KEY)) {
                NbtList lore = display.getList(ItemStack.LORE_KEY, 8);
                for (int i = 0; i < lore.size(); ++i) {
                    string = lore.getString(i);
                    try {
                        mutableText2 = Text.Serialization.fromJson(string);
                        if (mutableText2 != null && mutableText2.getContent() instanceof PlainTextContent.Literal && ((PlainTextContent.Literal) mutableText2.getContent()).string().equals("shadow_id: ")) {
                            lore.remove(i);
                            if (!sStack.carpet_shadow$hasShadowId())
                                sStack.carpet_shadow$setShadowId(((PlainTextContent.Literal) mutableText2.getSiblings().get(0).getContent()).string());
                            break;
                        }
                    } catch (JsonParseException ignored) {
                    }
                }

                if (lore.isEmpty())
                    display.remove(ItemStack.LORE_KEY);
                if (display.isEmpty())
                    nbt.remove(ItemStack.DISPLAY_KEY);
            }

            if (nbt.isEmpty())
                stack.setNbt(null);
        }
    }
}
