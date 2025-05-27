package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.compactmods.gander.core.camera.SceneCamera;
import dev.compactmods.gander.render.geometry.BakedLevel;
import dev.compactmods.gander.render.pipeline.PipelineState;
import dev.compactmods.gander.render.pipeline.impl.BakedLevelScreenRenderPipeline;
import dev.compactmods.gander.render.screen.GanderScreenRenderHelper;
import dev.compactmods.gander.render.toolkit.GanderRenderToolkit;
import dev.compactmods.gander.ui.widget.CompassOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ANGanderRender implements Renderable {
    private final GanderScreenRenderHelper renderHelper;
    private final ScreenRectangle renderArea;
    private final BakedLevel bakedLevel;
    private PipelineState state;
    private final CompassOverlay compassOverlay;
    private boolean shouldRenderCompass;
    private final SceneCamera camera;

    public ANGanderRender(BakedLevel bakedLevel, int x, int y, int width, int height) {
        this.bakedLevel = bakedLevel;
        this.compassOverlay = new CompassOverlay();
        this.shouldRenderCompass = false;
        this.camera = new SceneCamera();
        this.renderArea = new ScreenRectangle(new ScreenPosition(x, y), width, height);
        this.renderHelper = new GanderScreenRenderHelper(width, height);
    }

    public SceneCamera camera() {
        return this.camera;
    }

    public void recalculateTranslucency() {
        BakedLevel lvl = (BakedLevel)this.state.get(GanderRenderToolkit.BAKED_LEVEL);
        lvl.resortTranslucency(this.camera.getLookFrom());
    }

    public void shouldRenderCompass(boolean render) {
        this.shouldRenderCompass = render;
    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.state == null) {
            this.state = BakedLevelScreenRenderPipeline.INSTANCE.setup(this::setupInitialState);
        }

        this.renderHelper.renderInScreenSpace(graphics, this.camera, (projMatrix) -> {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(this.bakedLevel.blockBoundaries().getXsize() / (double)-2.0F, this.bakedLevel.blockBoundaries().getYsize() / (double)-2.0F, this.bakedLevel.blockBoundaries().getZsize() / (double)-2.0F);
            this.state.set(GanderRenderToolkit.PROJECTION_MATRIX, projMatrix);
            BakedLevelScreenRenderPipeline.INSTANCE.render(this.state, graphics, partialTicks);
            poseStack.mulPose(projMatrix.invert());
            for(Entity entity : this.bakedLevel.originalLevel().getEntities(null, bakedLevel.blockBoundaries())){
                float partialTick = ClientInfo.partialTicks;
                double d0 = Mth.lerp((double)partialTick, entity.xOld, entity.getX());
                double d1 = Mth.lerp((double)partialTick, entity.yOld, entity.getY());
                double d2 = Mth.lerp((double)partialTick, entity.zOld, entity.getZ());
                float f = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
                float camX = (float)this.camera.getPosition().x;
                float camY = (float)this.camera.getPosition().y;
                float camZ = (float)this.camera.getPosition().z;

                Minecraft.getInstance().getEntityRenderDispatcher()
                        .render(
                                entity,
                                d0 - camX,
                                d1- camY,
                                d2 - camZ,
                                f,
                                partialTick,
                                poseStack,
                                graphics.bufferSource(),
                                Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, partialTick)
                        );
            }
            poseStack.popPose();
        });
    }

    private void setupInitialState(PipelineState state) {
        BlockPos[] blockEntityPositions = BlockPos.betweenClosedStream(this.bakedLevel.blockBoundaries()).filter((p) -> this.bakedLevel.originalLevel().getBlockState(p).hasBlockEntity()).map(BlockPos::immutable).toArray((x$0) -> new BlockPos[x$0]);
        state.set(GanderRenderToolkit.BLOCK_ENTITY_POSITIONS, blockEntityPositions);
        state.set(GanderRenderToolkit.BAKED_LEVEL, this.bakedLevel);
        state.set(GanderRenderToolkit.RENDER_BOUNDS, this.renderArea);
        state.set(GanderRenderToolkit.CAMERA, this.camera);
    }

    private void renderCompass(GuiGraphics graphics, float partialTicks, PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.translate(this.bakedLevel.blockBoundaries().getXsize() / (double)-2.0F, this.bakedLevel.blockBoundaries().getYsize() / (double)-2.0F, this.bakedLevel.blockBoundaries().getZsize() / (double)-2.0F);
        Vector3f position = this.camera.getLookFrom();
        poseStack.translate(-position.x, -position.y, -position.z);
        poseStack.last().pose().negateY();
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        this.compassOverlay.render(graphics, partialTicks);
        poseStack.popPose();
    }

    public void zoom(double factor) {
        this.camera.zoom((float)factor);
    }

    public ScreenRectangle getRenderArea() {
        return this.renderArea;
    }
}
