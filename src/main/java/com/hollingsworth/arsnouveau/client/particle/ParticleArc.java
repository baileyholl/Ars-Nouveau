package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleArc  extends SpriteTexturedParticle {
    public static final String NAME = "arc";
//    @ObjectHolder(ArsNouveau.MODID + ":" + ParticleArc.NAME) public static ParticleType<ArcParticleTypeData> TYPE;

    float forwardFactor;

    Entity targetEntity;
    Entity sourceEntity;
    boolean ignoreAge = true;
    double deviation;
    float speed;
    float width;
    boolean extendToTarget;
    float extensionProgress;

    Vec3d targetPoint;
    Vec3d currentTargetPoint;
    Vec3d sourcePoint;
    boolean hadTarget = false;
    boolean hadSource = false;
    private ParticleArc(World worldIn, Vec3d coord, IAnimatedSprite sprite, Vec3d sourcePoint, Vec3d targetPoint) {
        super(worldIn, coord.getX(), coord.getY(), coord.getZ());
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
        currentTargetPoint = copyVec(targetPoint);
        deviation = 1.0;
        speed = 0.01f;
        width = 0.05f;
        this.maxAge = 100;
        forwardFactor = 0;
        this.selectSpriteRandomly(sprite);
    }
    protected ParticleArc(World p_i50998_1_, double p_i50998_2_, double p_i50998_4_, double p_i50998_6_) {
        super(p_i50998_1_, p_i50998_2_, p_i50998_4_, p_i50998_6_);
    }

    public ParticleArc(World world, double x, double y, double z, double targetX, double targetY, double targetZ, String IIconName){
        super(world, x, y, z);
        targetPoint = new Vec3d(targetX, targetY, targetZ);
        currentTargetPoint = copyVec(targetPoint);
        sourcePoint = new Vec3d(x, y, z);
        deviation = 1.0;
        speed = 0.01f;
        width = 0.05f;

        this.maxAge = 100;
        forwardFactor = 0;
    }

    @Override
    public void renderParticle(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float partialTicks) {
        super.renderParticle(p_225606_1_, p_225606_2_, partialTicks);
        try {
            drawArcingLine(prevPosX + (posX - prevPosX) * partialTicks, prevPosY + (posY - prevPosY) * partialTicks, prevPosZ + (posZ - prevPosZ) * partialTicks, currentTargetPoint.x, currentTargetPoint.y, currentTargetPoint.z, partialTicks, speed, deviation);
        }catch (Throwable e){
            System.out.println(e);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (this.age >= this.maxAge){
            this.setExpired();
            return;
        }

        if (targetEntity != null){
            if (ignoreAge)
                this.age = 0;
            if (!targetEntity.isAlive()){
                this.setExpired();
                return;
            }
            targetPoint = new Vec3d(targetEntity.getPosX(), targetEntity.getPosY() + targetEntity.getEyeHeight() - (targetEntity.getHeight() * 0.2f), targetEntity.getPosZ());
            currentTargetPoint = new Vec3d(targetPoint.x, targetPoint.y, targetPoint.z);
        }else if (hadTarget){
            this.setExpired();
            return;
        }

        if (sourceEntity != null){
            if (ignoreAge)
                this.age = 0;
            if (!sourceEntity.isAlive()){
                this.setExpired();
                return;
            }
            sourcePoint = new Vec3d(sourceEntity.getPosX(), sourceEntity.getPosY() + sourceEntity.getEyeHeight() - (sourceEntity.getHeight() * 0.2f), sourceEntity.getPosZ());
        }else if (hadSource){
            this.setExpired();
            return;
        }


        if (extendToTarget && extensionProgress < 1.0f){
            extensionProgress += 0.08;

            Vec3d delta = copyVec(targetPoint).subtract(sourcePoint);
            delta.scale(extensionProgress);
            currentTargetPoint = delta.add(sourcePoint);
        }
    }
    // Why is this required, what the fuck.
    public Vec3d copyVec(Vec3d vec3d){
        return new Vec3d(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return RenderTypes.AN_RENDER;
    }


    public void drawArcingLine( double srcX, double srcY, double srcZ, double dstX, double dstY, double dstZ, float partialTicks, float speed, double distance){

//        GL11.glPushMatrix();

 //       Minecraft.getInstance().getRenderManager().textureManager.bindTexture(new ResourceLocation(ArsNouveau.MODID, "arc"));

        int fxQuality = 10;

        PlayerEntity player = Minecraft.getInstance().player;
        double interpolatedX = player.prevPosX + (player.getPosX() - player.prevPosX) * partialTicks;
        double interpolatedY = player.prevPosY + (player.getPosY() - player.prevPosY) * partialTicks;
        double interpolatedZ = player.prevPosZ + (player.getPosZ() - player.prevPosZ) * partialTicks;

        Tessellator tessellator = Tessellator.getInstance();
//        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        double deltaX = srcX - dstX;
        double deltaY = srcY - dstY;
        double deltaZ = srcZ - dstZ;

        float time = System.nanoTime() / 10000000L;

        float dist = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        float blocks = Math.round(dist);
        float length = blocks * (fxQuality / 2.0F);

        float vMin = 0.0F;
        float VMax = 1.0F;

//        GL11.glTranslated(-interpolatedX + dstX, -interpolatedY + dstY, -interpolatedZ + dstZ);
//
//        tessellator.draw();

        double wGain = (width * 3) / (length * distance);
        float curWidth = width * 3;
//        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (int i = 0; i <= length * distance; i++){
            float lengthFactor = i / length;
            float f3 = 1.0F - Math.abs(i - length / 2.0F) / (length / 2.0F);

            //ZXY
            float dx = (float) (deltaX + MathHelper.sin((float)((srcX % 16.0D + dist * (1.0F - lengthFactor) * fxQuality / 2.0F - time % 32767.0F / 5.0F) / 4.0D)) * 0.5F * f3);
            float dy = (float) (deltaY + MathHelper.sin((float)((srcY % 16.0D + dist * (1.0F - lengthFactor) * fxQuality / 2.0F - time % 32767.0F / 5.0F) / 3.0D)) * 0.5F * f3);
            float dz = (float) (deltaZ + MathHelper.sin((float)((srcZ % 16.0D + dist * (1.0F - lengthFactor) * fxQuality / 2.0F - time % 32767.0F / 5.0F) / 2.0D)) * 0.5F * f3);
            System.out.println(dy * lengthFactor);
//            world.addParticle(ParticleSource.createData(new ParticleColor( world.rand.nextInt(255),  world.rand.nextInt(255),  world.rand.nextInt(255))),
//                    targetPoint.x + dx * lengthFactor - curWidth,  targetPoint.y + dy * lengthFactor, targetPoint.z + dz * lengthFactor, 0,0,0);

//            tessellator.getBuffer().color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);

            float u = (1.0F - lengthFactor) * dist - time * speed;
//            tessellator.getBuffer().pos(dx * lengthFactor - curWidth, dy * lengthFactor, dz * lengthFactor)
//                    .color(100,100,100,255)
//                    .tex(u, 1.0f).endVertex();
//            tessellator.getBuffer().pos(dx * lengthFactor + curWidth, dy * lengthFactor, dz * lengthFactor).tex(u, 0.0f).endVertex();
//            tessellator.getBuffer().getVertexBuilder().addVertex(dx * lengthFactor - curWidth, dy * lengthFactor, dz * lengthFactor, 0,0,0
//                    ,1.0f,u, 1.0f, 1, 1,dx * lengthFactor - curWidth, dy * lengthFactor, dz * lengthFactor);
//            tessellator.getBuffer().addVertex(dx * lengthFactor + curWidth, dy * lengthFactor, dz * lengthFactor,0,0,0
//                    ,1.0f, u, 0.0f, 1, 1,dx * lengthFactor + curWidth, dy * lengthFactor, dz * lengthFactor );

            curWidth -= wGain;
        }
//        tessellator.draw();

        forwardFactor = (forwardFactor + 0.01f) % 1.0f;
//        GL11.glPopMatrix();
    }


    public static IParticleData createData(Vec3d source, Vec3d target){
        return null;
//        return new ArcParticleTypeData(TYPE, source, target);
    }

    @OnlyIn(Dist.CLIENT)
    static class Factory implements IParticleFactory<ArcParticleTypeData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(ArcParticleTypeData data, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleArc(worldIn, new Vec3d(x, y, z), this.spriteSet, data.source, data.target);
        }
    }
}
