package com.hollingsworth.arsnouveau.setup.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RegistryHelper {

    public static Identifier getRegistryName(Item i) {
        return BuiltInRegistries.ITEM.getKey(i);
    }

    public static Identifier getRegistryName(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b);
    }

    public static Identifier getRegistryName(EntityType<?> i) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(i);
    }

    public static Identifier getRegistryName(ParticleType<?> type) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
    }
}
