package com.hollingsworth.arsnouveau.api.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface IVariantTextureProvider<T> {

    @Deprecated(forRemoval = true)
    default ResourceLocation getTexture(LivingEntity entity){
        return null;
    }

    default ResourceLocation getTexture(T entity){
        return getTexture((LivingEntity)entity);
    }

}
