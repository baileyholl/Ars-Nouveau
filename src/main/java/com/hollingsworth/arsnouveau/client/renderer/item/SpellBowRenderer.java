package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import javax.annotation.Nullable;

public class SpellBowRenderer extends FixedGeoItemRenderer<Wand> {
    public SpellBowRenderer() {
        super(new SpellBowModel());
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int p_239207_6_) {
        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND){
            PlayerEntity player = Minecraft.getInstance().player;
            Vector3d playerPos = player.position().add(0, player.getEyeHeight(), 0);
            Vector3d look = player.getLookAngle(); // or getLook(partialTicks)
            //The next 3 variables are directions on the screen relative to the players look direction. So right = to the right of the player, regardless of facing direction.
            Vector3d right = new Vector3d(-look.z, 0, look.x).normalize();
            Vector3d forward = look;
            Vector3d down = right.cross(forward);

            //These are used to calculate where the particles are going. We want them going into the laser, so we move the destination right, down, and forward a bit.
            right = right.scale(0.2 - player.attackAnim);
            forward = forward.scale(0.45f);
            down = down.scale(-0.1 - player.attackAnim);
            Vector3d laserPos = playerPos.add(right);
            laserPos = laserPos.add(forward);
            laserPos = laserPos.add(down);
            ISpellCaster tool = SpellCaster.deserialize(itemStack);
                int timeHeld = (int) (72000 - Minecraft.getInstance().player.getUseItemRemainingTicks());
                if(timeHeld > 0 && timeHeld != 72000){
                    float scaleAge = (float) ParticleUtil.inRange(0.05,0.1);
                    if(player.level.random.nextInt( 6)  == 0){
                        for(int i =0; i< 1; i++){
                            Vector3d particlePos = new Vector3d(laserPos.x, laserPos.y,laserPos.z);
                            particlePos = particlePos.add(ParticleUtil.pointInSphere().scale(0.3f));
                            player.level.addParticle(ParticleLineData.createData(tool.getColor().toParticleColor() ,scaleAge, 5+player.level.random.nextInt(20)) ,
                                    particlePos.x(), particlePos.y(), particlePos.z(),
                                    laserPos.x()   , laserPos.y() , laserPos.z());
                        }
                    }
                }
        }
        super.renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, p_239207_6_);

    }
    @Override
    public void render(GeoModel model, Object animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        IBone top = model.getBone("bow_top").get();
        IBone gem = model.getBone("gem").get();
        IBone bottom = model.getBone("bow_top2").get();
        float outerAngle = ((ClientInfo.ticksInGame + partialTicks) /10.0f) % 360;
        top.setRotationZ((float) Math.toRadians(-10.0));
        top.setRotationY(0.0f);
        top.setRotationX(0.0f);

        bottom.setRotationZ((float) Math.toRadians(10f));
        bottom.setRotationY(0);
        bottom.setRotationX((float) Math.toRadians(-180.0f));


        if(Minecraft.getInstance().player.getMainHandItem().equals(currentItemStack)) {
           // System.out.println(72000 - Minecraft.getInstance().player.getItemInUseCount());
            int timeHeld = (int) (72000 - Minecraft.getInstance().player.getUseItemRemainingTicks() + partialTicks);

            if(timeHeld != 0 && timeHeld != 72000){
                top.setRotationZ((float) (Math.toRadians(-10) - Math.toRadians(timeHeld) *2.0f));
                bottom.setRotationZ((float) (Math.toRadians(-10f) +Math.toRadians(timeHeld) *2.0f));
                outerAngle =  ((ClientInfo.ticksInGame + partialTicks) /5.0f) % 360;
                if(timeHeld >= 19){
                    top.setRotationZ((float) (Math.toRadians(-10) - Math.toRadians(19) *2.0f));
                    bottom.setRotationZ((float) (Math.toRadians(-10)  +Math.toRadians(19) *2.0f));
                    outerAngle =  ((ClientInfo.ticksInGame + partialTicks) /3.0f) % 360;
                }
            }
        }
        gem.setRotationX(outerAngle);
        gem.setRotationY(outerAngle);

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer,vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

}
