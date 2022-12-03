package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import javax.annotation.Nullable;

public class SpellBowRenderer extends FixedGeoItemRenderer<SpellBow> {
    public SpellBowRenderer() {
        super(new SpellBowModel());
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        //we override the color getter for a specific bone, this means the other ones need to use the neutral color
        if (bone.getName().equals("gem")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, Color.WHITE.getRed() / 255f, Color.WHITE.getGreen() / 255f, Color.WHITE.getBlue() / 255f, Color.WHITE.getAlpha() / 255f);
        }
    }

    @Override
    public Color getRenderColor(Object animatable, float partialTick, PoseStack poseStack, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, @org.jetbrains.annotations.Nullable VertexConsumer buffer, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        if (currentItemStack.hasTag()) {
            color = ((SpellBow) animatable).getSpellCaster(currentItemStack).getColor();
        }
        return Color.ofRGBA(color.toWrapper().r, color.toWrapper().g, color.toWrapper().b, 200);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        if (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            Player player = Minecraft.getInstance().player;
            Vec3 playerPos = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 look = player.getLookAngle(); // or getLook(partialTicks)
            //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
            Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
            Vec3 forward = look;
            Vec3 down = right.cross(forward);

            //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
            right = right.scale(0.2 - player.attackAnim);
            forward = forward.scale(0.45f);
            down = down.scale(-0.1 - player.attackAnim);
            Vec3 laserPos = playerPos.add(right);
            laserPos = laserPos.add(forward);
            laserPos = laserPos.add(down);
            ISpellCaster tool = CasterUtil.getCaster(itemStack);
            int timeHeld = 72000 - Minecraft.getInstance().player.getUseItemRemainingTicks();
            if (timeHeld > 0 && timeHeld != 72000) {
                float scaleAge = (float) ParticleUtil.inRange(0.05, 0.1);
                if (player.level.random.nextInt(6) == 0) {
                    for (int i = 0; i < 1; i++) {
                        Vec3 particlePos = new Vec3(laserPos.x, laserPos.y, laserPos.z);
                        particlePos = particlePos.add(ParticleUtil.pointInSphere().scale(0.3f));
                        player.level.addParticle(ParticleLineData.createData(tool.getColor(), scaleAge, 5 + player.level.random.nextInt(20)),
                                particlePos.x(), particlePos.y(), particlePos.z(),
                                laserPos.x(), laserPos.y(), laserPos.z());
                    }
                }
            }
        }
        super.renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, p_239207_6_);

    }

    @Override
    public void render(GeoModel model, Object animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        IBone top = model.getBone("bow_top").get();
        IBone gem = model.getBone("gem").get();
        IBone bottom = model.getBone("bow_bot").get();
        float outerAngle = ((ClientInfo.ticksInGame + partialTicks) / 10.0f) % 360;
        top.setRotationZ((float) Math.toRadians(-10.0));
        top.setRotationY(0.0f);
        top.setRotationX(0.0f);

        bottom.setRotationZ((float) Math.toRadians(10f));
        bottom.setRotationY(0);
        bottom.setRotationX((float) Math.toRadians(-180.0f));


        if (Minecraft.getInstance().player.getMainHandItem().equals(currentItemStack)) {
            // System.out.println(72000 - Minecraft.getInstance().player.getItemInUseCount());
            int timeHeld = (int) (72000 - Minecraft.getInstance().player.getUseItemRemainingTicks() + partialTicks);

            if (timeHeld != 0 && timeHeld != 72000) {
                top.setRotationZ((float) (Math.toRadians(-10) - Math.toRadians(timeHeld) * 2.0f));
                bottom.setRotationZ((float) (Math.toRadians(-10f) + Math.toRadians(timeHeld) * 2.0f));
                outerAngle = ((ClientInfo.ticksInGame + partialTicks) / 5.0f) % 360;
                if (timeHeld >= 19) {
                    top.setRotationZ((float) (Math.toRadians(-10) - Math.toRadians(19) * 2.0f));
                    bottom.setRotationZ((float) (Math.toRadians(-10) + Math.toRadians(19) * 2.0f));
                    outerAngle = ((ClientInfo.ticksInGame + partialTicks) / 3.0f) % 360;
                }
            }
        }
        gem.setRotationX(outerAngle);
        gem.setRotationY(outerAngle);

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
