package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class EntityEntry extends SinglePageWidget {
    EntityType<?> entityType;
    Component description;
    float scale;
    LivingEntity entity;
    int yOffset = 0;

    public EntityEntry(EntityType<? extends LivingEntity> entityType, Component description, float scale, BaseDocScreen parent, int x, int y, int width, int height, int yOffset) {
        super(parent, x, y, width, height);
        this.entityType = entityType;
        this.description = description;
        this.scale = scale;
        this.entity = entityType.create(parent.getMinecraft().level);
        this.yOffset = yOffset;
    }

    public static SinglePageCtor create(EntityType<? extends LivingEntity> entityType){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, Component.empty(), 1.0f, parent, x, y, width, height, 0);
    }

    public static SinglePageCtor create(EntityType<? extends LivingEntity> entityType, Component description){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, description, 1.0f, parent, x, y, width, height, 0);
    }

    public static SinglePageCtor create(EntityType<? extends LivingEntity> entityType, Component description, float scale){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, description, scale, parent, x, y, width, height, 0);
    }

    public static SinglePageCtor create(EntityType<? extends LivingEntity> entityType, Component description, float scale, int yOffset){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, description, scale, parent, x, y, width, height, yOffset);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        drawHeader(entityType.getDescription(), guiGraphics, mouseX, mouseY, partialTick);
        if(this.description != null){
            drawParagraph(description, guiGraphics, x, y + 100, mouseX, mouseY, partialTick);
        }
        renderEntity(guiGraphics, entity, x + width - 10, y + height - 60, 0, this.scale * 75, -0.3f);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//
//        renderEntityInInventoryFollowsMouse(guiGraphics, x, y, x + width - 10,  y + height - 60, (int) (this.scale * 75), -0.3f, mouseX, mouseY, entity);

//        renderEntity(guiGraphics, entity, x + width - 10, y + height - 60, 0, this.scale * 75, -0.3f);
    }

    public static void renderEntityInInventoryFollowsMouse(
            GuiGraphics guiGraphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int scale,
            float yOffset,
            float mouseX,
            float mouseY,
            LivingEntity entity
    ) {
        float f = (float)(x1 + x2) / 2.0F;
        float f1 = (float)(y1 + y2) / 2.0F;
        float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
        float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));
        // Forge: Allow passing in direct angle components instead of mouse position
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(
            GuiGraphics p_282802_,
            int p_275688_,
            int p_275245_,
            int p_275535_,
            int p_294406_,
            int p_294663_,
            float p_275604_,
            float angleXComponent,
            float angleYComponent,
            LivingEntity p_275689_
    ) {
        float f = (float)(p_275688_ + p_275535_) / 2.0F;
        float f1 = (float)(p_275245_ + p_294406_) / 2.0F;
        p_282802_.enableScissor(p_275688_, p_275245_, p_275535_, p_294406_);
        float f2 = angleXComponent;
        float f3 = angleYComponent;
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf1 = new Quaternionf().rotateX(f3 * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf1);
        float f4 = p_275689_.yBodyRot;
        float f5 = p_275689_.getYRot();
        float f6 = p_275689_.getXRot();
        float f7 = p_275689_.yHeadRotO;
        float f8 = p_275689_.yHeadRot;
        p_275689_.yBodyRot = 180.0F + f2 * 20.0F;
        p_275689_.setYRot(180.0F + f2 * 40.0F);
        p_275689_.setXRot(-f3 * 20.0F);
        p_275689_.yHeadRot = p_275689_.getYRot();
        p_275689_.yHeadRotO = p_275689_.getYRot();
        float f9 = p_275689_.getScale();
        Vector3f vector3f = new Vector3f(0.0F, p_275689_.getBbHeight() / 2.0F + p_275604_ * f9, 0.0F);
        float f10 = (float)p_294663_ / f9;
        renderEntityInInventory(p_282802_, f, f1, f10, vector3f, quaternionf, quaternionf1, p_275689_);
        p_275689_.yBodyRot = f4;
        p_275689_.setYRot(f5);
        p_275689_.setXRot(f6);
        p_275689_.yHeadRotO = f7;
        p_275689_.yHeadRot = f8;
        p_282802_.disableScissor();
    }

    public static void renderEntityInInventory(
            GuiGraphics guiGraphics,
            float x,
            float y,
            float scale,
            Vector3f translate,
            Quaternionf pose,
            @Nullable Quaternionf cameraOrientation,
            LivingEntity entity
    ) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((double)x, (double)y, 50.0);
        guiGraphics.pose().scale(scale, scale, -scale);
        guiGraphics.pose().translate(translate.x, translate.y, translate.z);
        guiGraphics.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            entityrenderdispatcher.overrideCameraOrientation(cameraOrientation.conjugate(new Quaternionf()).rotateY((float) Math.PI));
        }

        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880));
        guiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }


    public static void renderEntity(GuiGraphics graphics, Entity entity, float x, float y, float rotation, float renderScale, float offset) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(x, y, 50);
        ms.scale(renderScale, renderScale, renderScale);
        ms.translate(0, offset, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(180));
        ms.mulPose(Axis.YP.rotationDegrees(rotation));
        EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        erd.setRenderShadow(false);
        erd.render(entity, 0, 0, 0, 0, 1, ms, immediate, 0xF000F0);
        erd.setRenderShadow(true);
        immediate.endBatch();
        ms.popPose();
    }
}
