package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    private final static ResourceLocation laserBeam = new ResourceLocation(ArsNouveau.MODID , "textures/particle/laser.png");
    private final static ResourceLocation laserBeam2 = new ResourceLocation(ArsNouveau.MODID , "textures/particle/laser2.png");
    private final static ResourceLocation laserBeamGlow = new ResourceLocation(ArsNouveau.MODID , "textures/particle/laser_glow.png");

    public static void drawLasers(RenderWorldLastEvent event, Vec3d from, Vec3d to, double xOffset, double yOffset, double zOffset, float r, float g, float b, float thickness, PlayerEntity player, float ticks, float speedModifier) {
        Hand activeHand = Hand.MAIN_HAND;
        double distance = from.subtract(to).length();
        long gameTime = ClientInfo.ticksInGame;
        double v = gameTime * speedModifier;
        float additiveThickness = (thickness * 3.5f) * calculateLaserFlickerModifier(gameTime);
        BufferBuilder wr = Tessellator.getInstance().getBuffer();

        Vec3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack matrix = event.getMatrixStack();
        matrix.translate(view.getX(), view.getY(), view.getZ());


        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        RenderSystem.enableColorMaterial();
        // This makes it so we don't clip into the world, we're effectively drawing on it
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        //This makes it so multiplayer doesn't matter which side the player is standing on to see someone elses laser
        RenderSystem.disableCull();
        RenderSystem.enableTexture();

        RenderSystem.rotatef(MathHelper.lerp(ticks, -player.rotationYaw, -player.prevRotationYaw), 0, 1, 0);
        RenderSystem.rotatef(MathHelper.lerp(ticks, player.rotationPitch, player.prevRotationPitch), 1, 0, 0);

        // additive laser beam
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, 0.7f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeamGlow);
        drawBeam(from, to, additiveThickness, activeHand, distance, wr, 0.5, 1, ticks);

        // main laser, colored part
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam2);
        drawBeam(from, to, thickness, activeHand, distance, wr, v, v + distance * 1.5, ticks);
        // white core
        RenderSystem.color4f(100 / 255f, 100 / 255f, 100 / 255f, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(laserBeam);
        drawBeam(from, to, thickness / 2, activeHand, distance, wr, v, v + distance * 1.5, ticks);

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.popMatrix();
    }


    private static float calculateLaserFlickerModifier(long gameTime) {
        return 0.9f + 0.1f * MathHelper.sin(gameTime * 0.99f) * MathHelper.sin(gameTime * 0.3f) * MathHelper.sin(gameTime * 0.1f);
    }

    private static void drawBeam(Vec3d from, Vec3d to, float thickness, Hand hand, double distance, BufferBuilder wr, double v1, double v2, float ticks) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        float startXOffset = (float) from.x;
        float startYOffset = (float) from.y;
        float startZOffset = (float) from.z;
        float xOffset = 0;
        float yOffset = 0;
        float zOffset = 0;

        float f = (float) MathHelper.lerp(ticks, from.x, to.x);
        float f1 = (float) MathHelper.lerp(ticks, from.y, to.y);
        startXOffset = startXOffset + (f1);
        startYOffset = startYOffset + (f);

        // Support for hand sides remembering to take into account of Skin options
        if( Minecraft.getInstance().gameSettings.mainHand != HandSide.RIGHT )
            hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;

        wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        wr.pos(startXOffset, -thickness + startYOffset, startZOffset).tex(1, (float) v1).endVertex();
        wr.pos(xOffset, -thickness + yOffset, distance + zOffset).tex(1, (float) v2).endVertex();
        wr.pos(xOffset, thickness + yOffset, distance + zOffset).tex(0, (float) v2).endVertex();
        wr.pos(startXOffset, thickness + startYOffset, startZOffset).tex(0, (float) v1).endVertex();

        Tessellator.getInstance().draw();
    }
}
