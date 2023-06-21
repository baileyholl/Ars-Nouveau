package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

public class TextureVariantRenderer<T extends LivingEntity & IVariantTextureProvider & GeoEntity> extends GenericRenderer<T> {

    public TextureVariantRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture((Object) entity);
    }

}
