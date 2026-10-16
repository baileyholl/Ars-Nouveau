package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.BuilderGeist;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class BuilderGeistModel extends HierarchicalModel<BuilderGeist> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public BuilderGeistModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.head = this.root.getChild("head");
    }

    @Override
    public void setupAnim(BuilderGeist entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        float f = Mth.cos(ageInTicks * 5.5F * Mth.DEG_TO_RAD) * 0.1F;
        this.rightArm.zRot = (float) (Math.PI / 5) + f;
        this.leftArm.zRot = -((float) (Math.PI / 5) + f);
        this.body.xRot = (float) (Math.PI / 20);
        this.leftWing.yRot = 1.0995574F + Mth.cos(ageInTicks * 45.836624F * Mth.DEG_TO_RAD) * Mth.DEG_TO_RAD * 16.2F;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.leftWing.xRot = 0.47123888F;
        this.leftWing.zRot = -0.47123888F;
        this.rightWing.xRot = 0.47123888F;
        this.rightWing.zRot = 0.47123888F;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        boolean right = side == HumanoidArm.RIGHT;
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        (right ? this.rightArm : this.leftArm).translateAndRotate(poseStack);
        poseStack.scale(0.55F, 0.55F, 0.55F);
        poseStack.translate(right ? 0.046875 : -0.046875, -0.15625, 0.078125);
    }
}
