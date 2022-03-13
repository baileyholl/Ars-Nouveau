package com.hollingsworth.arsnouveau.client.gui.RadialMenu;

public classUtils{
public static void drawItemAsIcon(Item providedItem,PoseStack poseStack,int positionX,int positionY,int size){
        ItemRenderer itemRenderer=Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack=providedItem.getDefaultInstance();
        //Code stolen from ItemRenderer.renderGuiItem and changed to suit scaled items instead of fixing size to 16
        BakedModel itemBakedModel=itemRenderer.getModel(itemStack,null,null,0);

        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false,false);
        RenderSystem.setShaderTexture(0,TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        poseStack.pushPose();
        poseStack.translate(positionX,positionY,100.0F);
        poseStack.translate(8.0D,8.0D,0.0D);
        poseStack.scale(1.0F,-1.0F,1.0F);
        poseStack.scale(size,size,size);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource multibuffersource$buffersource=Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag=!itemBakedModel.usesBlockLight();
        if(flag){
        Lighting.setupForFlatItems();
        }

        itemRenderer.render(itemStack,ItemTransforms.TransformType.GUI,false,poseStack,multibuffersource$buffersource,15728880,OverlayTexture.NO_OVERLAY,itemBakedModel);
        multibuffersource$buffersource.endBatch();
        if(flag){
        Lighting.setupFor3DItems();
        }

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        }

public static void drawTextureFromResourceLocation(Object providedResourceLocation,PoseStack stack,int x,int y,int size){
        RenderSystem.setShaderTexture(0,(ResourceLocation)providedResourceLocation);
        GuiComponent.blit(stack,x,y,0,0,size,size,size,size);
        }
        }
