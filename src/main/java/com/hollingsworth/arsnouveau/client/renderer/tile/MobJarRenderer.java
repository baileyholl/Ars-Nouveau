package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class MobJarRenderer implements BlockEntityRenderer<MobJarTile> {
    private final EntityRenderDispatcher entityRenderer;
    public MobJarRenderer(BlockEntityRendererProvider.Context pContext){
        entityRenderer = pContext.getEntityRenderer();
    }

    @Override
    public void render(MobJarTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Entity entity = pBlockEntity.displayEntity == null ? pBlockEntity.getEntity() : pBlockEntity.displayEntity;
        if(entity == null)
            return;
        if(pBlockEntity.displayEntity == null){
            pBlockEntity.displayEntity = entity;
        }
        entity.setBoundingBox(new AABB(BlockPos.ZERO));
        entity.setPos(pBlockEntity.getX(), pBlockEntity.getY(), pBlockEntity.getZ());
        float f = 0.53125F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());

        if ((double)f1 > 1.0d) {
            f /= f1 * 1.0;
        }
//        EntityDimensions ogDim = entity.getDimensions(entity.getPose());
//        EntityDimensions dimensions = new EntityDimensions(ogDim.width * f, ogDim.height * f, false);
//        entity.eyeHeight = dimensions.height * 0.8f;
        pPoseStack.translate(0.5D, 0.0F, 0.5D);
        pPoseStack.scale(f, f, f);
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

        this.entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBufferSource, pPackedLight);
        for(Entity entity1 : entity.getPassengers()){
            this.entityRenderer.render(entity1, 0.0D, 0.0D, 0.0D, 0.0F, 0, pPoseStack, pBufferSource, pPackedLight);
        }
    }
}
