package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.ANGeoModel;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;

public class SpellBowRenderer extends FixedGeoItemRenderer<SpellBow> {
    public SpellBowRenderer() {
        super(new ANGeoModel<SpellBow>("geo/spellbow.geo.json", "textures/item/spellbow.png", "animations/wand_animation.json"));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SpellBow animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (bone.getName().equals("gem")) {
            //NOTE: if the bone have a parent, the recursion will get here with the neutral color, making the color getter useless
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
        }
    }

    @Override
    public Color getRenderColor(SpellBow animatable, float partialTick, int packedLight) {
        ParticleColor color = ParticleColor.defaultParticleColor();
        var caster = SpellCasterRegistry.from(currentItemStack);
        if (caster != null){
            color = caster.getColor();
        }
        return Color.ofRGBA(color.getRed(), color.getGreen(), color.getBlue(), 0.75f);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
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
            ISpellCaster tool = SpellCasterRegistry.from(itemStack);
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
    public void renderFinal(PoseStack poseStack, SpellBow animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTicks, int packedLight, int packedOverlay, int color) {
        GeoBone top = model.getBone("bow_top").get();
        GeoBone gem = model.getBone("gem").get();
        GeoBone bottom = model.getBone("bow_bot").get();
        double ticks = animatable.getTick(animatable);
        float outerAngle = (float) (((ticks + partialTicks) / 10.0f) % 360);
        top.setRotZ((float) Math.toRadians(-10.0));
        top.setRotY(0.0f);
        top.setRotX(0.0f);

        bottom.setRotZ((float) Math.toRadians(10f));
        bottom.setRotY(0);
        bottom.setRotX((float) Math.toRadians(-180.0f));


        if (Minecraft.getInstance().player.getMainHandItem().equals(currentItemStack)) {
            // System.out.println(72000 - Minecraft.getInstance().player.getItemInUseCount());
            int timeHeld = 72000 - Minecraft.getInstance().player.getUseItemRemainingTicks();

            if (timeHeld != 0 && timeHeld != 72000) {
                top.setRotZ((float) (Math.toRadians(-10) - Math.toRadians(timeHeld) * 2.0f));
                bottom.setRotZ((float) (Math.toRadians(-10f) + Math.toRadians(timeHeld) * 2.0f));
                outerAngle = (float) (((ticks + partialTicks) / 5.0f) % 360);
                if (timeHeld >= 19) {
                    top.setRotZ((float) (Math.toRadians(-10) - Math.toRadians(19) * 2.0f));
                    bottom.setRotZ((float) (Math.toRadians(-10) + Math.toRadians(19) * 2.0f));
                    outerAngle = (float) (((ticks + partialTicks) / 3.0f) % 360);
                }
            }
        }
        gem.setRotX(outerAngle);
        gem.setRotY(outerAngle);
    }

    @Override
    public RenderType getRenderType(SpellBow animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
