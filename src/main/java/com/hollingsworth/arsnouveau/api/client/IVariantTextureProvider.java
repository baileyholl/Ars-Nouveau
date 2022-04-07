package com.hollingsworth.arsnouveau.api.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface IVariantTextureProvider {

    ResourceLocation getTexture(LivingEntity entity);

}
