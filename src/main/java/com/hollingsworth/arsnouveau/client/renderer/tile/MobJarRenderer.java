package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicReference;

public class MobJarRenderer implements BlockEntityRenderer<MobJarTile> {
    private final EntityRenderDispatcher entityRenderer;
    public MobJarRenderer(BlockEntityRendererProvider.Context pContext){
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(MobJarTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Entity entity = pBlockEntity.getEntity();
        if(entity == null)
            return;
        float f = 0.53125F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());

        if ((double)f1 > 1.0d) {
            f /= f1 * 1.0;
        }
        AtomicReference<Vec3> adjustedScale = new AtomicReference<>(new Vec3(0, 0, 0));
        AtomicReference<Vec3> adjustedTranslation = new AtomicReference<>(new Vec3(0, 0, 0));

        JarBehaviorRegistry.forEach(entity, jarBehavior ->{
            Vec3 customScale = jarBehavior.scaleOffset(pBlockEntity);
            adjustedScale.set(adjustedScale.get().add(customScale));
            adjustedTranslation.set(adjustedTranslation.get().add(jarBehavior.translate(pBlockEntity)));
        });

        Vec3 scale = new Vec3(f, f, f).multiply(adjustedScale.get().add(1,1,1));
        Vec3 translate = new Vec3(0.5, 0, 0.5).add(adjustedTranslation.get());
        pPoseStack.translate(translate.x, translate.y, translate.z);
        pPoseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);

        Direction direction = pBlockEntity.getBlockState().getValue(MobJar.FACING);
        if(direction == Direction.EAST) {
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        }else if(direction == Direction.WEST){
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
        }else if(direction == Direction.NORTH){
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        }else if(direction == Direction.SOUTH){
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        }
        pPoseStack.mulPose(pBlockEntity.getBlockState().getValue(MobJar.FACING).getRotation());
        entity.setDeltaMovement(0,0,0);
        this.entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBufferSource, pPackedLight);
        for(Entity entity1 : entity.getPassengers()){
            this.entityRenderer.render(entity1, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBufferSource, pPackedLight);
        }
    }
}
