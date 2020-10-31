package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.ResourceLocation;

public class RenderFlyingItem extends EntityRenderer<EntityFlyingItem> {

    protected RenderFlyingItem(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityFlyingItem entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
//        if(entityIn.getStack() == null)
//            return;
////        entityIn.age++;
////
////        if(entityIn.age > 400)
////            entityIn.remove();
//

//        BlockPos start = entityIn.getDataManager().get(EntityFlyingItem.from);
//        BlockPos end = entityIn.getDataManager().get(EntityFlyingItem.to);
//        if(BlockUtil.distanceFrom(entityIn.getPosition(), end.up()) < 1 || entityIn.age > 1000 || BlockUtil.distanceFrom(entityIn.getPosition(), end) > 14){
//            entityIn.remove();
//            return;
//        }
//        System.out.println((ClientInfo.ticksInGame + partialTicks)%100);
//        double time = 1- entityIn.normalize((ClientInfo.ticksInGame + partialTicks)%120, 0.0, 120);
//        EasingType type = EasingType.NONE;
//        double startY = start.getY();
//        double endY = end.getY() +4.0;
//        Vec3d entityLerp = entityIn.getLerped();
//        double lerpX = lerp(time, (double)start.getX() +0.5, (double)end.getX()+0.5, type);
//        double lerpY = lerp(time, lerp(time, startY, endY, type), lerp(time,endY, startY, type), type);
//        double lerpZ = lerp(time,(double)start.getZ()+0.5, (double)end.getZ()+0.5, type);
//
//
//
//        entityIn.setPosition(lerpX, lerpY, lerpZ);
//
//
//        if (end.getX() != 0 || end.getY() != 0 || end.getZ() != 0){
//
//            Vec3d targetVector = new Vec3d(lerpX-posX,lerpY-posY,lerpZ-posZ);
//            double length = targetVector.length();
//            targetVector = targetVector.scale(0.3/length);
//            double weight  = 0;
//            if (length <= 3){
//                weight = 0.9*((3.0-length)/3.0);
//            }
//
//            motionX = (0.9-weight)*motionX+(0.1+weight)*targetVector.x;
//            motionY = (0.9-weight)*motionY+(0.1+weight)*targetVector.y;
//            motionZ = (0.9-weight)*motionZ+(0.1+weight)*targetVector.z;
//        }
//
//
////        posX += motionX/3;
////        posY += motionY/3;
////        posZ += motionZ/3;
//        BlockPos adjustedPos = new BlockPos(posX, end.getY(), posZ);
//
//        entityIn.setPosition(lerpX, lerpY, lerpZ);
//        if(BlockUtil.distanceFrom(adjustedPos, end) <= 0.5){
//            posY  = entityIn.getPosY() - 0.05;
//            motionY = -0.05;
//            entityIn.setPosition(lerpX, posY - 0.05, lerpZ);
//        }
////        this.setMotion(motionX, motionY, motionZ);
//
//        if(entityIn.world.isRemote && entityIn.age > 1) {
//            double deltaX = entityIn.getPosX() - entityIn.lastTickPosX;
//            double deltaY = entityIn.getPosY() - entityIn.lastTickPosY;
//            double deltaZ = entityIn.getPosZ() - entityIn.lastTickPosZ;
//            double dist = Math.ceil(Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ) * 20);
//            int counter = 0;
//
//            for (double i = 0; i < dist; i ++){
//                double coeff = i/dist;
//                counter += entityIn.world.rand.nextInt(3);
//                if (counter % (Minecraft.getInstance().gameSettings.particles.getId() == 0 ? 1 : 2 * Minecraft.getInstance().gameSettings.particles.getId()) == 0) {
//                    entityIn.world.addParticle(GlowParticleData.createData(
//                            new ParticleColor(entityIn.getDataManager().get(RED),entityIn.getDataManager().get(GREEN),entityIn.getDataManager().get(BLUE))),
//                            (float) (entityIn.prevPosX + deltaX * coeff), (float) (entityIn.prevPosY + deltaY * coeff), (float) (entityIn.prevPosZ + deltaZ * coeff), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f));
//                }
//            }
//
//        }
//        if(entityIn.getPosition().equals(entityIn.getDataManager().get(from)))
//            return;

        matrixStack.push();
        RenderSystem.enableLighting();
//        matrixStack.translate(lerpX - entityLerp.x,  lerpY - entityLerp.y,  lerpZ - entityLerp.z);
        matrixStack.scale(0.35f, 0.35f, 0.35F);

        Minecraft.getInstance().getItemRenderer().renderItem(entityIn.getStack(), ItemCameraTransforms.TransformType.FIXED, 15728880, packedLightIn, matrixStack, bufferIn);
        matrixStack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityFlyingItem entity) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png");
    }
}
