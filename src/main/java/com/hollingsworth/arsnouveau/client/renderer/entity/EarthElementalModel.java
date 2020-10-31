package com.hollingsworth.arsnouveau.client.renderer.entity;

public class EarthElementalModel {
//
//    private final AnimatedModelRenderer earth_elemental;
//    private final AnimatedModelRenderer ball;
//    private final AnimatedModelRenderer body;
//    private final AnimatedModelRenderer upper_jaw;
//    private final AnimatedModelRenderer right_arm;
//    private final AnimatedModelRenderer left_arm;
//
//    private final AnimatedModelRenderer grate;
//    private final AnimatedModelRenderer item;
//    public EarthElementalModel() {
//        textureWidth = 128;
//        textureHeight = 128;
//        earth_elemental = new AnimatedModelRenderer(this);
//        earth_elemental.setRotationPoint(0.0F, 24.0F, 0.5F);
//
//        earth_elemental.setModelRendererName("earth_elemental");
//        this.registerModelRenderer(earth_elemental);
//
//        ball = new AnimatedModelRenderer(this);
//        ball.setRotationPoint(0.0F, -6.0F, 0.0F);
//        earth_elemental.addChild(ball);
//        ball.setTextureOffset(20, 72).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);
//        ball.setTextureOffset(71, 11).addBox(5.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
//        ball.setTextureOffset(0, 54).addBox(-6.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
//        ball.setTextureOffset(10, 72).addBox(-2.0F, -2.0F, 5.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);
//        ball.setTextureOffset(58, 10).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
//        ball.setTextureOffset(53, 57).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
//        ball.setTextureOffset(36, 33).addBox(-3.0F, 4.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
//        ball.setTextureOffset(48, 42).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
//        ball.setTextureOffset(69, 0).addBox(-3.0F, -3.0F, 4.0F, 6.0F, 6.0F, 1.0F, 0.0F, false);
//        ball.setTextureOffset(21, 56).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 1.0F, 0.0F, false);
//        ball.setTextureOffset(0, 42).addBox(4.0F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, 0.0F, false);
//        ball.setTextureOffset(64, 64).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
//        ball.setTextureOffset(18, 16).addBox(-5.0F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, 0.0F, false);
//        ball.setModelRendererName("ball");
//        this.registerModelRenderer(ball);
//
//        body = new AnimatedModelRenderer(this);
//        body.setRotationPoint(0.0F, -6.0F, 0.5F);
//        earth_elemental.addChild(body);
//        body.setTextureOffset(36, 16).addBox(-8.0F, -14.0F, -8.0F, 16.0F, 14.0F, 3.0F, 0.0F, false);
//        body.setTextureOffset(18, 30).addBox(-8.0F, -14.0F, -5.0F, 3.0F, 14.0F, 12.0F, 0.0F, false);
//        body.setTextureOffset(68, 33).addBox(-5.0F, -14.0F, 4.0F, 10.0F, 14.0F, 3.0F, 0.0F, false);
//        body.setTextureOffset(0, 16).addBox(5.0F, -14.0F, -5.0F, 3.0F, 14.0F, 12.0F, 0.0F, false);
//        body.setTextureOffset(39, 47).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 1.0F, 9.0F, 0.0F, false);
//        body.setTextureOffset(40, 0).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 1.0F, 9.0F, 0.0F, false);
//        body.setTextureOffset(0, 22).addBox(2.0F, -12.0F, 7.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//        body.setTextureOffset(0, 16).addBox(-5.0F, -12.0F, 7.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//        body.setTextureOffset(0, 6).addBox(2.0F, -7.0F, 7.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//        body.setTextureOffset(0, 0).addBox(-5.0F, -7.0F, 7.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//        body.setModelRendererName("body");
//        this.registerModelRenderer(body);
//
//        upper_jaw = new AnimatedModelRenderer(this);
//        upper_jaw.setRotationPoint(0.0F, -14.0F, 6.0F);
//        body.addChild(upper_jaw);
//        upper_jaw.setTextureOffset(0, 0).addBox(-7.0F, -4.0F, -12.0F, 14.0F, 4.0F, 12.0F, 0.0F, false);
//        upper_jaw.setTextureOffset(30, 73).addBox(-5.0F, -6.0F, -7.0F, 3.0F, 2.0F, 2.0F, 0.0F, false);
//        upper_jaw.setTextureOffset(32, 65).addBox(-6.0F, -5.0F, -3.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
//        upper_jaw.setTextureOffset(53, 64).addBox(2.0F, -6.0F, -7.0F, 3.0F, 2.0F, 2.0F, 0.0F, false);
//        upper_jaw.setTextureOffset(58, 39).addBox(3.0F, -5.0F, -3.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
//        upper_jaw.setTextureOffset(21, 63).addBox(-2.0F, -6.0F, -10.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
//        upper_jaw.setModelRendererName("upper_jaw");
//        this.registerModelRenderer(upper_jaw);
//
//        right_arm = new AnimatedModelRenderer(this);
//        right_arm.setRotationPoint(-8.0F, -10.5F, -1.0F);
//        body.addChild(right_arm);
//        setRotationAngle(right_arm, 1.5708F, 0.0F, 0.0F);
//        right_arm.setTextureOffset(32, 57).addBox(-5.0F, -2.5F, -9.0F, 5.0F, 5.0F, 11.0F, 0.0F, false);
//        right_arm.setTextureOffset(31, 59).addBox(-3.5F, 1.5F, -11.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
//        right_arm.setTextureOffset(0, 72).addBox(-4.5F, -2.0F, 2.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);
//        right_arm.setTextureOffset(71, 57).addBox(-2.0F, -2.5F, -12.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
//        right_arm.setTextureOffset(68, 50).addBox(-5.0F, -2.5F, -12.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
//        right_arm.setModelRendererName("right_arm");
//        this.registerModelRenderer(right_arm);
//
//        left_arm = new AnimatedModelRenderer(this);
//        left_arm.setRotationPoint(8.0F, -10.5F, -1.0F);
//        body.addChild(left_arm);
//        setRotationAngle(left_arm, 1.5708F, 0.0F, 0.0F);
//        left_arm.setTextureOffset(0, 56).addBox(0.0F, -2.5F, -9.0F, 5.0F, 5.0F, 11.0F, 0.0F, false);
//        left_arm.setTextureOffset(54, 33).addBox(1.5F, 1.5F, -11.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
//        left_arm.setTextureOffset(0, 62).addBox(0.5F, -2.0F, 2.0F, 4.0F, 4.0F, 1.0F, 0.0F, false);
//        left_arm.setTextureOffset(8, 42).addBox(0.0F, -2.5F, -12.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
//        left_arm.setTextureOffset(26, 16).addBox(3.0F, -2.5F, -12.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);
//        left_arm.setModelRendererName("left_arm");
//        this.registerModelRenderer(left_arm);
//
//        grate = new AnimatedModelRenderer(this);
//        grate.setRotationPoint(0.0F, -7.0F, -7.5F);
//        body.addChild(grate);
//        grate.setTextureOffset(40, 10).addBox(-5.0F, -1.0F, -1.0F, 10.0F, 1.0F, 1.0F, 0.0F, false);
//        grate.setTextureOffset(40, 0).addBox(-5.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
//        grate.setTextureOffset(36, 33).addBox(4.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
//        grate.setTextureOffset(32, 21).addBox(1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
//        grate.setTextureOffset(18, 16).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
//        grate.setTextureOffset(36, 40).addBox(-5.0F, 5.0F, -1.0F, 10.0F, 1.0F, 1.0F, 0.0F, false);
//        grate.setModelRendererName("grate");
//        this.registerModelRenderer(grate);
//
//        item = new AnimatedModelRenderer(this);
//        item.setRotationPoint(0.0F, 13.5F, 0.0F);
//        item.setTextureOffset(73, 72).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//        item.setModelRendererName("item");
//        this.registerModelRenderer(item);
//
//        this.rootBones.add(earth_elemental);
//        this.rootBones.add(item);
//    }
//
//    @Override
//    public void setRotationAngles(EntityEarthElemental entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
//        //previously the render function, render code was moved to a method below
//    }
//
//    @Override
//    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
//        earth_elemental.render(matrixStack, buffer, packedLight, packedOverlay);
//    }
//
//    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }
//
//    @Override
//    public ResourceLocation getAnimationFileLocation() {
//        return new ResourceLocation(ArsNouveau.MODID + ":animations/earth_elemental_animation.json");
//    }
}
