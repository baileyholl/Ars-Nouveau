package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.EntityEarthElemental;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;

public class SylphModel extends AnimatedEntityModel<EntitySylph> {

    public final AnimatedModelRenderer sylph;
    public final AnimatedModelRenderer head;
    public final AnimatedModelRenderer propellers;
    public final AnimatedModelRenderer propellor1;
    public final AnimatedModelRenderer propellor2;
    public final AnimatedModelRenderer body;
    public final AnimatedModelRenderer arm_left;
    public final AnimatedModelRenderer arm_right;
    public final AnimatedModelRenderer leg_left;
    public final AnimatedModelRenderer leg_right;

    public SylphModel(){
        textureWidth = 32;
        textureHeight = 32;
        sylph = new AnimatedModelRenderer(this);
        sylph.setRotationPoint(0.0F, 24.0F, 0.0F);

        sylph.setModelRendererName("sylph");
        this.registerModelRenderer(sylph);

        head = new AnimatedModelRenderer(this);
        head.setRotationPoint(0.0F, -5.5F, 0.0F);
        sylph.addChild(head);
        head.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 5.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(12, 13).addBox(-3.5F, 0.0F, -1.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);
        head.setTextureOffset(14, 18).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        head.setModelRendererName("head");
        this.registerModelRenderer(head);

        propellers = new AnimatedModelRenderer(this);
        propellers.setRotationPoint(0.0F, -4.0F, 0.0F);
        head.addChild(propellers);

        propellers.setModelRendererName("propellers");
        this.registerModelRenderer(propellers);

        propellor1 = new AnimatedModelRenderer(this);
        propellor1.setRotationPoint(2.0F, 1.5F, 3.0F);
        propellers.addChild(propellor1);
        setRotationAngle(propellor1, 0.0F, 0.0F, 0.3927F);
        propellor1.setTextureOffset(17, 2).addBox(-11.4218F, -0.6205F, -3.5F, 4.0F, 0.0F, 1.0F, 0.0F, false);
        propellor1.setTextureOffset(11, 12).addBox(-11.4218F, -0.6205F, -2.5F, 8.0F, 0.0F, 1.0F, 0.0F, false);
        propellor1.setTextureOffset(15, 16).addBox(-10.4218F, -0.6205F, -1.5F, 6.0F, 0.0F, 1.0F, 0.0F, false);
        propellor1.setTextureOffset(17, 1).addBox(-9.4218F, -0.6205F, -0.5F, 4.0F, 0.0F, 1.0F, 0.0F, false);
        propellor1.setTextureOffset(0, 5).addBox(-4.4218F, -0.6205F, -3.5F, 2.0F, 0.0F, 1.0F, 0.0F, false);
        propellor1.setModelRendererName("propellor1");
        this.registerModelRenderer(propellor1);

        propellor2 = new AnimatedModelRenderer(this);
        propellor2.setRotationPoint(2.0F, 1.5F, 3.0F);
        propellers.addChild(propellor2);
        setRotationAngle(propellor2, 0.0F, 0.0F, -0.3927F);
        propellor2.setTextureOffset(17, 0).addBox(3.7263F, -2.1512F, -3.5F, 4.0F, 0.0F, 1.0F, 0.0F, false);
        propellor2.setTextureOffset(11, 11).addBox(-0.2737F, -2.1512F, -4.5F, 8.0F, 0.0F, 1.0F, 0.0F, false);
        propellor2.setTextureOffset(15, 15).addBox(0.7263F, -2.1512F, -5.5F, 6.0F, 0.0F, 1.0F, 0.0F, false);
        propellor2.setTextureOffset(15, 17).addBox(1.7263F, -2.1512F, -6.5F, 4.0F, 0.0F, 1.0F, 0.0F, false);
        propellor2.setTextureOffset(0, 4).addBox(-1.2737F, -2.1512F, -3.5F, 2.0F, 0.0F, 1.0F, 0.0F, false);
        propellor2.setModelRendererName("propellor2");
        this.registerModelRenderer(propellor2);

        body = new AnimatedModelRenderer(this);
        body.setRotationPoint(0.0F, -3.5F, 0.0F);
        sylph.addChild(body);
        body.setTextureOffset(0, 11).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        body.setModelRendererName("body");
        this.registerModelRenderer(body);

        arm_left = new AnimatedModelRenderer(this);
        arm_left.setRotationPoint(-2.0F, 0.0F, 0.0F);
        body.addChild(arm_left);
        setRotationAngle(arm_left, 0.0F, 0.0F, 0.5236F);
        arm_left.setTextureOffset(0, 11).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        arm_left.setModelRendererName("arm_left");
        this.registerModelRenderer(arm_left);

        arm_right = new AnimatedModelRenderer(this);
        arm_right.setRotationPoint(2.0F, 0.0F, 0.0F);
        body.addChild(arm_right);
        setRotationAngle(arm_right, 0.0F, 0.0F, -0.5236F);
        arm_right.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        arm_right.setModelRendererName("arm_right");
        this.registerModelRenderer(arm_right);

        leg_left = new AnimatedModelRenderer(this);
        leg_left.setRotationPoint(-1.0F, 2.0F, 0.0F);
        body.addChild(leg_left);
        leg_left.setTextureOffset(0, 19).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        leg_left.setModelRendererName("leg_left");
        this.registerModelRenderer(leg_left);

        leg_right = new AnimatedModelRenderer(this);
        leg_right.setRotationPoint(1.0F, 2.0F, 0.0F);
        body.addChild(leg_right);
        leg_right.setTextureOffset(18, 3).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        leg_right.setModelRendererName("leg_right");
        this.registerModelRenderer(leg_right);

        this.rootBones.add(sylph);
    }

    @Override
    public void setRotationAngles(EntitySylph entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.rotateAngleX = headPitch * 0.010453292F;
        this.head.rotateAngleY = netHeadYaw * 0.015453292F;
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        sylph.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public ResourceLocation getAnimationFileLocation() {
        return new ResourceLocation(ArsNouveau.MODID + ":animations/sylph_animations.json");
    }
}
