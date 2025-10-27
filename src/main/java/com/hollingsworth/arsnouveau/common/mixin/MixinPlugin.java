package com.hollingsworth.arsnouveau.common.mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public boolean viveCraftLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
        try {

            Class.forName("org.vivecraft.tweaker.VivecraftTransformer");
            viveCraftLoaded = true;
        } catch (Throwable t) {
            // Vivecraft not loaded
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !viveCraftLoaded || !mixinClassName.equals("com.hollingsworth.arsnouveau.common.mixin.elytra.ClientElytraMixin");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}