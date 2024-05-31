package com.hollingsworth.arsnouveau.setup.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.ForgeRegistries;

public class RegistryHelper {

    public static ResourceLocation getRegistryName(Item i) {
        return ForgeRegistries.ITEMS.getKey(i);
    }

    public static ResourceLocation getRegistryName(Block b) {
        return ForgeRegistries.BLOCKS.getKey(b);
    }

    public static ResourceLocation getRegistryName(EntityType<?> i) {
        return ForgeRegistries.ENTITY_TYPES.getKey(i);
    }

    public static ResourceLocation getRegistryName(Enchantment e) {
        return ForgeRegistries.ENCHANTMENTS.getKey(e);
    }

    public static ResourceLocation getRegistryName(ParticleType<?> type) {
        return ForgeRegistries.PARTICLE_TYPES.getKey(type);
    }

}
