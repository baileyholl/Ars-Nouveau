package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityKobold;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class KoboldRenderer extends MobRenderer<EntityKobold, KoboldModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/kobold_texture.png");

    public KoboldRenderer(EntityRendererManager manager) {
        super(manager, new KoboldModel(), 0.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityKobold entity) {
        return TEXTURE;
    }
}
