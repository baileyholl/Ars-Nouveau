package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ArcanoBossModel extends HierarchicalModel<ArcanoBoss> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "arcano_boss"), "main");
    private final ModelPart root;
    private final Map<String, Optional<ModelPart>> descendantNameCache = new HashMap<>();

    private final ModelPart hip;
    private final ModelPart torso;
    private final ModelPart arm_right;
    private final ModelPart staff;
    private final ModelPart finial;
    private final ModelPart finial2;
    private final ModelPart arm_left;
    private final ModelPart chest;
    private final ModelPart wings;
    private final ModelPart wing_top_right;
    private final ModelPart wing_top_left;
    private final ModelPart wing_bot_right;
    private final ModelPart wing_bot_left;
    private final ModelPart head;
    private final ModelPart face;
    private final ModelPart tail;

    public ArcanoBossModel(ModelPart root) {
        this.root = root.getChild("base");
        getAllPartsFromRoot(this.root())
                .forEach(entry -> this.descendantNameCache.put(entry.getKey(), Optional.of(entry.getValue())));
        this.hip = this.root.getChild("hip");
        this.torso = this.hip.getChild("torso");
        this.arm_right = this.torso.getChild("arm_right");
        this.staff = this.arm_right.getChild("staff");
        this.finial = this.staff.getChild("finial");
        this.finial2 = this.staff.getChild("finial2");
        this.arm_left = this.torso.getChild("arm_left");
        this.chest = this.torso.getChild("chest");
        this.wings = this.chest.getChild("wings");
        this.wing_top_right = this.wings.getChild("wing_top_right");
        this.wing_top_left = this.wings.getChild("wing_top_left");
        this.wing_bot_right = this.wings.getChild("wing_bot_right");
        this.wing_bot_left = this.wings.getChild("wing_bot_left");
        this.head = this.torso.getChild("head");
        this.face = this.head.getChild("face");
        this.tail = this.root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition hip = base.addOrReplaceChild("hip", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition torso = hip.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));

        PartDefinition arm_right = torso.addOrReplaceChild("arm_right", CubeListBuilder.create().texOffs(82, 13).addBox(-1.5F, -1.5218F, -1.5005F, 3.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -5.5F, 0.5F));

        PartDefinition staff = arm_right.addOrReplaceChild("staff", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0218F, -19.5005F, 2.0F, 2.0F, 39.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.75F, 0.0F));

        PartDefinition finial = staff.addOrReplaceChild("finial", CubeListBuilder.create().texOffs(82, 34).addBox(-11.0F, -25.7718F, -21.0005F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(82, 38).addBox(-5.0F, -25.7718F, -22.0005F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(82, 38).addBox(-11.0F, -25.7718F, -22.0005F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(-5.0F, -25.7718F, -25.0005F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(-13.0F, -25.7718F, -25.0005F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 88).addBox(-8.0F, -25.7718F, -24.0005F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 24.75F, -0.5F));

        PartDefinition finial2 = staff.addOrReplaceChild("finial2", CubeListBuilder.create().texOffs(82, 34).addBox(-11.0F, -25.7718F, 19.0005F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(82, 38).addBox(-5.0F, -25.7718F, 21.0005F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(82, 38).addBox(-11.0F, -25.7718F, 21.0005F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(-5.0F, -25.7718F, 22.0005F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 88).addBox(-13.0F, -25.7718F, 22.0005F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 88).addBox(-8.0F, -25.7718F, 22.0005F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 24.75F, 0.5F));

        PartDefinition arm_left = torso.addOrReplaceChild("arm_left", CubeListBuilder.create().texOffs(82, 13).addBox(-1.5F, -1.5218F, -1.5005F, 3.0F, 18.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -5.5F, 0.5F));

        PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(58, 53).addBox(-3.0F, -1.0F, -7.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(58, 53).addBox(-3.0F, -1.0F, -7.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(-0.75F))
                .texOffs(0, 41).addBox(-7.5F, -1.5F, -8.0F, 18.0F, 36.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -6.0F, 3.0F));

        PartDefinition wings = chest.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, -2.0F, 4.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition wing_top_right = wings.addOrReplaceChild("wing_top_right", CubeListBuilder.create().texOffs(58, 47).addBox(-32.0F, -3.0F, 0.0F, 32.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 3.0F, 0.0F));

        PartDefinition wing_top_left = wings.addOrReplaceChild("wing_top_left", CubeListBuilder.create().texOffs(58, 41).addBox(0.0F, -3.0F, 0.0F, 33.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 3.0F, 0.0F));

        PartDefinition wing_bot_right = wings.addOrReplaceChild("wing_bot_right", CubeListBuilder.create().texOffs(58, 47).addBox(-32.0F, -3.0F, 0.0F, 32.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 6.0F, 0.0F));

        PartDefinition wing_bot_left = wings.addOrReplaceChild("wing_bot_left", CubeListBuilder.create().texOffs(58, 41).addBox(0.0F, -3.0F, 0.0F, 33.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 6.0F, 0.0F));

        PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 2.5F));

        PartDefinition face = head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(70, 71).addBox(-5.5F, -12.5F, -2.0F, 11.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(82, 0).addBox(-4.5F, -3.5F, -4.0F, 9.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, 0.0F));

        PartDefinition tail = base.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(58, 71).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ArcanoBoss entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.descendantNameCache.values().forEach(optional -> optional.ifPresent(ModelPart::resetPose));
        this.animate(entity.idle, ArcanoBossAnimations.idle, ageInTicks);
        this.animate(entity.flight, ArcanoBossAnimations.flight, ageInTicks);
        this.animate(entity.swingStaff, ArcanoBossAnimations.swing_staff, ageInTicks);
        this.animate(entity.spinStaff, ArcanoBossAnimations.spin_staff, ageInTicks);
        this.animate(entity.spinStaff2, ArcanoBossAnimations.spin_staff2, ageInTicks);


//        this.animate(entity.idleAnimationState, RootminAnimations.IDLE, ageInTicks);
//        this.animate(entity.angryAnimationState, RootminAnimations.ANGRY, ageInTicks);
//        this.animate(entity.curiousAnimationState, RootminAnimations.CURIOUS, ageInTicks);
//        this.animate(entity.curseAnimationState, RootminAnimations.CURSE, ageInTicks);
//        this.animate(entity.embarassedAnimationState, RootminAnimations.EMBARASSED, ageInTicks);
//        this.animate(entity.shockAnimationState, RootminAnimations.SHOCK, ageInTicks);
//        this.animate(entity.shootAnimationState, RootminAnimations.SHOOT, ageInTicks);
//        this.animate(entity.runAnimationState, RootminAnimations.RUN, ageInTicks);
//        this.animate(entity.walkAnimationState, RootminAnimations.WALK, ageInTicks);
//        this.animate(entity.blockToEntityAnimationState, RootminAnimations.BLOCK_TO_ENTITY, ageInTicks);
//        this.animate(entity.entityToBlockAnimationState, RootminAnimations.ENTITY_TO_BLOCK, ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int packedColor) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
    }


    @Override
    public Optional<ModelPart> getAnyDescendantWithName(String name) {
        return super.getAnyDescendantWithName(name);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public Stream<Map.Entry<String, ModelPart>> getAllPartsFromRoot(ModelPart root) {
        return Stream.concat(
                Stream.of(Map.entry("base", root)),
                root.children.entrySet().stream().flatMap(this::getAllParts)
        );
    }

    private Stream<Map.Entry<String, ModelPart>> getAllParts(Map.Entry<String, ModelPart> entry) {
        return Stream.concat(
                Stream.of(entry),
                entry.getValue().children.entrySet().stream().flatMap(this::getAllParts)
        );
    }

}