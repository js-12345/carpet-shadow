package com.carpet_shadow;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class CarpetShadowMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Supplier<Boolean>> PREDICATES = ImmutableMap.of(
            "com.carpet_shadow.mixins.supression.ScreenHandlerAllowGeneration", () -> !FabricLoader.getInstance().isModLoaded("carpet-fixes")
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return PREDICATES.getOrDefault(mixinClassName, () -> true).get();
    }

    // default

    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
