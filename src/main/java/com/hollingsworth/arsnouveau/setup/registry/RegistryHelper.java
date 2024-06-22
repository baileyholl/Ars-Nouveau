package com.hollingsworth.arsnouveau.setup.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class RegistryHelper {

    public static ResourceLocation getRegistryName(Item i) {
        return BuiltInRegistries.ITEM.getKey(i);
    }

    public static ResourceLocation getRegistryName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b);
    }

    public static ResourceLocation getRegistryName(EntityType<?> i) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(i);
    }

    public static ResourceLocation getRegistryName(Enchantment e) {
        return BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getKey(e);
    }

    public static ResourceLocation getRegistryName(ParticleType<?> type) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
    }
}
