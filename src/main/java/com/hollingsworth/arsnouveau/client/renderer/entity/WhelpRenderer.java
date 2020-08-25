package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class WhelpRenderer extends MobRenderer<EntityWhelp, WhelpModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/entity/kobold_texture.png");

    public WhelpRenderer(EntityRendererManager manager) {
        super(manager, new WhelpModel(), 0.2f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityWhelp entity) {
        return TEXTURE;
    }


}
