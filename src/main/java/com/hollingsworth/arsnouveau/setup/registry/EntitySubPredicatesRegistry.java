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

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<PercentHealthEqualOrLowerPredicate>> PERCENT_HEALTH_EQUAL_OR_LOWER = ENTITY_SUB_PREDICATES.register(
            "percent_health_equal_or_lower",
            () -> PercentHealthEqualOrLowerPredicate.CODEC
    );


    public record PercentHealthEqualOrLowerPredicate(float threshold) implements EntitySubPredicate {
        public static final MapCodec<PercentHealthEqualOrLowerPredicate> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(Codec.FLOAT.fieldOf("threshold").forGetter((PercentHealthEqualOrLowerPredicate i) -> i.threshold)).apply(instance, PercentHealthEqualOrLowerPredicate::new)
        );

        @Override
        public MapCodec<PercentHealthEqualOrLowerPredicate> codec() {
            return CODEC;
        }

        @Override
        public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
            return entity instanceof LivingEntity livingEntity && livingEntity.getHealth() / livingEntity.getMaxHealth() <= threshold;
        }
    }
}
