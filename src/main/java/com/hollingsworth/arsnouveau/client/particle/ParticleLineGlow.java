package com.hollingsworth.arsnouveau.client.particle;

//@OnlyIn(Dist.CLIENT)
//public class ParticleLineGlow extends SpriteTexturedParticle {
//
//    public float colorR = 0;
//    public float colorG = 0;
//    public float colorB = 0;
//    public float initScale = 0;
//    public float initX = 0;
//    public float initY = 0;
//    public float initZ = 0;
//    public float destX = 0;
//    public float destY = 0;
//    public float destZ = 0;
//    public static final String NAME = "line_glow";
//
//    @ObjectHolder(ArsNouveau.MODID + ":" + ParticleLineGlow.NAME) public static ParticleType<ColorParticleTypeData> TYPE;
//
//    public ParticleLineGlow(World worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, IAnimatedSprite sprite) {
//        super(worldIn, x,y,z,0,0,0);
//        this.colorR = r;
//        this.colorG = g;
//        this.colorB = b;
//        if (this.colorR > 1.0){
//            this.colorR = this.colorR/255.0f;
//        }
//        if (this.colorG > 1.0){
//            this.colorG = this.colorG/255.0f;
//        }
//        if (this.colorB > 1.0){
//            this.colorB = this.colorB/255.0f;
//        }
//        this.setColor(colorR, colorG, colorB);
//        this.maxAge = lifetime;
//        this.particleScale = scale;
//        this.initScale = scale;
//        this.motionX = 0;
//        this.motionY = 0;
//        this.motionZ = 0;
//        this.initX = (float)x;
//        this.initY = (float)y;
//        this.initZ = (float)z;
//        this.destX = (float)vx;
//        this.destY = (float)vy;
//        this.destZ = (float)vz;
//        this.particleAngle = 2.0f*(float)Math.PI;
//        this.selectSpriteRandomly(sprite);
//    }
//    @Override
//    public IParticleRenderType getRenderType() {
//        return RenderTypes.EMBER_RENDER;
//    }
//
//
//    @Override
//    public int getBrightnessForRender(float pTicks){
//        return 255;
//    }
//
//
//    @Override
//    public void tick(){
//        super.tick();
//
//        if (new Random().nextInt(6) == 0){
//            this.age++;
//        }
//        float lifeCoeff = (float)this.age/(float)this.maxAge;
//        this.posX = ((1.0f-lifeCoeff)*initX + (lifeCoeff)*destX);
//        this.posY = ((1.0f-lifeCoeff)*initY + (lifeCoeff)*destY);
//        this.posZ = ((1.0f-lifeCoeff)*initZ + (lifeCoeff)*destZ);
//        this.particleScale = initScale-initScale*lifeCoeff;
//        this.particleAlpha = 1.0f-lifeCoeff;
//        this.prevParticleAngle = particleAngle;
//        particleAngle += 1.0f;
//    }
//
//
//
//    @Override
//    public boolean isAlive() {
//        return this.age < this.maxAge;
//    }
//
//    public static IParticleData createData(ParticleColor color) {
//        return new ColorParticleTypeData(TYPE, color);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    static class Factory implements IParticleFactory<ColorParticleTypeData> {
//        private final IAnimatedSprite spriteSet;
//
//        public Factory(IAnimatedSprite sprite) {
//            this.spriteSet = sprite;
//        }
//
//        @Override
//        public Particle makeParticle(ColorParticleTypeData data, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
////            return new ParticleGlow(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, worldIn.rand.nextInt(255), worldIn.rand.nextInt(255), worldIn.rand.nextInt(255), 1.0f, .25f, 36, this.spriteSet);
//            return new ParticleLineGlow(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, 225, 60, 60, 1.0f, 1.0f, 36, this.spriteSet);
//
//        }
//    }
//
//
//}
