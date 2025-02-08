package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

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
        DocClientUtils.blit(guiGraphics, DocAssets.IMAGE_FRAME, x + 4, y + 10);
        DocClientUtils.drawHeaderNoUnderline(entityType.getDescription(), guiGraphics, x, y, width, mouseX, mouseY, partialTick);
        if(this.description != null){
            DocClientUtils.drawParagraph(description, guiGraphics, x, y + 100, width, mouseX, mouseY, partialTick);
        }
        float entityWidth = entity.getBbWidth();
        float entityHeight = entity.getBbHeight();

        float entitySize = Math.max(1F, Math.max(entityWidth, entityHeight));

        float renderScale = 100F / entitySize * 0.6F * scale;
        float offset = Math.max(entityHeight, entitySize) * 0.5F + yOffset;
        renderEntity(guiGraphics, entity, x + (float) width /2, y + offset + 80, (float) ClientInfo.ticksInGame + partialTick, renderScale, 0);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static void renderEntity(GuiGraphics graphics, Entity entity, float x, float y, float rotation, float renderScale, float offset) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(x, y, 50);
        ms.scale(renderScale, renderScale, renderScale);
        ms.translate(0, offset, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(180));
        ms.mulPose(Axis.YP.rotationDegrees(rotation));
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        erd.setRenderShadow(false);
        erd.render(entity, 0, 0, 0, 0, 1, ms, immediate, 0xF000F0);
        erd.setRenderShadow(true);
        immediate.endBatch();
        ms.popPose();
        Lighting.setupFor3DItems();
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if(description != null) {
            object.addProperty(DocExporter.DESCRIPTION_PROPERTY, this.description.getString());
        }
        object.addProperty(DocExporter.ENTITY_PROPERTY,  BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }
}
