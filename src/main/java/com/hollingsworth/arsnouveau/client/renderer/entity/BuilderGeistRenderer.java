package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.BuilderGeist;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class BuilderGeistRenderer extends MobRenderer<BuilderGeist, BuilderGeistModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");

    public BuilderGeistRenderer(EntityRendererProvider.Context context) {
        super(context, new BuilderGeistModel(context.bakeLayer(ModelLayers.VEX)), 0.3F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected int getBlockLightLevel(BuilderGeist entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(BuilderGeist entity) {
        return TEXTURE;
    }
}
