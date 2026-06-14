package com.hollingsworth.arsnouveau.client.renderer.layer;

import com.hollingsworth.arsnouveau.common.entity.MagicalBuddyMob;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class GeoShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {

    private final EntityRenderDispatcher entityRenderer;

    public GeoShoulderLayer(RenderLayerParent<T, PlayerModel<T>> pRenderer, EntityRendererProvider.Context pContext) {
        super(pRenderer);
        entityRenderer = pContext.getEntityRenderDispatcher();
    }

    @Override

    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pPartialTicks, true);
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pPartialTicks, false);
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Player pLivingEntity, float pPartialTick, boolean pLeftShoulder) {
        CompoundTag compoundtag = pLeftShoulder ? pLivingEntity.getShoulderEntityLeft() : pLivingEntity.getShoulderEntityRight();

        EntityType.byString(compoundtag.getString("id")).filter(entityType -> entityType.getBaseClass().isAssignableFrom(MagicalBuddyMob.class)).ifPresent(
                entityType -> {
                    var dummy = entityType.create(pLivingEntity.level);
                    if (dummy instanceof MagicalBuddyMob buddy) {
                        dummy.load(compoundtag);
                        pPoseStack.pushPose();
                        buddy.adjustShoulderPosition(pPoseStack, pLeftShoulder, pLivingEntity);
                        this.entityRenderer.render(dummy, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBuffer, pPackedLight);
                        pPoseStack.popPose();
                    }
                }
        );
    }

}
