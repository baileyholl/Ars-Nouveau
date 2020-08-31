package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.block.Block;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ObjectHolder;

public class ParticleSource extends SpriteTexturedParticle {

    public static final String NAME = "source";
    @ObjectHolder(ArsNouveau.MODID + ":" + ParticleSource.NAME) public static ParticleType<ColorParticleTypeData> TYPE;

    private final double coordX;
    private final double coordY;
    private final double coordZ;

    private ParticleSource(World worldIn, Vec3d coord, IAnimatedSprite sprite, ParticleColor pColor) {
        super(worldIn, coord.getX(), coord.getY(), coord.getZ());
        this.coordX = coord.getX();
        this.coordY = coord.getY();
        this.coordZ = coord.getZ();
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.prevPosX = coordX;
        this.prevPosY = coordY;
        this.prevPosZ = coordZ;
        this.posX = this.prevPosX;
        this.posY = this.prevPosY;
        this.posZ = this.prevPosZ;
        this.particleScale = 0.5F * (this.rand.nextFloat() * 0.2F);
        float f = this.rand.nextFloat() * 0.4F + 0.6F;
        this.particleRed = f * pColor.getRed();
        this.particleGreen = f * pColor.getGreen();
        this.particleBlue = f * pColor.getBlue();
        particleAlpha = 0.5f;
        this.canCollide = false;
        this.maxAge = 100;
        setSize(0.01F, 0.01F);
        this.selectSpriteRandomly(sprite);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return ModParticles.AN_RENDER;
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        int i = super.getBrightnessForRender(partialTick);
        float f = (float) this.age / (float) this.maxAge;
        f = f * f;
        f = f * f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int) (f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
            return;
        }
        motionY -= 0.04D * particleGravity;
        float f = (float) this.age / (float) this.maxAge;
        f = 1.0F - f;
        float f1 = 1.0F - f;
        f1 = f1 * f1;
        f1 = f1 * f1;
        this.posX = this.coordX + this.motionX * f;
        this.posY = this.coordY + this.motionY * f;
        this.posZ = this.coordZ + this.motionZ * f;
        this.particleScale *= f - f1 * 1.2F;
//        wiggleAround(posX, (getBoundingBox().minY + getBoundingBox().maxY) / 2.0D, posZ);

        this.move(motionX, motionY, motionZ);




    }


    // [VanillaCopy] Entity.pushOutOfBlocks with tweaks
    private void wiggleAround(double x, double y, double z)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        Vec3d vec3d = new Vec3d(x - (double)blockpos.getX(), y - (double)blockpos.getY(), z - (double)blockpos.getZ());
        BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for(Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            blockpos$mutableblockpos.setPos(blockpos).move(direction1);
            if (!Block.isOpaque(this.world.getBlockState(blockpos$mutableblockpos).getCollisionShape(this.world, blockpos$mutableblockpos))) {
                double d1 = vec3d.getCoordinate(direction1.getAxis());
                double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
                if (d2 < d0) {
                    d0 = d2;
                    direction = direction1;
                }
            }
        }

        // Botania - made multiplier and add both smaller
        float f = this.rand.nextFloat() * 0.05F + 0.025F;
        float f1 = (float)direction.getAxisDirection().getOffset();
        // Botania - Randomness in other axes as well
        float secondary = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        float secondary2 = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        if (direction.getAxis() == Direction.Axis.X) {
            motionX = (double)(f1 * f);
            motionY = secondary;
            motionZ = secondary2;
        } else if (direction.getAxis() == Direction.Axis.Y) {
            motionX = secondary;
            motionY = (double)(f1 * f);
            motionZ = secondary2;
        } else if (direction.getAxis() == Direction.Axis.Z) {
            motionX = secondary;
            motionY = secondary2;
            motionZ = (double)(f1 * f);
        }
    }

    public static IParticleData createData(ParticleColor color) {
        return new ColorParticleTypeData(TYPE, color);
    }

    @OnlyIn(Dist.CLIENT)
    static class Factory implements IParticleFactory<ColorParticleTypeData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(ColorParticleTypeData data, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleSource(worldIn, new Vec3d(x, y, z), this.spriteSet, data.color);
        }
    }
}
