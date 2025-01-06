package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DamageUtil {

    //Implementation can be swapped to use AT methods of DamageSource, but use of this class will allow access to addons without the requirement of ATs

    static public DamageSource source(LevelAccessor level, ResourceKey<DamageType> key) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key));
    }

    static public DamageSource source(LevelAccessor level, ResourceKey<DamageType> key, @Nullable Entity entity) {
        return source(level, key, entity, null);
    }

    static public DamageSource source(LevelAccessor level, ResourceKey<DamageType> key, @Nullable Entity entity, @Nullable Entity direct) {
        Holder.Reference<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        if (entity != null && direct != null)
            return new SpellDamageSource(type, entity, direct);
        else if (entity != null)
            return new SpellDamageSource(type, entity);
        else
            return new SpellDamageSource(type);
    }


    /**
     * Custom DamageSource that holds the luck augments of the spell damage to apply during drop generation
     */
    public static class SpellDamageSource extends DamageSource {

        public int getLuckLevel() {
            return luckLevel;
        }

        public void setLuckLevel(int luckLevel) {
            this.luckLevel = luckLevel;
        }

        public int luckLevel = 0;

        public SpellDamageSource(Holder<DamageType> pType, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity, @Nullable Vec3 pDamageSourcePosition) {
            super(pType, pDirectEntity, pCausingEntity, pDamageSourcePosition);
        }

        public SpellDamageSource(Holder<DamageType> pType, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity) {
            this(pType, pDirectEntity, pCausingEntity, null);
        }

        public SpellDamageSource(Holder<DamageType> pType, Vec3 pDamageSourcePosition) {
            this(pType, null, null, pDamageSourcePosition);
        }

        public SpellDamageSource(Holder<DamageType> pType, @Nullable Entity pEntity) {
            this(pType, pEntity, pEntity);
        }

        public SpellDamageSource(Holder<DamageType> pType) {
            this(pType, null, null, null);
        }
    }

}
