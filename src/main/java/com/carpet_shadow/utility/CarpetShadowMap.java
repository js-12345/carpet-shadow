package com.carpet_shadow.utility;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CarpetShadowMap extends PersistentState {

    private static final String SHADOW_ITEMS_NBT_KEY = "shadowItems";
    private static final PersistentState.Type<CarpetShadowMap> CARPET_SHADOW_MAP_TYPE = new Type<>(
            CarpetShadowMap::new,
            CarpetShadowMap::fromNbt,
            null
    );

    private static final String ALPHA_UP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA_DOWN = ALPHA_UP.toLowerCase();
    private static final String ALPHA = ALPHA_UP + ALPHA_DOWN;
    private static final String DIGITS = "0123456789";
    private static final String ALPHA_NUM = ALPHA + DIGITS;

    private final Map<String, ItemStack> shadowItemsMap;

    public static CarpetShadowMap loadShadowMapFromServer(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(CARPET_SHADOW_MAP_TYPE, CarpetShadow.MOD_ID);
    }

    public CarpetShadowMap() {
        this(new HashMap<>());
    }

    public CarpetShadowMap(Map<String, ItemStack> shadowItemsMap) {
        this.shadowItemsMap = shadowItemsMap;
    }

    public synchronized boolean containsShadowItem(ItemStack stack) {
        return containsShadowItem(ShadowItem.fromItemStack(stack).carpet_shadow$getShadowId());
    }

    public synchronized boolean containsShadowItem(String shadowId) {
        if (shadowId == null)
            return false;
        return shadowItemsMap.containsKey(shadowId);
    }

    public synchronized void addShadowItem(ItemStack stack) {
        ShadowItem sStack = ShadowItem.fromItemStack(stack);
        if (!sStack.carpet_shadow$hasShadowId()) {
            String id = this.generateUniqueId();
            sStack.carpet_shadow$setShadowId(id);
            shadowItemsMap.put(id, stack);
        } else if (CarpetShadowSettings.shadowTrackUntrackedItems) {
            // TODO: remove in later versions
            String id = sStack.carpet_shadow$getShadowId();
            if (!this.containsShadowItem(id)) {
                shadowItemsMap.put(id, stack);
            } else {
                CarpetShadow.LOGGER.warn("Tried to add shadow item '{}' with id '{}', removing shadow id of this item", stack, id);
                sStack.carpet_shadow$removeShadowId();
            }
        }
        this.markDirty();
    }

    public synchronized ItemStack getShadowItem(String shadowId) {
        return shadowItemsMap.getOrDefault(shadowId, null);
    }

    public synchronized ItemStack getOrAddShadowItem(ItemStack stack) {
        if (!this.containsShadowItem(stack))
            this.addShadowItem(stack);
        return this.getShadowItem(ShadowItem.fromItemStack(stack).carpet_shadow$getShadowId());
    }

    public synchronized void deleteShadowItem(ItemStack stack) {
        if (this.containsShadowItem(stack)) {
            String shadowId = ShadowItem.fromItemStack(stack).carpet_shadow$getShadowId();
            ItemStack savedStack = getShadowItem(shadowId);
            if (savedStack != stack)
                return;

            shadowItemsMap.remove(shadowId);
            CarpetShadow.LOGGER.info("Deleting shadow item: " + shadowId);

            if (stack.getCount() > 0)
                stack.setCount(0);
        }
        this.markDirty();
    }

    private String generateUniqueId() {
        String id = generateId();
        while (this.containsShadowItem(id))
            id = generateId();
        return id;
    }

    private static String generateId() {
        StringBuilder idBuilder = new StringBuilder();
        Random rand = new Random(); // TODO: add caching???

        for (int i = 0; i < CarpetShadowSettings.shadowItemIdSize; i++)
            idBuilder.append(ALPHA_NUM.charAt(rand.nextInt(ALPHA_NUM.length())));

        return idBuilder.toString();
    }

    public synchronized int getSize() {
        return shadowItemsMap.size();
    }

    public synchronized Map<String, ItemStack> getShadowItems() {
        return new HashMap<>(this.shadowItemsMap);
    }

    public static CarpetShadowMap fromNbt(NbtCompound nbt) {
        Map<String, ItemStack> shadowItems = new HashMap<>();

        NbtCompound shadowItemsTag = nbt.getCompound(SHADOW_ITEMS_NBT_KEY);
        for (String shadowId : shadowItemsTag.getKeys()) {
            ItemStack stack = ItemStack.fromNbt(shadowItemsTag.getCompound(shadowId));
            ShadowItem.fromItemStack(stack).carpet_shadow$setShadowId(shadowId);
            shadowItems.put(shadowId, stack);
        }

        return new CarpetShadowMap(shadowItems);
    }

    @Override
    public synchronized NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound shadowItemsTag = new NbtCompound();

        for (Map.Entry<String, ItemStack> shadowItemsEntry : shadowItemsMap.entrySet()) {
            ItemStack stack = shadowItemsEntry.getValue().copy();
            ShadowItem.fromItemStack(stack).carpet_shadow$removeShadowId();
            shadowItemsTag.put(shadowItemsEntry.getKey(), stack.writeNbt(new NbtCompound()));
        }

        nbt.put(SHADOW_ITEMS_NBT_KEY, shadowItemsTag);
        return nbt;
    }
}
