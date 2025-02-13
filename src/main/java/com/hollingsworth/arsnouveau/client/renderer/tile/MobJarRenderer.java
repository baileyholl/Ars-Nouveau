package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.registry.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicReference;

public class MobJarRenderer implements BlockEntityRenderer<MobJarTile> {
    private final EntityRenderDispatcher entityRenderer;

    public MobJarRenderer(BlockEntityRendererProvider.Context pContext) {
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public boolean shouldRender(MobJarTile blockEntity, Vec3 cameraPos) {
        return blockEntity.isVisible && BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos);
    }

    @Override
    public void render(MobJarTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Entity entity = pBlockEntity.getEntity();
        if (entity == null)
            return;
        float f = 0.53125F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());

        if ((double) f1 > 1.0d) {
            f /= f1 * 1.0;
        }
        if (entity instanceof LightningBolt bolt) {
            if (bolt.level.getGameTime() % 20 == 0)
                bolt.seed = ClientInfo.ticksInGame;
            f = 0.0075f;
        }
        AtomicReference<Vec3> adjustedScale = new AtomicReference<>(new Vec3(0, 0, 0));
        AtomicReference<Vec3> adjustedTranslation = new AtomicReference<>(new Vec3(0, 0, 0));
        AtomicReference<Boolean> shouldParticlaTick = new AtomicReference<>(false);
        JarBehaviorRegistry.forEach(entity, jarBehavior -> {
            Vec3 customScale = jarBehavior.scaleOffset(pBlockEntity);
            adjustedScale.set(adjustedScale.get().add(customScale));
            adjustedTranslation.set(adjustedTranslation.get().add(jarBehavior.translate(pBlockEntity)));
            if (jarBehavior.shouldUsePartialTicks(pBlockEntity))
                shouldParticlaTick.set(true);
        });

        Vec3 scale = new Vec3(f, f, f).multiply(adjustedScale.get().add(1, 1, 1));
        Vec3 translate = new Vec3(0.5, 0, 0.5).add(adjustedTranslation.get());
        pPoseStack.translate(translate.x, translate.y, translate.z);
        pPoseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);

        Direction direction = pBlockEntity.getBlockState().getValue(MobJar.FACING);
        if (direction == Direction.EAST) {
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        } else if (direction == Direction.WEST) {
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        } else if (direction == Direction.NORTH) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        } else if (direction == Direction.SOUTH) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        }
        pPoseStack.mulPose(pBlockEntity.getBlockState().getValue(MobJar.FACING).getRotation());
        entity.setDeltaMovement(0, 0, 0);
        if (shouldParticlaTick.get()) {
            entity.xo = entity.getX();
            entity.yo = entity.getY();
            entity.zo = entity.getZ();
            entity.xRotO = entity.xRot;
            entity.yRotO = entity.yRot;
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.yBodyRotO = livingEntity.yBodyRot;
                livingEntity.yHeadRotO = livingEntity.yHeadRot;
                // TODO: readd old animation speed for allay?
            }
        } else {
            pPartialTick = 0;
        }

        this.entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
        for (Entity entity1 : entity.getPassengers()) {
            this.entityRenderer.render(entity1, 0.0D, 0.0D, 0.0D, 0.0F, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
        }
    }
}
