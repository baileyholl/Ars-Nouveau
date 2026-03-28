package com.hollingsworth.arsnouveau.client.renderer.entity.familiar;

import com.hollingsworth.arsnouveau.client.renderer.entity.ArsEntityRenderState;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// GeckoLib 5.4.2 migration:
// - GeoEntityRenderer now requires 2 type params <T, R extends EntityRenderState & GeoRenderState>
// - getTextureLocation(T) REMOVED - texture comes from model's getTextureResource(GeoRenderState)
// - Texture lookup based on entity.getTexture() requires storing it via DataTicket in captureDefaultRenderState
// TODO: Port custom texture (ICosmeticItem) to DataTicket pattern in captureDefaultRenderState
public class GenericFamiliarRenderer<T extends FamiliarEntity> extends GeoEntityRenderer<T, ArsEntityRenderState> {

    public GenericFamiliarRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    public static MultiBufferSource.BufferSource cosmeticBuffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

    @Override
    public @NotNull ArsEntityRenderState createRenderState(@NotNull T entity, Void unused) {
        return new ArsEntityRenderState();
    }
}
