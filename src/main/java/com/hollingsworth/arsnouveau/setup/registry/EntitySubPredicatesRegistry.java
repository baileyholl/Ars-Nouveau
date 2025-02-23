package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;

public class EntitySubPredicatesRegistry {
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, ArsNouveau.MODID);

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HealthEqualOrLowerPredicate>> HEALTH_EQUAL_OR_LOWER = ENTITY_SUB_PREDICATES.register(
            "health_equal_or_lower",
            () -> HealthEqualOrLowerPredicate.CODEC
    );


    public record HealthEqualOrLowerPredicate(float threshold) implements EntitySubPredicate {
        public static final MapCodec<HealthEqualOrLowerPredicate> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(Codec.FLOAT.fieldOf("threshold").forGetter((HealthEqualOrLowerPredicate i) -> i.threshold)).apply(instance, HealthEqualOrLowerPredicate::new)
        );

        @Override
        public MapCodec<HealthEqualOrLowerPredicate> codec() {
            return CODEC;
        }

        @Override
        public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
            return entity instanceof LivingEntity livingEntity && livingEntity.getHealth() <= threshold;
        }
    }
}
